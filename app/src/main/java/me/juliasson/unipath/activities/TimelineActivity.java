package me.juliasson.unipath.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.ParseUser;
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

    private static final String TAG = "TimelineActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        findViews();

        initFCM();
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

    private void initFCM(){
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "initFCM: token: " + token);
        sendRegistrationToServer(token);

    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "sendRegistrationToServer: sending token to server: " + token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        String uid = "";
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                //wait
            }
            else{
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
        }
        else{
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        //uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference.child(getString(R.string.dbnode_users))
                .child(uid)
                .child(getString(R.string.field_messaging_token))
                .setValue(token);
        reference.child(getString(R.string.dbnode_users))
                .child(uid)
                .child("username")
                .setValue(ParseUser.getCurrentUser().getUsername());
    }

}
