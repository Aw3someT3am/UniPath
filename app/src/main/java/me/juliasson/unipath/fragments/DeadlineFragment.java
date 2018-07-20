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

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.DeadlineAdapter;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.CollegeDeadlineRelation;
import me.juliasson.unipath.model.Deadline;

public class DeadlineFragment extends Fragment{

    private RecyclerView mRecyclerView;
    private DeadlineAdapter mDeadlineAdapter;
    private ArrayList<Deadline> mDeadlineList = new ArrayList<>();
    private Context mContext;

    College college;
    //CollegeDeadlineRelation collegeDeadlineRelation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragmnet_deadlines, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mContext = view.getContext();

        college = getArguments().getParcelable(College.class.getSimpleName());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvDeadlines);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        initView();
    }

    private void initView() {
        setDeadlineListItems();
        mDeadlineAdapter = new DeadlineAdapter(mDeadlineList);
        mRecyclerView.setAdapter(mDeadlineAdapter);
        mDeadlineAdapter.clear();
    }

    private void setDeadlineListItems(){
        //ParseQuery go through each of the current user's deadlines and add them.
        CollegeDeadlineRelation.Query cdQuery = new CollegeDeadlineRelation.Query();
        cdQuery.getTop().withCollege().withDeadline();
        cdQuery.whereEqualTo("college", college);

        cdQuery.findInBackground(new FindCallback<CollegeDeadlineRelation>() {
            @Override
            public void done(List<CollegeDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        CollegeDeadlineRelation relation = objects.get(i);
                        Deadline deadline = relation.getDeadline();
                        mDeadlineList.add(deadline);
                        mDeadlineAdapter.notifyItemInserted(mDeadlineList.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
