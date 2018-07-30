package me.juliasson.unipath.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.DateTimeUtils;

public class NewDeadlineDialog extends AppCompatActivity {

    private Context mContext;
    private Date currentDate;
    private Date assignedDate;
    private DatePickerDialog datePickerDialog;
    private int mYear;
    private int mMonth;
    private int mDay;

    private EditText etDescription;
    private TextView tvDate;
    private ImageView ivDatePicker;
    private Button bvAddDeadline;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_new_deadline);
        this.setFinishOnTouchOutside(true);

        setSize();

        mContext = this;
        etDescription = findViewById(R.id.etDescription);
        tvDate = findViewById(R.id.tvDate);
        ivDatePicker = findViewById(R.id.ivDatePicker);
        bvAddDeadline = findViewById(R.id.bvAddDeadline);

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

        bvAddDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Deadline deadline = new Deadline();
                deadline.setDescription(etDescription.getText().toString());
                deadline.setDeadlineDate(assignedDate);
                //TODO: FINANCIAL STATUS
                deadline.setIsFinancial(false);
                UserDeadlineRelation relation = new UserDeadlineRelation();
                relation.setCompleted(false);
                relation.setUser(ParseUser.getCurrentUser());
                relation.setDeadline(deadline);
                //TODO: LET USER PICK COLLEGE
                relation.setCollege(new College());
            }
        });
    }

    @SuppressLint("NewApi")
    public void showDatePicker(View view) {
        datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
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
}
