package me.juliasson.unipath.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.LoginActivity;
import me.juliasson.unipath.adapters.CollegeAdapter;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.UserCollegeRelation;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.GalleryUtils;
import me.juliasson.unipath.utils.SpaceItemDecoration;

public class ProfileFragment extends Fragment {

    private SwipeRefreshLayout swipeContainer;
    private TextView tvProgressLabel;
    private ProgressBar pbProgress;
    private ImageView ivProfileImage;
    private TextView tvFirstName;
    private TextView tvEmail;
    private Button bvLogout;

    private CollegeAdapter collegeAdapter;
    private ArrayList<College> colleges;
    private RecyclerView rvColleges;

    private final String KEY_FIRST_NAME = "firstName";
    private final String KEY_LAST_NAME = "lastName";
    private final String KEY_PROFILE_IMAGE = "profileImage";
    private final String KEY_RELATION_USER = "user";

    private final static int GALLERY_IMAGE_SELECTION_REQUEST_CODE = 2034;
    private String filePath;

    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_profile, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mContext = view.getContext();

        //settings up general profile info
        swipeContainer = view.findViewById(R.id.swipeContainer);
        tvProgressLabel = view.findViewById(R.id.tvProgressLabel);
        pbProgress = view.findViewById(R.id.pbProgress);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvFirstName = view.findViewById(R.id.tvFirstName);
        tvEmail = view.findViewById(R.id.tvEmail);
        bvLogout = view.findViewById(R.id.bvLogout);

        //setting up favorite colleges list adapter
        rvColleges = view.findViewById(R.id.rvCollegeList);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.direct_gallery_grid_spacing);
        rvColleges.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        colleges = new ArrayList<>();
        collegeAdapter = new CollegeAdapter(colleges);
        rvColleges.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rvColleges.setAdapter(collegeAdapter);

        assignGeneralProfileInfo();

        //set up of favorite colleges list
        loadFavoriteColleges();
        setUpSwipeContainer(view);
    }

    //-------------------Handling assignment/prep for profile general info----------------------------
    public void assignGeneralProfileInfo() {
        final String firstName = ParseUser.getCurrentUser().get(KEY_FIRST_NAME).toString();
        final String lastName = ParseUser.getCurrentUser().get(KEY_LAST_NAME).toString();
        tvProgressLabel.setText(String.format("Hi, %s! Here's your progress.", firstName));
        tvFirstName.setText(String.format("%s %s", firstName, lastName));
        tvEmail.setText(ParseUser.getCurrentUser().getEmail());

        Glide.with(this)
                .load(ParseUser.getCurrentUser().getParseFile(KEY_PROFILE_IMAGE).getUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfileImage);

        bvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Log.d("ProfileActivity", "Logged out successfully");
                Toast.makeText(mContext, "Logout successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

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
        udQuery.whereEqualTo(KEY_RELATION_USER, ParseUser.getCurrentUser());

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
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setUpSwipeContainer(View view) {
        //find the swipe container
        swipeContainer = view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                refresh();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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
                    ParseUser.getCurrentUser().put(KEY_PROFILE_IMAGE, parseImageProfile);
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

        ucRelationQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); i++) {
                        UserCollegeRelation relation = objects.get(i);
                        if (ParseUser.getCurrentUser().getObjectId().equals(relation.getUser().getObjectId())) {
                            College college = relation.getCollege();
                            colleges.add(college);
                            collegeAdapter.notifyItemInserted(colleges.size()-1);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void refresh() {
        collegeAdapter.clear();
        loadFavoriteColleges();
        collegeAdapter.addAll(colleges);
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }
}
