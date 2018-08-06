package me.juliasson.unipath.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.NDCollegeListAdapter;
import me.juliasson.unipath.internal.GetCollegeInterface;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.UserCollegeRelation;

public class NDCollegeListDialog extends AppCompatActivity implements GetCollegeInterface {

    private RecyclerView rvCollegeList;
    private NDCollegeListAdapter collegeAdapter;
    private ArrayList<College> colleges;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_nd_college_picker);
        this.setFinishOnTouchOutside(true);

        setSize();

        rvCollegeList = findViewById(R.id.rvCollegeList);
        rvCollegeList.setLayoutManager(new LinearLayoutManager(this));
        colleges = new ArrayList<>();
        collegeAdapter = new NDCollegeListAdapter(colleges, this);
        rvCollegeList.setAdapter(collegeAdapter);

        loadFavoriteColleges();
    }

    public void loadFavoriteColleges() {
        final UserCollegeRelation.Query ucRelationQuery = new UserCollegeRelation.Query();
        ucRelationQuery.getTop().withCollege().withUser();
        ucRelationQuery.whereEqualTo("user", ParseUser.getCurrentUser());

        ucRelationQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); i++) {
                        UserCollegeRelation relation = objects.get(i);
                        College college = relation.getCollege();
                        colleges.add(college);
                        collegeAdapter.notifyItemInserted(colleges.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, (5 * height)/6);
    }

    @Override
    public void setCollege(College college) {
        Log.d("NDCollegeListDialog", String.format("%s", college.getCollegeName()));
        Intent intent = new Intent();
        intent.putExtra(College.class.getSimpleName(), Parcels.wrap(college));
        setResult(RESULT_OK, intent);
        finish();
    }
}
