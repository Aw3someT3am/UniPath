package me.juliasson.unipath.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

import me.juliasson.unipath.R;
import me.juliasson.unipath.utils.GalleryUtils;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProgressLabel;
    private ProgressBar pbProgress;
    private ImageView ivProfileImage;
    private TextView tvFirstName;
    private TextView tvEmail;
    private ImageButton bvBack;
    private Button bvLogout;

    private final String KEY_FIRST_NAME = "firstName";
    private final String KEY_LAST_NAME = "lastName";
    private final String KEY_PROFILE_IMAGE = "profileImage";

    private final static int GALLERY_IMAGE_SELECTION_REQUEST_CODE = 2034;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        pbProgress = findViewById(R.id.pbProgress);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvEmail = findViewById(R.id.tvEmail);
        bvBack = findViewById(R.id.bvBack);
        bvLogout = findViewById(R.id.bvLogout);

        final String firstName = ParseUser.getCurrentUser().get(KEY_FIRST_NAME).toString();
        final String lastName = ParseUser.getCurrentUser().get(KEY_LAST_NAME).toString();
        tvProgressLabel.setText(String.format("Hi, %s! Here's your progress.", firstName));
        tvFirstName.setText(String.format("%s %s", firstName, lastName));
        tvEmail.setText(ParseUser.getCurrentUser().getEmail());

        Glide.with(this)
                .load(ParseUser.getCurrentUser().getParseFile(KEY_PROFILE_IMAGE).getUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(ivProfileImage);

        bvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        bvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Log.d("ProfileActivity", "Logged out successfully");
                Toast.makeText(ProfileActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (null == data) return;

        if (requestCode == GALLERY_IMAGE_SELECTION_REQUEST_CODE && data.getData() != null) {
            Uri uri = data.getData();
            filePath = GalleryUtils.getPath(this, uri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Glide.with(this)
                        .load(bitmap)
                        .apply(RequestOptions.circleCropTransform())
                        .into(ivProfileImage);
                //ivProfileImage.setImageBitmap(bitmap);
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
                                Log.d("ProfileActivity", "Create item_post success");
                                Toast.makeText(ProfileActivity.this, "Profile image changed!", Toast.LENGTH_SHORT).show();
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
}
