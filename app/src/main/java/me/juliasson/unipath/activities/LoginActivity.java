package me.juliasson.unipath.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import me.juliasson.unipath.MyFirebaseMessagingService;
import me.juliasson.unipath.R;
import me.juliasson.unipath.utils.Constants;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button bvLogin;
    private Button bvSignup;
    private FirebaseAuth mAuth;
    private ProgressBar pbProgressBar;
    private MyFirebaseMessagingService myFirebaseMessagingService = new MyFirebaseMessagingService();

    private static final String TAG = "LoginActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        ParseUser currentUser = ParseUser.getCurrentUser();

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

        if (currentUser != null) {
            Log.d("LoginActivity", "Login successful");
            Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
            startActivity(i);
            finish();
        }

        pbProgressBar = findViewById(R.id.pbLoading);
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
                presentActivity(view);
            }
        });
    }

    private void logIn(final String username, final String password) {
        showProgressBar();
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    final String email = ParseUser.getCurrentUser().getEmail();
                    if(!email.equals("") && !password.equals("")) {
                        Toast.makeText(LoginActivity.this,"Login Authentication Accepted", Toast.LENGTH_LONG);
                        mAuth.signInWithEmailAndPassword(email,password);
                    }else{
                        Toast.makeText(LoginActivity.this,"You didn't fill all the fields", Toast.LENGTH_LONG);
                    }

                    MyFirebaseMessagingService.clear();
                    Log.d("LoginActivity", "Login successful");
                    Toast toast = Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
                    toast.show();

                    final Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
                    startActivity(i);
                    hideProgressBar();
                    finish();
                } else {
                    Log.e("LoginActivity","Login failure");
                    Toast toast = Toast.makeText(LoginActivity.this, "Login failure\nIncorrect username or password", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
                    toast.show();
                    hideProgressBar();
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

    public void showProgressBar() {
        pbProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        pbProgressBar.setVisibility(View.INVISIBLE);
    }

    //--------------------Animation---------------------
    public void presentActivity(View view) {
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, view, "transition");
        int revealX = (int) (view.getX() + view.getWidth() / 2);
        int revealY = (int) (view.getY() + view.getHeight() / 2);

        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        intent.putExtra(SignUpActivity.EXTRA_CIRCULAR_REVEAL_X, revealX);
        intent.putExtra(SignUpActivity.EXTRA_CIRCULAR_REVEAL_Y, revealY);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }
}
