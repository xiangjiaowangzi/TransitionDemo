package com.example.administrator.transitiondemo.data;

import com.example.administrator.transitiondemo.R;
import com.example.administrator.transitiondemo.data.api.MaterialImageConverter;
import com.example.administrator.transitiondemo.data.api.MaterialImageService;
import com.example.administrator.transitiondemo.data.api.mdoel.Shot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by LiuB on 2017/10/26.
 */

public abstract class DataManager implements DataLoadingSubject{

    private final AtomicInteger loadingCount;
    private MaterialImageService dribbbleSearchApi;
    private List<DataLoadingSubject.DataLoadingCallbacks> loadingCallbacks;
    private Map<String, Call> inflight;
    private int curPage;

    public DataManager() {
        loadingCount = new AtomicInteger(0);

    }

    @Override
    public boolean isDataLoading() {
        return loadingCount.get() > 0;
    }

    @Override
    public void registerCallback(DataLoadingSubject.DataLoadingCallbacks callback) {
        if (loadingCallbacks == null) {
            loadingCallbacks = new ArrayList<>(1);
        }
        loadingCallbacks.add(callback);
    }

    @Override
    public void unregisterCallback(DataLoadingSubject.DataLoadingCallbacks callback) {
        if (loadingCallbacks != null && loadingCallbacks.contains(callback)) {
            loadingCallbacks.remove(callback);
        }
    }

    public MaterialImageService getDribbbleSearchApi() {
        if (dribbbleSearchApi == null) createDribbbleSearchApi();
        return dribbbleSearchApi;
    }

    private void createDribbbleSearchApi() {
        dribbbleSearchApi = new Retrofit.Builder()
                .baseUrl(MaterialImageService.ENDPOINT)
                .addConverterFactory(new MaterialImageConverter.Factory())
                .build()
                .create((MaterialImageService.class));
    }

    private void loadDribbbleSearch(final int page) {
        String query = "Material Design";
        final Call<List<Shot>> searchCall = getDribbbleSearchApi().search(query, page,
                MaterialImageService.PER_PAGE_DEFAULT, MaterialImageService.SORT_RECENT);
        searchCall.enqueue(new Callback<List<Shot>>() {
            @Override
            public void onResponse(Call<List<Shot>> call, Response<List<Shot>> response) {
                if (response.isSuccessful()) {
                    sourceLoaded(response.body(), page);
                } else {
                    loadFailed("");
                }
            }

            @Override
            public void onFailure(Call<List<Shot>> call, Throwable t) {
                loadFailed("");
            }
        });
    }

    private void sourceLoaded(List<? extends PlaidItem> data, int page) {
        loadFinished();
        if (data != null && !data.isEmpty()) {
            onDataLoaded(data);
        }
    }

    public abstract void onDataLoaded(List<? extends PlaidItem> data);

    public void loadAllDataSources() {
        curPage++;
        loadDribbbleSearch(curPage);
    }

    private void loadFailed(String key) {
        loadFinished();
//        inflight.remove(key);
    }

    protected void loadFinished() {
        if (0 == loadingCount.decrementAndGet()) {
            dispatchLoadingFinishedCallbacks();
        }
    }

    protected void dispatchLoadingFinishedCallbacks() {
        if (loadingCallbacks == null || loadingCallbacks.isEmpty()) return;
        for (DataLoadingSubject.DataLoadingCallbacks loadingCallback : loadingCallbacks) {
            loadingCallback.dataFinishedLoading();
        }
    }


}
