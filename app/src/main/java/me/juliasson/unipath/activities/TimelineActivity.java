package me.juliasson.unipath.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.squareup.otto.Subscribe;

import me.juliasson.unipath.R;
import me.juliasson.unipath.event.EventBus;
import me.juliasson.unipath.event.PageChangedEvent;
import me.juliasson.unipath.view.VerticalPager;


public class TimelineActivity extends AppCompatActivity {

    /**
     * Start page index. 0 - top page, 1 - central page, 2 - bottom page.
     */
    private static final int CENTRAL_PAGE_INDEX = 1;
    public VerticalPager mVerticalPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        findViews();

    }

    private void findViews() {
        mVerticalPager = (VerticalPager) findViewById(R.id.activity_main_vertical_pager);
        initViews();
    }

    private void initViews() {
        snapPageWhenLayoutIsReady(mVerticalPager, CENTRAL_PAGE_INDEX);
    }

    private void snapPageWhenLayoutIsReady(final View pageView, final int page) {
        /*
         * VerticalPager is not fully initialized at the moment, so we want to snap to the central page only when it
         * layout and measure all its pages.
         */
        pageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                mVerticalPager.snapToPage(page, VerticalPager.PAGE_SNAP_DURATION_INSTANT);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                    // recommended removeOnGlobalLayoutListener method is available since API 16 only
                    pageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    removeGlobalOnLayoutListenerForJellyBean(pageView);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            private void removeGlobalOnLayoutListenerForJellyBean(final View pageView) {
                pageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        EventBus.getInstance().unregister(this);
        super.onPause();
    }

    @Subscribe
    public void onLocationChanged(PageChangedEvent event) {
        mVerticalPager.setPagingEnabled(event.hasVerticalNeighbors());
    }


}
