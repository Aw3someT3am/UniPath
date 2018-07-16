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

import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProgressLabel;
    private ProgressBar pbProgress;
    private ImageView ivProfileImage;
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvEmail;
    private ImageButton bvBack;
    private Button bvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        pbProgress = findViewById(R.id.pbProgress);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvFirstName = findViewById(R.id.tvFirstName);
        tvLastName = findViewById(R.id.tvLastName);
        tvEmail = findViewById(R.id.tvEmail);
        bvBack = findViewById(R.id.bvBack);
        bvLogout = findViewById(R.id.bvLogout);

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
