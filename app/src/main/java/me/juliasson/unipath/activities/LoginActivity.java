package me.juliasson.unipath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import me.juliasson.unipath.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button bvLogin;
    private Button bvSignup;
    private FirebaseAuth mAuth;

    private static final String TAG = "LoginActivity";
    private EditText etEmail;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        ParseUser.logOut();

        mAuth = FirebaseAuth.getInstance();

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            Log.d("LoginActivity", "Login successful");
            Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
            startActivity(i);
            finish();
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        bvLogin = findViewById(R.id.bvLogin);
        bvSignup = findViewById(R.id.bvSignUp);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(LoginActivity.this,"Verify successful", Toast.LENGTH_LONG);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

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
    }

    private void logIn(final String username, final String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    final String email = ParseUser.getCurrentUser().getEmail();
                    if(!email.equals("") && !password.equals("") && !username.equals("")) {
                        mAuth.signInWithEmailAndPassword(email,password);
                    }else{
                        Toast.makeText(LoginActivity.this,"You didn;t fill all the fields", Toast.LENGTH_LONG);
                    }

                    Log.d("LoginActivity", "Login successful");
                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    final Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Log.e("LoginActivity","Login failure");
                    Toast.makeText(LoginActivity.this, "Login failure\nIncorrect username or password", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
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
}
