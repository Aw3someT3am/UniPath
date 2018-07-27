package me.juliasson.unipath.activities;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import me.juliasson.unipath.R;
import me.juliasson.unipath.fragments.DatePickerFragment;

public class NewDeadlineDialog extends AppCompatActivity {

    private Context mContext;

    private EditText etDescription;
    private TextView tvDate;
    private ImageView ivDatePicker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_deadline_details);
        this.setFinishOnTouchOutside(true);

        setSize();

        mContext = this;
        etDescription = findViewById(R.id.etDescription);
        tvDate = findViewById(R.id.tvDate);
        ivDatePicker = findViewById(R.id.ivDatePicker);

        ivDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.image_view_click));
                showDatePicker(view);
            }
        });
    }

    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "date picker");
    }

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, (3 * height)/4);
    }
}
