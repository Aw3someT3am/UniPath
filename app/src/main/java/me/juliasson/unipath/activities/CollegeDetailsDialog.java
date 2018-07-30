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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.CollegeAdapter;
import me.juliasson.unipath.fragments.DeadlineFragment;
import me.juliasson.unipath.fragments.GeneralInfoFragment;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.UserCollegeRelation;

public class CollegeDetailsDialog extends AppCompatActivity {
    FrameLayout flContainer;
    FragmentTransaction fragmentTransaction;

    Fragment generalInfoFragmnet;
    Fragment deadlinesFragmnet;

    TextView tvcollegeName;
    ImageView ivCollegeImage;
    CollegeAdapter collegeAdapter = new CollegeAdapter(new ArrayList<College>());
    LikeButton lbLikeButtonDetails;

    College college;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_college_details);
        this.setFinishOnTouchOutside(true);
        
        setSize();

        tvcollegeName = (TextView) findViewById(R.id.tvCollege);
        ivCollegeImage = (ImageView) findViewById(R.id.ivCollegeImage);
        college = (College) Parcels.unwrap(getIntent().getParcelableExtra(College.class.getSimpleName()));
        lbLikeButtonDetails = (LikeButton) findViewById(R.id.lbLikeButtonDetails);

        lbLikeButtonDetails.setLiked(false);

        loadCollege(college);

        lbLikeButtonDetails.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                CollegeAdapter.addUserCollegeRelation(college);
                CollegeAdapter.addUserDeadlineRelations(college);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                CollegeAdapter.removeUserCollegeRelation(college);
                CollegeAdapter.removeUserDeadlinesRelation(college);
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

    /**
     * Grabs all of the "liked" colleges related to the user.
     * @param college the college being deemed "liked" or "not liked"
     */
    private void loadCollege(final College college) {
        UserCollegeRelation.Query ucQuery = new UserCollegeRelation.Query();
        ucQuery.getTop().withUser().withCollege();

        ucQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        UserCollegeRelation relation = objects.get(i);
                        if(relation.getCollege().getObjectId().equals(college.getObjectId()) &&
                                relation.getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
                            lbLikeButtonDetails.setLiked(true);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
