package me.juliasson.unipath.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.DDCollegeListAdapter;
import me.juliasson.unipath.internal.GetDeadlineDialogStatusInterface;
import me.juliasson.unipath.internal.UpdateTimelineAdapterInterface;
import me.juliasson.unipath.model.TimeLine;
import me.juliasson.unipath.model.UserDeadlineRelation;


public class DeadlineDetailsDialog extends AppCompatActivity implements GetDeadlineDialogStatusInterface{

    private DDCollegeListAdapter ddcAdapter;
    private ArrayList<UserDeadlineRelation> relations;
    private RecyclerView rvRelations;
    private TimeLine timeline;
    private HashMap<TimeLine, ArrayList<UserDeadlineRelation>> mHashRelations;
    private boolean isChanged = false;
    private static UpdateTimelineAdapterInterface utaInterface;

    private TextView tvDate;

    private String activityDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_deadline_details);
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        this.setFinishOnTouchOutside(true);

        setSize();

        timeline = Parcels.unwrap(getIntent().getParcelableExtra(TimeLine.class.getSimpleName()));
        mHashRelations = Parcels.unwrap(getIntent().getParcelableExtra(HashMap.class.getSimpleName()));
        tvDate = findViewById(R.id.tvDate);
        activityDate = timeline.getDate();
        tvDate.setText(activityDate);

        rvRelations = findViewById(R.id.rvCollegeList);
        relations = new ArrayList<>();
        ddcAdapter = new DDCollegeListAdapter(relations, this);
        rvRelations.setLayoutManager(new LinearLayoutManager(this));
        rvRelations.setAdapter(ddcAdapter);

        loadRelations();
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

    //-------------------------implementing interface----------------------------

    @Override
    public void isDialogEmpty(boolean isEmpty) {
        if (isEmpty) {
            utaInterface.updateItemRemoval(true);
            finish();
        }
    }

    @Override
    public void isDialogChanged(boolean isChanged) {
        if (isChanged) {
            this.isChanged = true;
        }
    }

    public static void setUtaInterface(UpdateTimelineAdapterInterface utaInterface0) {
        utaInterface = utaInterface0;
    }

    //------------------------listen for clicking outside of dialog-------------

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            utaInterface.updateItemStatus(isChanged);
            finish();
        }
        return false;
    }
}

