package me.juliasson.unipath.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.io.IOException;

import me.juliasson.unipath.R;
import me.juliasson.unipath.utils.GalleryUtils;

public class SignUpActivity extends AppCompatActivity {

    private ImageView ivProfileImage;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etEmail;
    private Button bvCreateAccount;
    private Button bvCancel;

    private final String KEY_FIRST_NAME = "firstName";
    private final String KEY_LAST_NAME = "lastName";
    private final String KEY_PROFILE_IMAGE = "profileImage";

    private final static int GALLERY_IMAGE_SELECTION_REQUEST_CODE = 2034;
    private String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ivProfileImage = findViewById(R.id.ivProfileImage);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        bvCreateAccount = findViewById(R.id.bvCreateAccount);
        bvCancel = findViewById(R.id.bvCancel);

        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/jpeg");
                startActivityForResult(intent, GALLERY_IMAGE_SELECTION_REQUEST_CODE);
            }
        });

        bvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String firstName = etFirstName.getText().toString();
                final String lastName = etLastName.getText().toString();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                final String email = etEmail.getText().toString();

                final File file = new File(filePath);
                final ParseFile parseImageProfile = new ParseFile(file);

                parseImageProfile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            onSignUp(firstName, lastName, username, password, email, parseImageProfile);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        bvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void onSignUp(String firstName, String lastName, String username, String password, String email, ParseFile profileImage) {
        ParseUser parseUser = new ParseUser();

        parseUser.setUsername(username);
        parseUser.setPassword(password);
        parseUser.setEmail(email);

        parseUser.put(KEY_FIRST_NAME, firstName);
        parseUser.put(KEY_LAST_NAME, lastName);
        parseUser.put(KEY_PROFILE_IMAGE, profileImage);

        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignupActivity", "Sign Up successful");
                    Toast.makeText(SignUpActivity.this, "Sign up successful", Toast.LENGTH_LONG).show();
                    final Intent i = new Intent(SignUpActivity.this, TimelineActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
        }
    }
}
