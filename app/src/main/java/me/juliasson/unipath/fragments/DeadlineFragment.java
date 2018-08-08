package me.juliasson.unipath.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.DeadlineAdapter;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.CollegeDeadlineRelation;
import me.juliasson.unipath.model.Deadline;
import me.juliasson.unipath.utils.Constants;

public class DeadlineFragment extends Fragment{

    private Context mContext;
    private RecyclerView mRecyclerView;
    private DeadlineAdapter mDeadlineAdapter;
    private ProgressBar pbProgress;
    private ArrayList<Deadline> mDeadlineList = new ArrayList<>();

    College college;
    //CollegeDeadlineRelation collegeDeadlineRelation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        mContext = parent.getContext();
        return inflater.inflate(R.layout.fragment_deadlines, parent, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        college = getArguments().getParcelable(College.class.getSimpleName());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvDeadlines);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);

        pbProgress = view.findViewById(R.id.pbProgress);

        initView();
    }

    private void initView() {
        setDeadlineListItems();
        mDeadlineAdapter = new DeadlineAdapter(mDeadlineList);
        mRecyclerView.setAdapter(mDeadlineAdapter);
        mDeadlineAdapter.clear();
    }

    private void setDeadlineListItems(){
        pbProgress.setVisibility(ProgressBar.VISIBLE);
        //ParseQuery go through each of the current user's deadlines and add them.
        CollegeDeadlineRelation.Query cdQuery = new CollegeDeadlineRelation.Query();
        cdQuery.getTop().withCollege().withDeadline();
        cdQuery.whereEqualTo(Constants.KEY_COLLEGE, college);

        cdQuery.findInBackground(new FindCallback<CollegeDeadlineRelation>() {
            @Override
            public void done(List<CollegeDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        CollegeDeadlineRelation relation = objects.get(i);
                        Deadline deadline = relation.getDeadline();
                        mDeadlineList.add(deadline);
                        mDeadlineAdapter.notifyItemInserted(mDeadlineList.size()-1);
                        pbProgress.setVisibility(ProgressBar.INVISIBLE);
                    }
                } else {
                    Toast toast = Toast.makeText(mContext, "Failed to load deadlines", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
                    toast.show();
                    Log.d("DeadlineFragment", "Failed to load deadlines");
                    e.printStackTrace();
                }
            }
        });
    }
}
