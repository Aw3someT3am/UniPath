package me.juliasson.unipath.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.parceler.Parcels;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.CollegeAdapter;
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
    LikeButton lbLikeButtonDetails;

    College college;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_details);
        this.setFinishOnTouchOutside(true);
        
        setSize();

        tvcollegeName = (TextView) findViewById(R.id.tvCollege);
        ivCollegeImage = (ImageView) findViewById(R.id.ivCollegeImage);
        college = (College) Parcels.unwrap(getIntent().getParcelableExtra(College.class.getSimpleName()));
        lbLikeButtonDetails = (LikeButton) findViewById(R.id.lbLikeButtonDetails);

        lbLikeButtonDetails.setLiked(false);

        lbLikeButtonDetails.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                CollegeAdapter.addUserCollegeRelation(college);
                addUserDeadlineRelations(college);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                removeUserCollegeRelation(college);
                removeUserDeadlinesRelation(college);
            }
        });

        tvcollegeName.setText(college.getCollegeName());

        Glide.with(this)
                .load(college.getCollegeImage().getUrl())
                .into(ivCollegeImage);

        Bundle args = new Bundle();
        args.putParcelable(College.class.getSimpleName(), college);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();

        generalInfoFragmnet = new GeneralInfoFragment();
        deadlinesFragmnet = new DeadlineFragment();

        generalInfoFragmnet.setArguments(args);
        deadlinesFragmnet.setArguments(args);

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

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, (6 * height)/7);
    }
}
