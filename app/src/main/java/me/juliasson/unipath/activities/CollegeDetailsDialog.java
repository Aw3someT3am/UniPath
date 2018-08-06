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
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
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

import me.juliasson.unipath.fragments.CalculatorFragment;
import me.juliasson.unipath.internal.LikesInterface;
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
    Fragment netCalculatorFragmnet;

    TextView tvcollegeName;
    ImageView ivCollegeImage;
    CollegeAdapter collegeAdapter = new CollegeAdapter(new ArrayList<College>());
    LikeButton lbLikeButtonDetails;

    private static LikesInterface likesInterface;

    private boolean isChanged = false;
    College college;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_college_details);
        this.setFinishOnTouchOutside(true);
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

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
                isChanged = true;
                CollegeAdapter.addUserCollegeRelation(college);
                CollegeAdapter.addUserDeadlineRelations(college);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                isChanged = true;
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
        netCalculatorFragmnet = new CalculatorFragment();

        generalInfoFragmnet.setArguments(args);
        deadlinesFragmnet.setArguments(args);
        netCalculatorFragmnet.setArguments(args);

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
                    case R.id.fragment_calculator:
                        fragmentTransaction.replace(R.id.flContainer, netCalculatorFragmnet).commit();
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
        ucQuery.whereEqualTo("college", college);
        ucQuery.whereEqualTo("user", ParseUser.getCurrentUser());

        ucQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        lbLikeButtonDetails.setLiked(true);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void setLiked(LikesInterface inter) {
        likesInterface = inter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            likesInterface.setValues(isChanged);
            finish();
        }
        return super.onTouchEvent(event);
    }
}
