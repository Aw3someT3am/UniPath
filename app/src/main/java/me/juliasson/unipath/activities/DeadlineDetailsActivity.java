package me.juliasson.unipath.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.DDCollegeListAdapter;
import me.juliasson.unipath.model.TimeLine;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.DateTimeUtils;


public class DeadlineDetailsActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;
    private int count_completed_deadlines = 0;
    private DDCollegeListAdapter ddcAdapter;
    private ArrayList<UserDeadlineRelation> relations;
    private RecyclerView rvRelations;
    private TimeLine timeline;
    private HashMap<TimeLine, ArrayList<UserDeadlineRelation>> mHashRelations;

    TextView tvDate;

    private String activityDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_deadline_details);
        this.setFinishOnTouchOutside(true);

        setSize();

        timeline = Parcels.unwrap(getIntent().getParcelableExtra(TimeLine.class.getSimpleName()));
        mHashRelations = Parcels.unwrap(getIntent().getParcelableExtra(HashMap.class.getSimpleName()));
        tvDate = findViewById(R.id.tvDate);
        activityDate = DateTimeUtils.parseDateTime(timeline.getDate(), DateTimeUtils.parseInputFormat, DateTimeUtils.parseOutputFormat);
        tvDate.setText(activityDate);

        rvRelations = findViewById(R.id.rvCollegeList);
        relations = new ArrayList<>();
        ddcAdapter = new DDCollegeListAdapter(relations);
        rvRelations.setLayoutManager(new LinearLayoutManager(this));
        rvRelations.setAdapter(ddcAdapter);

        loadRelations();

        //find the swipe container
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                refresh();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public void loadRelations() {
        if (mHashRelations.containsKey(timeline)) {
            for (UserDeadlineRelation relation : mHashRelations.get(timeline)) {
                relations.add(relation);
                ddcAdapter.notifyItemInserted(relations.size() - 1);
            }
        }
    }

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, (3 * height)/4);
    }

    public void refresh() {
        ddcAdapter.clear();
        ddcAdapter.addAll(relations);
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }
}

