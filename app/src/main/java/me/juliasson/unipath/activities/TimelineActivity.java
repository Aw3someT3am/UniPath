package me.juliasson.unipath.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import me.juliasson.unipath.R;
import me.juliasson.unipath.fragments.CalendarFragment;
import me.juliasson.unipath.fragments.LinearTimelineFragment;
import me.juliasson.unipath.fragments.ProfileFragment;
import me.juliasson.unipath.fragments.SearchFragment;
import me.juliasson.unipath.internal.Refreshable;


public class TimelineActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener
         {

    private static final String TAG = TimelineActivity.class.getSimpleName();

    public static void start(Context context) {
        final Intent intent = new Intent(context, TimelineActivity.class);
        context.startActivity(intent);
    }

    private ViewPager homePager;
    private BottomNavigationView navigationView;

    private LinearTimelineFragment fragment_linear_timeline = new LinearTimelineFragment();
    private SearchFragment fragment_search = new SearchFragment();
    private ProfileFragment fragment_profile = new ProfileFragment();
    private CalendarFragment fragment_calendar = new CalendarFragment();

    private HomeAdapter homeAdapter;

    private final Fragment[] fragments = new Fragment[] {
            fragment_linear_timeline,
            fragment_calendar,
            fragment_profile //DO NOT CHANGE WITH OTHER FRAGMENTS.
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        navigationView = findViewById(R.id.bottom_nav);
        navigationView.setSelectedItemId(R.id.action_post);
        navigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setOnNavigationItemReselectedListener(this);

        homePager = findViewById(R.id.home_pager);
        homePager.setOffscreenPageLimit(2);


        homeAdapter = new HomeAdapter(getSupportFragmentManager(), fragments);
        homePager.setAdapter(homeAdapter);
        homePager.setCurrentItem(1);

        homePager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if(navigationView.getSelectedItemId() != R.id.action_home) {
                            navigationView.setSelectedItemId(R.id.action_home);
                            getSupportActionBar().setElevation(
                                    getResources().getDimensionPixelSize(R.dimen.action_bar_elevation)
                            );
                        }
                        break;
                    case 1:
                        if(navigationView.getSelectedItemId() != R.id.action_post) {
                            navigationView.setSelectedItemId(R.id.action_post);
                            getSupportActionBar().setElevation(0);
                        }
                        break;
                    case 2:
                        if (navigationView.getSelectedItemId() != R.id.action_profile) {
                            navigationView.setSelectedItemId(R.id.action_profile);
                            fragment_profile.setPbProgress();
                            getSupportActionBar().setElevation(0);
                        }
                        break;
                    default:

                        break;
                }
            }
        });
        setContentView(R.layout.activity_timeline);

        navigationView = findViewById(R.id.bottom_nav);
        navigationView.setSelectedItemId(R.id.action_post);
        navigationView.setOnNavigationItemSelectedListener(this);
        navigationView.setOnNavigationItemReselectedListener(this);

        homePager = findViewById(R.id.home_pager);
        homePager.setOffscreenPageLimit(2);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
//                        Log.d(TAG, msg);
//                        Toast.makeText(TimelineActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

        homeAdapter = new HomeAdapter(getSupportFragmentManager(), fragments);
        homePager.setAdapter(homeAdapter);
        homePager.setCurrentItem(1);

        homePager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if(navigationView.getSelectedItemId() != R.id.action_home) {
                            navigationView.setSelectedItemId(R.id.action_home);
                            getSupportActionBar().setElevation(
                                    getResources().getDimensionPixelSize(R.dimen.action_bar_elevation)
                            );
                        }
                        break;
                    case 1:
                        if(navigationView.getSelectedItemId() != R.id.action_post) {
                            navigationView.setSelectedItemId(R.id.action_post);
                            getSupportActionBar().setElevation(0);
                        }
                        break;
                    case 2:
                        if (navigationView.getSelectedItemId() != R.id.action_profile) {
                            navigationView.setSelectedItemId(R.id.action_profile);
                            fragment_profile.setPbProgress();
                            getSupportActionBar().setElevation(0);
                        }
                        break;
                    default:

                        break;
                }
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Log.d(TAG, "home selected.");
                homePager.setCurrentItem(0);
                return true;
            case R.id.action_post:
                Log.d(TAG, "post selected.");
                homePager.setCurrentItem(1);
                return true;
            case R.id.action_profile:
                Log.d(TAG, "profile selected.");
                homePager.setCurrentItem(2);
                fragment_profile.setPbProgress();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                Log.d(TAG, "home re-selected.");
                final Refreshable fragment = (Refreshable)fragments[1];
                fragment.onRefresh();
                break;
            case R.id.action_post:
                Log.d(TAG, "post re-selected.");
                break;
            case R.id.action_profile:
                Log.d(TAG, "profile re-selected.");
                fragment_profile.setPbProgress();
                break;
            default:
                break;
        }
    }

    static class HomeAdapter extends FragmentStatePagerAdapter {
        private final Fragment[] fragments;

        public HomeAdapter(FragmentManager fm, Fragment[] fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
