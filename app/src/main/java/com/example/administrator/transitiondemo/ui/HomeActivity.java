package com.example.administrator.transitiondemo.ui;

import android.app.Activity;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;


import com.example.administrator.transitiondemo.R;
import com.example.administrator.transitiondemo.data.DataLoadingSubject;
import com.example.administrator.transitiondemo.data.DataManager;
import com.example.administrator.transitiondemo.data.PlaidItem;
import com.example.administrator.transitiondemo.ui.adpter.FeedAdapter;
import com.example.administrator.transitiondemo.ui.recyclerview.GridItemDividerDecoration;
import com.example.administrator.transitiondemo.ui.recyclerview.LoadMoreListener;
import com.example.administrator.transitiondemo.ui.recyclerview.SlideInItemAnimator;
import com.example.administrator.transitiondemo.util.AnimUtils;
import com.example.administrator.transitiondemo.util.ViewUtils;

import java.util.List;

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
    private TextView noFiltersEmptyText;

    private FeedAdapter adapter;
    private GridLayoutManager layoutManager;
    private DataManager dataManager;

    int columns = 2;

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

        iniData();
        iniAdapter();
        initView();
    }

    void iniTransitionCallBack() {
        setExitSharedElementCallback(FeedAdapter.createSharedElementReenterCallback(this));
    }

    private void iniData() {
        dataManager = new DataManager() {
            @Override
            public void onDataLoaded(List<? extends PlaidItem> data) {
                adapter.addAndResort(data);
                checkEmptyState();
            }
        };
        dataManager.registerCallback(new DataLoadingSubject.DataLoadingCallbacks() {
            @Override
            public void dataStartedLoading() {

            }

            @Override
            public void dataFinishedLoading() {
                checkEmptyState();
            }
        });
        dataManager.loadAllDataSources();
    }

    private void iniAdapter() {
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

        adapter = new FeedAdapter(this, dataManager, columns, true);

    }

    private void initView() {
        grid.setAdapter(adapter);
        layoutManager = new GridLayoutManager(this, columns);
        // 设置边距
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemColumnSpan(position);
            }
        });
        grid.setLayoutManager(layoutManager);
        grid.addOnScrollListener(toolbarElevation);
        grid.addOnScrollListener(new LoadMoreListener(layoutManager, dataManager) {
            @Override
            public void onLoadMore() {
                dataManager.loadAllDataSources();
            }
        });
        grid.setHasFixedSize(true);
        grid.addItemDecoration(new GridItemDividerDecoration(R.dimen.divider_height,
                ContextCompat.getColor(this, R.color.divider)));
        grid.setItemAnimator(new SlideInItemAnimator());

        drawer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // inset the toolbar down by the status bar height
                ViewGroup.MarginLayoutParams lpToolbar = (ViewGroup.MarginLayoutParams) toolbar
                        .getLayoutParams();
                lpToolbar.topMargin += insets.getSystemWindowInsetTop();
                lpToolbar.leftMargin += insets.getSystemWindowInsetLeft();
                lpToolbar.rightMargin += insets.getSystemWindowInsetRight();
                toolbar.setLayoutParams(lpToolbar);

                // inset the grid top by statusbar+toolbar & the bottom by the navbar (don't clip)
                grid.setPadding(
                        grid.getPaddingLeft() + insets.getSystemWindowInsetLeft(), // landscape
                        insets.getSystemWindowInsetTop() + ViewUtils.getActionBarSize
                                (HomeActivity.this),
                        grid.getPaddingRight() + insets.getSystemWindowInsetRight(), // landscape
                        grid.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                // inset the fab for the navbar
                ViewGroup.MarginLayoutParams lpFab = (ViewGroup.MarginLayoutParams) fab
                        .getLayoutParams();
                lpFab.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
                lpFab.rightMargin += insets.getSystemWindowInsetRight(); // landscape
                fab.setLayoutParams(lpFab);

                View postingStub = findViewById(R.id.stub_posting_progress);
                ViewGroup.MarginLayoutParams lpPosting =
                        (ViewGroup.MarginLayoutParams) postingStub.getLayoutParams();
                lpPosting.bottomMargin += insets.getSystemWindowInsetBottom(); // portrait
                lpPosting.rightMargin += insets.getSystemWindowInsetRight(); // landscape
                postingStub.setLayoutParams(lpPosting);

                // we place a background behind the status bar to combine with it's semi-transparent
                // color to get the desired appearance.  Set it's height to the status bar height
                View statusBarBackground = findViewById(R.id.status_bar_background);
                FrameLayout.LayoutParams lpStatus = (FrameLayout.LayoutParams)
                        statusBarBackground.getLayoutParams();
                lpStatus.height = insets.getSystemWindowInsetTop();
                statusBarBackground.setLayoutParams(lpStatus);

                // inset the filters list for the status bar / navbar
                // need to set the padding end for landscape case
                final boolean ltr = filtersList.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
                filtersList.setPaddingRelative(filtersList.getPaddingStart(),
                        filtersList.getPaddingTop() + insets.getSystemWindowInsetTop(),
                        filtersList.getPaddingEnd() + (ltr ? insets.getSystemWindowInsetRight() :
                                0),
                        filtersList.getPaddingBottom() + insets.getSystemWindowInsetBottom());

                // clear this listener so insets aren't re-applied
                drawer.setOnApplyWindowInsetsListener(null);

                return insets.consumeSystemWindowInsets();
            }
        });
        setupTaskDescription();
    }

    private void setupTaskDescription() {
        // set a silhouette icon in overview as the launcher icon is a bit busy
        // and looks bad on top of colorPrimary
        //Bitmap overviewIcon = ImageUtils.vectorToBitmap(this, R.drawable.ic_launcher_silhouette);
        // TODO replace launcher icon with a monochrome version from RN.
        Bitmap overviewIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        setTaskDescription(new ActivityManager.TaskDescription(getString(R.string.app_name),
                overviewIcon,
                ContextCompat.getColor(this, R.color.primary)));
        overviewIcon.recycle();
    }

    private RecyclerView.OnScrollListener toolbarElevation =
            new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                    if (newState == RecyclerView.SCROLL_STATE_IDLE
//                            && layoutManager.findFirstVisibleItemPosition() == 0
//                            && layoutManager.findViewByPosition(0).getTop() == grid.getPaddingTop()
//                            && toolbar.getTranslationZ() != 0) {
//                        toolbar.setTranslationZ(0f);
//                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING
//                            && toolbar.getTranslationZ() != -1f) {
//                        toolbar.setTranslationZ(-1f);
//                    }
                }
            };

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

    /**
     * 检查
     */
    private void checkEmptyState() {
        if (adapter.getDataItemCount() == 0) {
            loading.setVisibility(View.GONE);
            setNoFiltersEmptyTextVisibility(View.VISIBLE);
            toolbar.setTranslationZ(0f);
        } else {
            loading.setVisibility(View.GONE);
            setNoFiltersEmptyTextVisibility(View.GONE);
        }
    }

    private void setNoFiltersEmptyTextVisibility(int visibility) {
        if (visibility == View.VISIBLE) {
            if (noFiltersEmptyText == null) {
                // create the no filters empty text
                ViewStub stub = (ViewStub) findViewById(R.id.stub_no_filters);
                noFiltersEmptyText = (TextView) stub.inflate();
                String emptyText = getString(R.string.no_filters_selected);
                int filterPlaceholderStart = emptyText.indexOf('\u08B4');
                int altMethodStart = filterPlaceholderStart + 3;
                SpannableStringBuilder ssb = new SpannableStringBuilder(emptyText);
                // show an image of the filter icon
                ssb.setSpan(new ImageSpan(this, R.drawable.ic_filter_small,
                                ImageSpan.ALIGN_BASELINE),
                        filterPlaceholderStart,
                        filterPlaceholderStart + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // make the alt method (swipe from right) less prominent and italic
                ssb.setSpan(new ForegroundColorSpan(
                                ContextCompat.getColor(this, R.color.text_secondary_light)),
                        altMethodStart,
                        emptyText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new StyleSpan(Typeface.ITALIC),
                        altMethodStart,
                        emptyText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                noFiltersEmptyText.setText(ssb);
                noFiltersEmptyText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawer.openDrawer(GravityCompat.END);
                    }
                });
            }
            noFiltersEmptyText.setVisibility(visibility);
        } else if (noFiltersEmptyText != null) {
            noFiltersEmptyText.setVisibility(visibility);
        }

    }
}
