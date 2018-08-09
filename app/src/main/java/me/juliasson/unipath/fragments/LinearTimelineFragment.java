package me.juliasson.unipath.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.LoginActivity;
import me.juliasson.unipath.activities.NewDeadlineDialog;
import me.juliasson.unipath.activities.TimelineActivity;
import me.juliasson.unipath.adapters.TimeLineAdapter;
import me.juliasson.unipath.internal.GetDeadlineCheckedInterface;
import me.juliasson.unipath.internal.GetItemDetailOpenedInterface;
import me.juliasson.unipath.internal.UpdateFavCollegeListLinearTimeline;
import me.juliasson.unipath.internal.UpdateLinearTimelineInterface;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.model.OrderStatus;
import me.juliasson.unipath.model.TimeLine;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.Constants;
import me.juliasson.unipath.utils.DateTimeUtils;

public class LinearTimelineFragment extends Fragment implements
        UpdateLinearTimelineInterface,
        UpdateFavCollegeListLinearTimeline,
        GetItemDetailOpenedInterface,
        GetDeadlineCheckedInterface {

    private FrameLayout touchInterceptor;

    private RecyclerView mRecyclerView;
    private TextView tvNoDeadlines;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLine> mDataList = new ArrayList<>();
    private HashSet<TimeLine> mDataSet = new HashSet<>();
    private HashMap<TimeLine, ArrayList<UserDeadlineRelation>> mRelationsInTimeLine = new HashMap<>();
    private Context mContext;
    private View mView;
    private boolean isTimelineOpened = false;

    private UpdateLinearTimelineInterface ultInterface;
    private GetItemDetailOpenedInterface gidInterface;
    private GetDeadlineCheckedInterface dcInterface;
    private static GetDeadlineCheckedInterface dcInterfaceFromTimelineActivity;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        mContext = parent.getContext();
        touchInterceptor = new FrameLayout(mContext);
        touchInterceptor.setClickable(true);
        TimelineActivity.updateFavCollegeListInterfaceLinearTimeline(this);
        View v = inflater.inflate(R.layout.fragment_linear_timeline, parent, false);
        mView = v;
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ultInterface = this;
        gidInterface = this;
        dcInterface = this;
        tvNoDeadlines = (TextView) view.findViewById(R.id.tvNoDeadlines);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        setDataListItems();
    }

    private void setDataListItems(){
        //ParseQuery go through each of the current user's deadlines and add them.
        UserDeadlineRelation.Query udQuery = new UserDeadlineRelation.Query();
        udQuery.getTop().withUser().withDeadline().withCollege();
        udQuery.whereEqualTo(Constants.KEY_USER, ParseUser.getCurrentUser());

        mDataSet.clear();
        mDataList.clear();
        mRelationsInTimeLine.clear();
        hideNoDeadlines();

        udQuery.findInBackground(new FindCallback<UserDeadlineRelation>() {
            @Override
            public void done(List<UserDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        UserDeadlineRelation relation = objects.get(i);
                        Deadline deadline = relation.getDeadline();
                        Date date = deadline.getDeadlineDate();
                        SimpleDateFormat format = new SimpleDateFormat(DateTimeUtils.parseOutputFormat, Locale.ENGLISH);
                        String dateString = format.format(date);
                        TimeLine timeline = new TimeLine(dateString, date, OrderStatus.INACTIVE); //default OrderStatus will be inactive.
                        mDataSet.add(timeline);
                        addTimeLineToRelationMap(timeline, relation);
                    }
                    mDataList.addAll(mDataSet);
                    mTimeLineAdapter = new TimeLineAdapter(mDataList,
                            mRelationsInTimeLine,
                            ultInterface,
                            gidInterface,
                            dcInterface);
                    mRecyclerView.setAdapter(mTimeLineAdapter);
                    sortData();
                    if (!mDataList.isEmpty()) {
                        hideNoDeadlines();
                    } else {
                        showNoDeadlines();
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addTimeLineToRelationMap(TimeLine timeline, UserDeadlineRelation relation) {
        ArrayList<UserDeadlineRelation> relationList = mRelationsInTimeLine.get(timeline);
        if (relationList == null) {
            relationList = new ArrayList<>();
        }
        relationList.add(relation);
        mRelationsInTimeLine.put(timeline, relationList);
    }

    public void sortData() {
        Collections.sort(mDataList, new Comparator<TimeLine>() {
            @Override
            public int compare(TimeLine t1, TimeLine t2) {
                return t1.getDDate().compareTo(t2.getDDate());
            }
        });
        mTimeLineAdapter.notifyDataSetChanged();
    }

    public void refresh() {
        mTimeLineAdapter.clear();
        setDataListItems();
        mTimeLineAdapter.addAll(mDataList);
    }

    public void showNoDeadlines() {
        tvNoDeadlines.setVisibility(View.VISIBLE);
    }

    public void hideNoDeadlines() {
        tvNoDeadlines.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause() {
        if (touchInterceptor.getParent() == null && isTimelineOpened) {
            ((ViewGroup) mView.getRootView()).addView(touchInterceptor);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        ((ViewGroup) mView.getRootView()).removeView(touchInterceptor);
        super.onResume();
    }

    //---------------------Action bar icons------------------------------

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_toolbar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionbar_logout:
                ParseUser.logOut();
                mAuth.signOut();

                Log.d("ProfileFragment", "Logged out successfully");
                Toast toast = Toast.makeText(mContext, "Logout successful", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
                toast.show();

                Intent i = new Intent(mContext, LoginActivity.class);
                startActivity(i);
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.new_deadline:
                Intent intent = new Intent(mContext, NewDeadlineDialog.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    //---------------------implementing interface-------------------------

    @Override
    public void updateItemRemoval(boolean remove) {
        if (remove) {
            isTimelineOpened = false;
            refresh();
        }
    }

    @Override
    public void updateItemComplete(boolean isComplete) {
        if (isComplete) {
            refresh();
        }
    }

    @Override
    public void updateList(boolean update) {
        if (update) {
            refresh();
        }
    }

    @Override
    public void getItemDetailOpened(boolean isOpened) {
        isTimelineOpened = isOpened;
    }

    @Override
    public void getDeadlineChecked(boolean isChanged) {
        if (isChanged) {
            dcInterfaceFromTimelineActivity.getDeadlineChecked(true);
        }
    }

    public static void setDcInterfaceFromTimelineActivity(GetDeadlineCheckedInterface dcInterface) {
        dcInterfaceFromTimelineActivity = dcInterface;
    }
}
