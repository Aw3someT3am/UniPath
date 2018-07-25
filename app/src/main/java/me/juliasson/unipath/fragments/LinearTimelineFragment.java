package me.juliasson.unipath.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.TimeLineAdapter;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.model.OrderStatus;
import me.juliasson.unipath.model.TimeLine;
import me.juliasson.unipath.model.UserDeadlineRelation;

public class LinearTimelineFragment extends Fragment {

    private SwipeRefreshLayout swipeContainer;
    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLine> mDataList = new ArrayList<>();
    private HashSet<TimeLine> mDataSet = new HashSet<>();
    private Date currentDate;

    private static final String KEY_USER = "user";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_linear_timeline, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        currentDate = Calendar.getInstance().getTime();

        //find the swipe container
        swipeContainer = view.findViewById(R.id.swipeContainer);
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

        initView();
    }

    private void initView() {
        mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);
        setDataListItems();
    }

    private void setDataListItems(){
        //ParseQuery go through each of the current user's deadlines and add them.
        UserDeadlineRelation.Query udQuery = new UserDeadlineRelation.Query();
        udQuery.getTop().withUser().withDeadline();
        udQuery.whereEqualTo(KEY_USER, ParseUser.getCurrentUser());

        udQuery.findInBackground(new FindCallback<UserDeadlineRelation>() {
            @Override
            public void done(List<UserDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        UserDeadlineRelation relation = objects.get(i);
                        Deadline deadline = relation.getDeadline();
                        String description = deadline.getDescription();
                        Date date = deadline.getDeadlineDate();
                        if (currentDate.after(date) && relation.getCompleted()) {
                            mDataSet.add(new TimeLine(description, date.toString(), date, OrderStatus.INACTIVE));
                        } else if (currentDate.after(date) && !relation.getCompleted()) {
                            mDataSet.add(new TimeLine(description, date.toString(), date, OrderStatus.MISSED));
                        }
                        else {
                            mDataSet.add(new TimeLine(description, date.toString(), date, OrderStatus.ACTIVE));
                        }
                    }
                    mDataList.addAll(mDataSet);
                    sortData();
                } else {
                    e.printStackTrace();
                }
            }
        });
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
        mDataSet.clear();
        setDataListItems();
        mTimeLineAdapter.addAll(mDataList);
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }

}
