package me.juliasson.unipath.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.TimeLineAdapter;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.model.OrderStatus;
import me.juliasson.unipath.model.TimeLine;
import me.juliasson.unipath.model.UserDeadlineRelation;

public class LinearTimelineFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private TimeLineAdapter mTimeLineAdapter;
    private List<TimeLine> mDataList = new ArrayList<>();
    private Context mContext;

    private static final String KEY_USER = "user";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_linear_timeline, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mContext = view.getContext();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        initView();
    }

    private void initView() {
        setDataListItems();
        mTimeLineAdapter = new TimeLineAdapter(mDataList);
        mRecyclerView.setAdapter(mTimeLineAdapter);
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
                        mDataList.add(new TimeLine(description, date.toString(), relation.getCompleted() ? OrderStatus.COMPLETED : OrderStatus.ACTIVE));
                        mTimeLineAdapter.notifyItemInserted(mDataList.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
