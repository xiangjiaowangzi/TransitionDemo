package com.example.administrator.transitiondemo.ui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;


import com.example.administrator.transitiondemo.R;
import com.example.administrator.transitiondemo.data.api.pref.SourceManager;
import com.example.administrator.transitiondemo.ui.adpter.FeedAdapter;
import com.example.administrator.transitiondemo.ui.adpter.FilterAdapter;
import com.example.administrator.transitiondemo.util.AnimUtils;

import javax.xml.transform.Source;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lbin on 2017/10/19.
 */

public class HomeActivity extends Activity {

    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.grid)
    RecyclerView grid;
    @BindView(R.id.fab)
    ImageButton fab;
    @BindView(R.id.filters)
    RecyclerView filtersList;
    @BindView(android.R.id.empty)
    ProgressBar loading;

//    private DesignerNewsPrefs designerNewsPrefs;
//    private DribbblePrefs dribbblePrefs;

    FilterAdapter filtersAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        iniUi(savedInstanceState);
    }

    void iniUi(Bundle savedInstanceState) {
        drawer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        setActionBar(toolbar);
        if (savedInstanceState == null) {
            animateToolbar();
        }
        iniTransitionCallBack();

//        dribbblePrefs = DribbblePrefs.get(this);
//        designerNewsPrefs = DesignerNewsPrefs.get(this);

//        iniAdapter();
    }

    void iniTransitionCallBack() {
        setExitSharedElementCallback(FeedAdapter.createSharedElementReenterCallback(this));
    }

//    private void iniAdapter() {
//        filtersAdapter = new FilterAdapter(this, SourceManager.getSources(this),
//                new FilterAdapter.FilterAuthoriser() {
//                    @Override
//                    public void requestDribbbleAuthorisation(View sharedElement, Source forSource) {
//                        Intent login = new Intent(HomeActivity.this, DribbbleLogin.class);
//                        MorphTransform.addExtras(login,
//                                ContextCompat.getColor(HomeActivity.this, R.color.background_dark),
//                                sharedElement.getHeight() / 2);
//                        ActivityOptions options =
//                                ActivityOptions.makeSceneTransitionAnimation(HomeActivity.this,
//                                        sharedElement, getString(R.string.transition_dribbble_login));
//                        startActivityForResult(login,
//                                getAuthSourceRequestCode(forSource), options.toBundle());
//                    }
//                });
//    }


    /**
     * Toolbar 动画
     */
    private void animateToolbar() {
        View t = toolbar.getChildAt(0);
        if (t != null && t instanceof TextView) {
            TextView title = (TextView) t;
            title.setAlpha(0f);
            title.setScaleX(0.8f);
            title.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setStartDelay(300)
                    .setDuration(900)
                    .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
        }
    }


    /**
     * 显示Fab
     */
    private void showFab() {
        fab.setAlpha(0f);
        fab.setScaleX(0f);
        fab.setScaleY(0f);
        fab.setTranslationY(fab.getHeight() / 2);
        fab.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(300L)
                .setInterpolator(AnimUtils.getLinearOutSlowInInterpolator(this))
                .start();
    }

}
