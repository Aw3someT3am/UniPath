package me.juliasson.unipath;

import android.content.Intent;
import android.os.Bundle;
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
import com.parse.ParseUser;

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
    }
}
