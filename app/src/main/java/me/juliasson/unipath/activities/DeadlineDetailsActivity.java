package me.juliasson.unipath.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.parceler.Parcels;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;


public class DeadlineDetailsActivity extends AppCompatActivity {
    TextView tvcollegeName;
    ImageView ivCollegeImage;

    College college;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deadline_details);
        this.setFinishOnTouchOutside(true);

        tvcollegeName = (TextView) findViewById(R.id.tvCollege);
        ivCollegeImage = (ImageView) findViewById(R.id.ivCollegeImage);
        college = (College) Parcels.unwrap(getIntent().getParcelableExtra(College.class.getSimpleName()));

        tvcollegeName.setText(college.getCollegeName());

        Glide.with(this)
                .load(college.getCollegeImage().getUrl())
                .into(ivCollegeImage);

        Bundle args = new Bundle();
        args.putParcelable("college", college);


    }
}

