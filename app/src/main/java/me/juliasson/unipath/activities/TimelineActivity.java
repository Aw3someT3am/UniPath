package me.juliasson.unipath.activities;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.github.jinatonic.confetti.CommonConfetti;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.parse.ParseUser;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import me.juliasson.unipath.MyFirebaseMessagingService;
import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.DDCollegeListAdapter;
import me.juliasson.unipath.event.EventBus;
import me.juliasson.unipath.event.PageChangedEvent;
import me.juliasson.unipath.fragments.LinearTimelineFragment;
import me.juliasson.unipath.fragments.ProfileFragment;
import me.juliasson.unipath.fragments.SearchFragment;
import me.juliasson.unipath.internal.GetCollegeAddedToFavListInterface;
import me.juliasson.unipath.internal.GetCollegeUnlikedFromProfileInterface;
import me.juliasson.unipath.internal.GetCustomDeadlineAddedInterface;
import me.juliasson.unipath.internal.GetDeadlineCheckedInterface;
import me.juliasson.unipath.internal.GetDeadlineDeletedInterface;
import me.juliasson.unipath.internal.GetIsProgressCompleteInterface;
import me.juliasson.unipath.internal.NotificationInProfile;
import me.juliasson.unipath.internal.NotificationInterface;
import me.juliasson.unipath.internal.UpdateFavCollegeListCalendar;
import me.juliasson.unipath.internal.UpdateFavCollegeListLinearTimeline;
import me.juliasson.unipath.internal.UpdateFavCollegeListProfile;
import me.juliasson.unipath.internal.UpdateFavCollegeListSearchInterface;
import me.juliasson.unipath.internal.UpdateProfileProgressBarInterface;
import me.juliasson.unipath.model.Notify;
import me.juliasson.unipath.view.VerticalPager;

public class TimelineActivity extends AppCompatActivity implements
        GetCollegeAddedToFavListInterface,
        GetCustomDeadlineAddedInterface,
        GetDeadlineDeletedInterface,
        GetCollegeUnlikedFromProfileInterface,
        GetDeadlineCheckedInterface,
        GetIsProgressCompleteInterface,
        NotificationInterface {

    /**
     * Start page index. 0 - top page, 1 - central page, 2 - bottom page.
     */
    private static final int CENTRAL_PAGE_INDEX = 1;
    public VerticalPager mVerticalPager;
    private static boolean isFirstTime = false;

    private static UpdateFavCollegeListProfile updateFavCollegeListInterfaceProfile;
    private static UpdateFavCollegeListCalendar updateFavCollegeListInterfaceCalendar;
    private static UpdateFavCollegeListLinearTimeline updateFavCollegeListInterfaceLinearTimeline;
    private static UpdateProfileProgressBarInterface updateProfileProgressBarInterface;
    private static NotificationInProfile notificationInProfile;
    private static UpdateFavCollegeListSearchInterface updateFavCollegeListSearchInterface;
    private static final String TAG = "TimelineActivity";
    private int size;
    private int velocitySlow, velocityNormal, velocityFast;
    private Bitmap bitmap;
    private ArrayList<Notify> notifications;

    protected ViewGroup container;
    protected int goldDark, goldMed, gold, goldLight;
    protected int[] colors;
    private final int CONFETTI_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.toolbar_action_bar);

        SearchFragment.setCollegeListChangedInterface(this);
        NewDeadlineDialog.setCustomDeadlineInterface(this);
        DDCollegeListAdapter.setDeadlineDeletedInterface(this);
        ProfileFragment.setUnlikedFromProfileInterface(this);
        ProfileFragment.setIsProgressCompleteInterface(this);
        LinearTimelineFragment.setDcInterfaceFromTimelineActivity(this);
        MyFirebaseMessagingService.setNotificationInterface(this);
        MyFirebaseMessagingService.setTimelineActivity(this);

        findViews();
        initFCM();

        container = (ViewGroup) findViewById(R.id.container);

        goldDark = ContextCompat.getColor(this, R.color.holo_blue_bright);
        goldMed = ContextCompat.getColor(this, android.R.color.holo_red_light);
        gold = ContextCompat.getColor(this, android.R.color.holo_orange_light);
        goldLight = ContextCompat.getColor(this, android.R.color.holo_green_light);
        colors = new int[] { goldDark, goldMed, gold, goldLight };

        if (isFirstTime) {
            ProfileFragment.setIsFirstTime(true);
        }
    }

    private void findViews() {
        mVerticalPager = (VerticalPager) findViewById(R.id.activity_main_vertical_pager);
        initViews();
    }

    private void initViews() {
        snapPageWhenLayoutIsReady(mVerticalPager, CENTRAL_PAGE_INDEX);
    }

    @LayoutRes
    protected int getLayoutRes() {
        return R.layout.activity_timeline;
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

    //-----------------------Implementing interface-----------------------

    @Override
    public void getCollegeListChanged(boolean isChanged) {
        if (isChanged) {
            //tell calendar and profile fragments to refresh please
            updateFavCollegeListInterfaceProfile.updateList(true);
            updateFavCollegeListInterfaceCalendar.updateList(true);
        }
    }

    @Override
    public void getCustomDeadlineAdded(boolean added) {
        if (added) {
            //update linear timeline fragment and calendar
            updateFavCollegeListInterfaceCalendar.updateList(true);
            updateFavCollegeListInterfaceLinearTimeline.updateList(true);
        }
    }

    @Override
    public void getDeadlineDeleted(boolean deleted) {
        if (deleted) {
            updateFavCollegeListInterfaceCalendar.updateList(true);
        }
    }

    @Override
    public void getCollegeUnliked(boolean unliked) {
        if (unliked) {
            updateFavCollegeListInterfaceCalendar.updateList(true);
            updateFavCollegeListInterfaceLinearTimeline.updateList(true);
            updateFavCollegeListSearchInterface.updateList(true);
        }
    }

    @Override
    public void getDeadlineChecked(boolean isChanged) {
        if (isChanged) {
            updateProfileProgressBarInterface.updateProgressBar(true);
            updateFavCollegeListInterfaceCalendar.updateList(true);
        }
    }

    @Override
    public void isProgressComplete(boolean isComplete) {
        if (isComplete) {
            CommonConfetti.rainingConfetti(container, colors).stream(CONFETTI_DURATION);
        }
    }

    @Override
    public void setValues(ArrayList<Notify> notifications) {
        this.notifications = notifications;
        notificationInProfile.setNotifications(notifications);
        Log.d("TimelineActivity", Integer.toString(notifications.size()));
    }

    public static void updateFavCollegeListInterfaceProfile(UpdateFavCollegeListProfile listInterface) {
        updateFavCollegeListInterfaceProfile = listInterface;
    }

    public static void updateFavCollegeListInterfaceCalendar(UpdateFavCollegeListCalendar listInterface) {
        updateFavCollegeListInterfaceCalendar = listInterface;
    }

    public static void updateFavCollegeListInterfaceLinearTimeline(UpdateFavCollegeListLinearTimeline listInterface) {
        updateFavCollegeListInterfaceLinearTimeline = listInterface;
    }

    public static void updateProfileProgressBarInterface(UpdateProfileProgressBarInterface listInterface) {
        updateProfileProgressBarInterface = listInterface;
    }

    public static void updateNotifications(NotificationInProfile listInterface) {
        notificationInProfile = listInterface;
    }

    public static void updateFavCollegeListSearchInterface(UpdateFavCollegeListSearchInterface listInterface) {
        updateFavCollegeListSearchInterface = listInterface;
    }

    public static void setIsFirstTime(boolean firstTime) {
        isFirstTime = firstTime;
    }
}
