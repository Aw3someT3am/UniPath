package me.juliasson.unipath.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.yarolegovich.discretescrollview.DSVOrientation;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.LoginActivity;
import me.juliasson.unipath.activities.MapActivity;
import me.juliasson.unipath.activities.NewDeadlineDialog;
import me.juliasson.unipath.activities.TimelineActivity;
import me.juliasson.unipath.adapters.CollegeAdapter;
import me.juliasson.unipath.internal.GetCollegeUnlikedFromProfileAdapterInterface;
import me.juliasson.unipath.internal.GetCollegeUnlikedFromProfileInterface;
import me.juliasson.unipath.internal.UpdateFavCollegeListProfile;
import me.juliasson.unipath.internal.UpdateProfileProgressBarInterface;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.UserCollegeRelation;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.Constants;
import me.juliasson.unipath.utils.GalleryUtils;

public class ProfileFragment extends Fragment implements
        UpdateFavCollegeListProfile,
        GetCollegeUnlikedFromProfileAdapterInterface,
        UpdateProfileProgressBarInterface {

    private TextView tvProgressLabel;
    private ProgressBar pbProgress;
    private ImageView ivProfileImage;
    private TextView tvProgressText;
    private TextView tvFirstName;
    private TextView tvEmail;
    private ImageView ivForward;
    private ImageView ivBack;
    private ImageView bvFavoritesMap;
    private DiscreteScrollView scrollView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static GetCollegeUnlikedFromProfileInterface unlikedFromProfileInterface;

    ViewPager pager;
    TimelineActivity mTimelineActivity;

    private CollegeAdapter collegeAdapter;
    private static ArrayList<College> colleges;

    private static final String TAG = "ProfileFragment";

    private final static int GALLERY_IMAGE_SELECTION_REQUEST_CODE = 2034;
    private String filePath;
    private Context mContext;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        pager = (ViewPager) parent;
        setHasOptionsMenu(true);
        mTimelineActivity=(TimelineActivity) getActivity();
        TimelineActivity.updateFavCollegeListInterfaceProfile(this);
        TimelineActivity.updateProfileProgressBarInterface(this);

        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mContext = view.getContext();

        //settings up general profile info
        tvProgressLabel = view.findViewById(R.id.tvProgressLabel);
        pbProgress = view.findViewById(R.id.pbProgress);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvProgressText = view.findViewById(R.id.tvProgressText);
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvEmail = view.findViewById(R.id.tvEmail);
        bvFavoritesMap = view.findViewById(R.id.mapFavorites);
        ivForward = view.findViewById(R.id.ivForward);
        ivBack = view.findViewById(R.id.ivBack);

        scrollView = view.findViewById(R.id.picker);
        scrollView.setOrientation(DSVOrientation.HORIZONTAL);


        colleges = new ArrayList<>();
        collegeAdapter = new CollegeAdapter(colleges, this);
        //InfiniteScrollAdapter wrapper = InfiniteScrollAdapter.wrap(collegeAdapter);
        scrollView.setAdapter(collegeAdapter);

        assignGeneralProfileInfo();

        //set up of favorite colleges list
        loadFavoriteColleges();

        bvFavoritesMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // The list of 'liked' colleges is can simply be sent to map activity
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_view_click));
                Intent i = new Intent(mContext, MapActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("favoritedList", colleges);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        ivForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_view_click));
                Boolean scroll = scrollView.fling(10, 0);
                if (scrollView.getCurrentItem() < colleges.size() - 1) {
                    scrollView.smoothScrollToPosition(scrollView.getCurrentItem() + 1);
                }

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_view_click));
                Boolean scroll = scrollView.fling(10, 0);
                if (scrollView.getCurrentItem() >= 1) {
                    scrollView.smoothScrollToPosition(scrollView.getCurrentItem() - 1);
//                  ObjectAnimator.ofInt(scrollView, "scrollX",  scrollView.getCurrentItem() - 1).setDuration(500).start();

                }
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(mContext,"Verify successful", Toast.LENGTH_LONG);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_logout:
                ParseUser.logOut();
                mAuth.signOut();
                Log.d("ProfileActivity", "Logged out successfully");
                Toast.makeText(mContext, "Logout successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.new_deadline:
                Intent intent = new Intent(mContext, NewDeadlineDialog.class);
                startActivity(intent);
                break;
        }
        return true;
    }



    //-------------------Handling assignment/prep for profile general info----------------------------
    public void assignGeneralProfileInfo() {
        final String firstName = ParseUser.getCurrentUser().get(Constants.KEY_USER_FIRST_NAME).toString();
        final String lastName = ParseUser.getCurrentUser().get(Constants.KEY_USER_LAST_NAME).toString();
        tvProgressLabel.setText(String.format("Hi, %s! Here's your progress.", firstName));
        tvFirstName.setText(String.format("%s %s", firstName, lastName));
        tvEmail.setText(ParseUser.getCurrentUser().getEmail());

        Glide.with(this)
                .load(ParseUser.getCurrentUser().getParseFile(Constants.KEY_USER_PROFILE_IMAGE).getUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfileImage);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                startActivityForResult(intent, GALLERY_IMAGE_SELECTION_REQUEST_CODE);
            }
        });

        setPbProgress();
    }

    public void setPbProgress() {
        UserDeadlineRelation.Query udQuery = new UserDeadlineRelation.Query();
        udQuery.getTop().withUser();
        udQuery.whereEqualTo(Constants.KEY_USER, ParseUser.getCurrentUser());

        udQuery.findInBackground(new FindCallback<UserDeadlineRelation>() {
            @Override
            public void done(List<UserDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    double numCompleted = 0;
                    for (int i = 0; i < objects.size(); i++) {
                        UserDeadlineRelation relation = objects.get(i);
                        if (relation.getCompleted()) {
                            numCompleted++;
                        }
                    }
                    pbProgress.setProgress((int)(numCompleted/objects.size()*100));
                    tvProgressText.setText(String.format("%s/%s", (int)numCompleted, objects.size()));
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    //-------------------Handling profile image changes----------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (null == data) return;

        if (requestCode == GALLERY_IMAGE_SELECTION_REQUEST_CODE && data.getData() != null) {
            Uri uri = data.getData();
            filePath = GalleryUtils.getPath(mContext, uri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                Glide.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            saveProfileImageChange();
        }
    }

    private void saveProfileImageChange() {
        final File file = new File(filePath);
        final ParseFile parseImageProfile = new ParseFile(file);

        parseImageProfile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser.getCurrentUser().put(Constants.KEY_USER_PROFILE_IMAGE, parseImageProfile);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("ProfileFragment", "Create item_post success");
                                Toast.makeText(mContext, "Profile image changed!", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    //-------------------Loading Recycler View----------------------------
    public void loadFavoriteColleges() {
        final UserCollegeRelation.Query ucRelationQuery = new UserCollegeRelation.Query();
        ucRelationQuery.getTop().withCollege().withUser();
        ucRelationQuery.whereEqualTo(Constants.KEY_USER, ParseUser.getCurrentUser());

        ucRelationQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    colleges.clear();
                    collegeAdapter.notifyDataSetChanged();
                    for(int i = 0; i < objects.size(); i++) {
                        UserCollegeRelation relation = objects.get(i);
                        College college = relation.getCollege();
                        colleges.add(college);
                        collegeAdapter.notifyItemInserted(colleges.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    //--------------------Refreshing Fragment-----------------------------

    public void refresh() {
        collegeAdapter.clear();
        collegeAdapter.clearWithFilter();
        loadFavoriteColleges();
        collegeAdapter.addAllFiltered(colleges);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //--------------------Implementing Interface----------------------------

    @Override
    public void updateList(boolean update) {
        if (update) {
            refresh();
            setPbProgress();
        }
    }

    @Override
    public void getCollegeUnliked(boolean unliked) {
        if (unliked) {
            unlikedFromProfileInterface.getCollegeUnliked(true);
        }
    }

    @Override
    public void updateProgressBar(boolean update) {
        if (update) {
            setPbProgress();
        }
    }

    public static void setUnlikedFromProfileInterface(GetCollegeUnlikedFromProfileInterface unlikedInterface) {
        unlikedFromProfileInterface = unlikedInterface;
    }
}
