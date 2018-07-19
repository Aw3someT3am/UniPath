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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.juliasson.unipath.R;
import me.juliasson.unipath.fragments.DeadlineFragment;
import me.juliasson.unipath.fragments.GeneralInfoFragment;
import me.juliasson.unipath.model.College;

public class CollegeDetailsActivity extends AppCompatActivity {
    FrameLayout flContainer;
    FragmentTransaction fragmentTransaction;

    Fragment generalInfoFragmnet;
    Fragment deadlinesFragmnet;

    TextView tvcollegeName;
    ImageView ivCollegeImage;

    College college;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_details);
        this.setFinishOnTouchOutside(true);

//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//        int width = dm.widthPixels;
//        int height =  dm.heightPixels;
//
//        getWindow().setLayout((int) (width*.8), (int)(height*.6));
        tvcollegeName = (TextView) findViewById(R.id.tvCollege);
        ivCollegeImage = (ImageView) findViewById(R.id.ivCollegeImage);
        college = (College) Parcels.unwrap(getIntent().getParcelableExtra(College.class.getSimpleName()));

        tvcollegeName.setText(college.getCollegeName());
        int radius = 30; // corner radius, higher value = more rounded
        int margin = 10; // crop margin, set to 0 for corners with no crop
        Glide.with(this)
                .load(college.getCollegeImage())
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(radius, margin)))
                .into(ivCollegeImage);

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
