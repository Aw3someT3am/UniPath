package me.juliasson.unipath.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;
import java.io.IOException;

import me.juliasson.unipath.R;
import me.juliasson.unipath.utils.Constants;
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

    private static final String TAG = "SignUpActivity";
    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    private final int MIN_PASS_LENGTH = 6;

    View rootLayout;

    private int revealX;
    private int revealY;

    private FirebaseAuth mAuth;

    private final static int GALLERY_IMAGE_SELECTION_REQUEST_CODE = 2034;
    private String filePath="";
    private Context mContext;
    private Activity mActivity;
    private ProgressBar pbProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.toolbar_action_bar);

        mContext = this;
        mActivity = this;
        mAuth = FirebaseAuth.getInstance();

        rootLayout = findViewById(R.id.rootLayout);
        final Intent intent = getIntent();
        if (savedInstanceState == null &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);

            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);


            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }

        pbProgressBar = findViewById(R.id.pbLoading);
        ivProfileImage = findViewById(R.id.ivProfileImage);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        bvCreateAccount = findViewById(R.id.bvCreateAccount);
        bvCancel = findViewById(R.id.bvCancel);

        Glide.with(this)
                .load(R.drawable.ic_person_100dp)
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

        bvCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressBar();
                final String firstName = etFirstName.getText().toString();
                final String lastName = etLastName.getText().toString();
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                final String email = etEmail.getText().toString();

                final File file = new File(filePath);
                final ParseFile parseImageProfile = new ParseFile(file);

                if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                    makeToast("Please fill all sections");
                    hideProgressBar();
                } else if (password.length() < MIN_PASS_LENGTH) {
                    makeToast("Password must be 6 or more characters");
                    hideProgressBar();
                } else if (!email.contains("@")) {
                    makeToast("Please enter a valid email address");
                    hideProgressBar();
                } else {
                    parseImageProfile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                onSignUp(firstName, lastName, username, password, email, parseImageProfile);
                            } else {
                                makeToast("Please choose a profile image");
                                Log.d("SignupActivity", "Bad Parse Image");
                                hideProgressBar();
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

        bvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unRevealActivity();
            }
        });
    }

    private void makeToast(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
        toast.show();
    }

    private void onSignUp(String firstName, String lastName, String username, final String password, final String email, ParseFile profileImage) {
        ParseUser parseUser = new ParseUser();

        parseUser.setUsername(username);
        parseUser.setPassword(password);
        parseUser.setEmail(email);

        parseUser.put(Constants.KEY_USER_FIRST_NAME, firstName);
        parseUser.put(Constants.KEY_USER_LAST_NAME, lastName);
        parseUser.put(Constants.KEY_USER_PROFILE_IMAGE, profileImage);

        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    makeToast("Sign up successful");
                    Log.d("SignupActivity", "Sign Up successful");

                    //if successful sign up, then sign up authentication
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT);
                                    }

                                    // ...
                                }
                            });

                    //open homepage upon account creation
                    final Intent i = new Intent(SignUpActivity.this, TimelineActivity.class);
                    startActivity(i);
                    hideProgressBar();
                    finish();
                } else {
                    makeToast("Sign up failure\nFill all fields with valid answers.");
                    Log.d("SignupActivity", "Sign up failure");
                    hideProgressBar();
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    public void showProgressBar() {
        pbProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        pbProgressBar.setVisibility(View.INVISIBLE);
    }

    //--------------------Animation---------------------
    protected void revealActivity(int x, int y) {
        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
        circularReveal.setDuration(500);
        circularReveal.setInterpolator(new AccelerateInterpolator());
        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    protected void unRevealActivity() {
        float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                rootLayout, revealX, revealY, finalRadius, 0);

        circularReveal.setDuration(500);
        circularReveal.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                rootLayout.setVisibility(View.INVISIBLE);
                finish();
            }
        });
        circularReveal.start();
    }
}
