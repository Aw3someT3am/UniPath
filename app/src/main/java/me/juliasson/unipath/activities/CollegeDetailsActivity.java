package me.juliasson.unipath.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import me.juliasson.unipath.R;
import me.juliasson.unipath.fragments.DeadlineFragment;
import me.juliasson.unipath.fragments.GeneralInfoFragment;

public class CollegeDetailsActivity extends AppCompatActivity {
    FrameLayout flContainer;
    FragmentTransaction fragmentTransaction;

    Fragment generalInfoFragmnet;
    Fragment deadlinesFragmnet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_details);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();

        generalInfoFragmnet = new GeneralInfoFragment();
        deadlinesFragmnet = new DeadlineFragment();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, generalInfoFragmnet).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.fragment_info:
                        fragmentTransaction.replace(R.id.flContainer, generalInfoFragmnet).commit();
                        return true;
                    case R.id.fragment_deadline:
                        fragmentTransaction.replace(R.id.flContainer, deadlinesFragmnet).commit();
                        return true;
                }
                return true;
            }
        });
    }
}
