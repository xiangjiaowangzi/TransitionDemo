package com.example.administrator.transitiondemo.data.api;

import android.text.TextUtils;

import com.example.administrator.transitiondemo.data.api.mdoel.Images;
import com.example.administrator.transitiondemo.data.api.mdoel.Shot;
import com.example.administrator.transitiondemo.data.api.mdoel.User;
import com.example.administrator.transitiondemo.util.LogUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by LiuB on 2017/10/26.
 */

public class MaterialImageConverter implements Converter<ResponseBody, List<Shot>> {

    public static final class Factory extends Converter.Factory{
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                                Annotation[] annotations, Retrofit retrofit) {
            return INSTANCE;
        }
    }

    private MaterialImageConverter(){}

    static final MaterialImageConverter INSTANCE = new MaterialImageConverter();
    private static final String HOST = "https://dribbble.com";
    private static final Pattern PATTERN_PLAYER_ID =
            Pattern.compile("users/(\\d+?)/", Pattern.DOTALL);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM d, yyyy");


    @Override
    public List<Shot> convert(ResponseBody value) throws IOException {
//        LogUtil.log("value " + value.string());
        final Elements shotElements =
                Jsoup.parse(value.string(), HOST).select("li[id^=screenshot]");
        final List<Shot> shots = new ArrayList<>(shotElements.size());
        for (Element element : shotElements) {
            final Shot shot = parseShot(element, DATE_FORMAT);
            if (shot != null) {
                shots.add(shot);
            }
        }
        return shots;
    }

    private static Shot parseShot(Element element, SimpleDateFormat dateFormat) {
        final Element descriptionBlock = element.select("a.dribbble-over").first();
        // API responses wrap description in a <p> tag. Do the same for consistent display.
        String description = descriptionBlock.select("span.comment").text().trim();
        LogUtil.log("description : " + description);
        if (!TextUtils.isEmpty(description)) {
            description = "<p>" + description + "</p>";
        }
        String imgUrl = element.select("img").first().attr("src");
        if (imgUrl.contains("_teaser.")) {
            imgUrl = imgUrl.replace("_teaser.", ".");
        }
        Date createdAt = null;
        try {
            createdAt = dateFormat.parse(descriptionBlock.select("em.timestamp").first().text());
        } catch (ParseException e) { }

        return new Shot.Builder()
                .setId(Long.parseLong(element.id().replace("screenshot-", "")))
                .setHtmlUrl(HOST + element.select("a.dribbble-link").first().attr("href"))
                .setTitle(descriptionBlock.select("strong").first().text())
                .setDescription(description)
                .setImages(new Images(null, imgUrl, null))
                .setAnimated(element.select("div.gif-indicator").first() != null)
                .setCreatedAt(createdAt)
                .setLikesCount(Long.parseLong(element.select("li.fav").first().child(0).text()
                        .replaceAll(",", "")))
                .setCommentsCount(Long.parseLong(element.select("li.cmnt").first().child(0).text
                        ().replaceAll(",", "")))
                .setViewsCount(Long.parseLong(element.select("li.views").first().child(0)
                        .text().replaceAll(",", "")))
                .setUser(parsePlayer(element.select("h2").first()))
                .build();
    }

    private static User parsePlayer(Element element) {
        final Element userBlock = element.select("a.url").first();
        String avatarUrl = userBlock.select("img.photo").first().attr("src");
        if (avatarUrl.contains("/mini/")) {
            avatarUrl = avatarUrl.replace("/mini/", "/normal/");
        }
        final Matcher matchId = PATTERN_PLAYER_ID.matcher(avatarUrl);
        Long id = -1l;
        if (matchId.find() && matchId.groupCount() == 1) {
            id = Long.parseLong(matchId.group(1));
        }
        final String slashUsername = userBlock.attr("href");
        final String username =
                TextUtils.isEmpty(slashUsername) ? null : slashUsername.substring(1);
        return new User.Builder()
                .setId(id)
                .setName(userBlock.text())
                .setUsername(username)
                .setHtmlUrl(HOST + slashUsername)
                .setAvatarUrl(avatarUrl)
                .setPro(element.select("span.badge-pro").size() > 0)
                .build();
    }
}
