package me.juliasson.unipath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import me.juliasson.unipath.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button bvLogin;
    private Button bvSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Log.d("LoginActivity", "Login successful");
            Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
            startActivity(i);
            finish();
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        bvLogin = findViewById(R.id.bvLogin);
        bvSignup = findViewById(R.id.bvSignUp);

        bvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                logIn(username, password);
            }
        });

        bvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        //TODO: REMOVE THESE LATER
        Button TIMELINE = findViewById(R.id.TIMELINE);
        TIMELINE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
                startActivity(i);
                finish();
            }
        });

        Button SEARCH = findViewById(R.id.SEARCH);
        SEARCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SearchActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void logIn(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d("LoginActivity", "Login successful");
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    final Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Log.e("LoginActivity","Login failure");
                    e.printStackTrace();
                }
            }
        });
    }
}