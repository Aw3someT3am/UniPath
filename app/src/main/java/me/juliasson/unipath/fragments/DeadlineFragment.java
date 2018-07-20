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

        final CollegeDeadlineRelation.Query deadlineQuery = new CollegeDeadlineRelation.Query();

        deadlineQuery.whereEqualTo("college",college);

//        deadlineQuery.findInBackground(new FindCallback<CollegeDeadlineRelation>() {
//            @Override
//            public void done(List<CollegeDeadlineRelation> objects, ParseException e) {
//                if (e == null){
//                    //Toast.makeText(getActivity(), "Add Posts", Toast.LENGTH_SHORT).show();
//                    Log.d("HomeActivity", Integer.toString(objects.size()));
//                    for(int i = 0; i < objects.size(); i++) {
////                        Log.d("HomeActivity", "Post[" + i + "] = " + objects.get(i).getDescription()
////                                + "\nusername = " + objects.get(i).getUser().getUsername());
////                        posts.addAll(objects);
////                        postAdapter.notifyDataSetChanged();
//                    }
////                    postAdapter.clear();
////                    postAdapter.addAll(objects);
////                    postAdapter.notifyDataSetChanged();
//                } else {
//                    //Toast.makeText(getActivity(), "null?", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        tvEarlyDeadline = (TextView) rootView.findViewById(R.id.tvDescription);
//        tvRegularDeadline = (TextView) rootView.findViewById(R.id.tvRegular);

        college = getArguments().getParcelable("college");

        return rootView;
    }
}
