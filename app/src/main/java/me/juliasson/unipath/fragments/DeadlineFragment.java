package me.juliasson.unipath.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.CollegeDeadlineRelation;

public class DeadlineFragment extends Fragment{
    TextView tvCollegeName;
    TextView tvEarlyDeadline;
    TextView tvRegularDeadline;
    College college;
    CollegeDeadlineRelation collegeDeadlineRelation;

    private ArrayList<College> colleges;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragmnet_deadlines, container, false);

        //final CollegeDeadlineRelation.Query postsQuery = new ImagePost.Query();

        tvEarlyDeadline = (TextView) rootView.findViewById(R.id.tvDescription);
        tvRegularDeadline = (TextView) rootView.findViewById(R.id.tvRegular);

        college = getArguments().getParcelable("college");

        return rootView;
    }
}
