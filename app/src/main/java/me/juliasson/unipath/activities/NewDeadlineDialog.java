package me.juliasson.unipath.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.juliasson.unipath.R;
import me.juliasson.unipath.internal.GetCustomDeadlineAddedInterface;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.DateTimeUtils;

public class NewDeadlineDialog extends AppCompatActivity {

    private Context mContext;
    private NewDeadlineDialog mActivity;
    private Date currentDate;
    private Date assignedDate;
    private DatePickerDialog datePickerDialog;
    private College chosenCollege;
    private int mYear;
    private int mMonth;
    private int mDay;

    private EditText etDescription;
    private TextView tvDate;
    private TextView tvCollegeName;
    private ImageView ivDatePicker;
    private ImageView ivCollegeImage;
    private Button bvAddDeadline;
    private LikeButton lbIsFinancial;
    private RelativeLayout rlPickCollege;

    private boolean isFinancial = false;
    private final String KEY_COLLEGE_IMAGE = "image";
    private final int RESULT_COLLEGE_PICKER = 1012;

    private static final String TAG = "AddToDatabase";

    private static FirebaseDatabase mFirebaseDatabase;
    private static FirebaseAuth mAuth;
    private static FirebaseAuth.AuthStateListener mAuthListener;
    private static DatabaseReference myRef;

    private static GetCustomDeadlineAddedInterface customDeadlineInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_deadline);
        this.setFinishOnTouchOutside(true);

        setSize();

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mContext = this;
        mActivity = this;
        etDescription = findViewById(R.id.etDescription);
        tvDate = findViewById(R.id.tvDate);
        tvCollegeName = findViewById(R.id.tvCollegeName);
        ivDatePicker = findViewById(R.id.ivDatePicker);
        ivCollegeImage = findViewById(R.id.ivCollegeImage);
        bvAddDeadline = findViewById(R.id.bvAddDeadline);
        lbIsFinancial = findViewById(R.id.lbIsFinancial);
        rlPickCollege = findViewById(R.id.rlPickCollege);

        final Calendar calendar = Calendar.getInstance();
        currentDate = calendar.getTime();
        assignedDate = currentDate;
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);

        tvDate.setText(DateTimeUtils.parseDateTime(currentDate.toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat));

        ivDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_view_click));
                showDatePicker(view);
            }
        });

        lbIsFinancial.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                isFinancial = true;
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                isFinancial = false;
            }
        });

        rlPickCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCollegePicker(view);
            }
        });

        bvAddDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(chosenCollege == null) && !(assignedDate == null)) {

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.
                            Object value = dataSnapshot.getValue();
                            Log.d(TAG, "Value is: " + value);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });

                    Log.d(TAG, "onClick: Attempting to add object to database.");
                    String date = DateTimeUtils.parseDateTime(assignedDate.toString(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat);
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();
                    myRef.child(mContext.getString(R.string.dbnode_users)).child(userID).child("dates").child("custom_deadline").setValue(date);
                    //toastMessage("Adding " + date + " to database...");

                    Deadline deadline = new Deadline();
                    deadline.setDescription(etDescription.getText().toString());
                    deadline.setDeadlineDate(assignedDate);
                    deadline.setIsFinancial(isFinancial);
                    deadline.setIsCustom(true);
                    UserDeadlineRelation relation = new UserDeadlineRelation();
                    relation.setCompleted(false);
                    relation.setUser(ParseUser.getCurrentUser());
                    relation.setDeadline(deadline);
                    relation.setCollege(chosenCollege);
                    relation.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(com.parse.ParseException e) {
                            Log.d("NewDeadlineDialog", "New custom deadline saved and created.");
                            Toast.makeText(mContext, "Custom deadline added!", Toast.LENGTH_SHORT).show();
                            customDeadlineInterface.getCustomDeadlineAdded(true);
                            mActivity.finish();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("NewApi")
    public void showDatePicker(View view) {
        datePickerDialog = new DatePickerDialog(mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                tvDate.setText(String.format("%s %s %s", dayOfMonth, getMonth(monthOfYear).substring(0, 3), year));
                String date = String.format("%s %s %s", getMonth(monthOfYear), dayOfMonth, year);
                DateFormat format = new SimpleDateFormat("MMMM d yyyy", Locale.ENGLISH);
                try {
                    assignedDate = format.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, (3 * height)/4);
    }

    public void showCollegePicker(View view) {
        Intent intent = new Intent(mContext, NDCollegeListDialog.class);
        startActivityForResult(intent, RESULT_COLLEGE_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_COLLEGE_PICKER && resultCode == RESULT_OK) {
            chosenCollege = Parcels.unwrap(data.getParcelableExtra(College.class.getSimpleName()));
            tvCollegeName.setText(chosenCollege.getCollegeName());

            Glide.with(mContext)
                    .load(chosenCollege.getParseFile(KEY_COLLEGE_IMAGE).getUrl())
                    .into(ivCollegeImage);
        }
    }

    //-------------------------implementing interface--------------------------

    public static void setCustomDeadlineInterface(GetCustomDeadlineAddedInterface addedInterface) {
        customDeadlineInterface = addedInterface;
    }

}
