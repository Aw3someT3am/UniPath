package me.juliasson.unipath.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.CollegeAdapter;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.CollegeDeadlineRelation;
import me.juliasson.unipath.model.UserCollegeRelation;
import me.juliasson.unipath.model.UserDeadlineRelation;
import me.juliasson.unipath.utils.DateTimeUtils;

public class FavoritesFragment extends Fragment {

    CollegeAdapter collegeAdapter;
    ArrayList<College> colleges;
    RecyclerView rvColleges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_favorites, parent, false);
    }

    //---------------QUERIES----------------

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        rvColleges = view.findViewById(R.id.rvCollegeList);
        colleges = new ArrayList<>();
        collegeAdapter = new CollegeAdapter(colleges);

    }

    public void queryUDRelation() {
        final UserDeadlineRelation.Query udRelationQuery = new UserDeadlineRelation.Query();
        udRelationQuery.getTop().withUser().withDeadline();

        udRelationQuery.findInBackground(new FindCallback<UserDeadlineRelation>() {
            @Override
            public void done(List<UserDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); i++) {
                        UserDeadlineRelation relation = objects.get(i);
                        String userName = relation.getUser().getString("firstName");
                        String deadline = DateTimeUtils.parseDateTime(relation.getDeadline().getDeadlineDate().toString(), "EEE MMM dd HH:mm:ss ZZZZZ yyyy", "dd-MMM-yyyy");
                        Log.d("FavFrag", String.format("%s has a deadline on %s", userName, deadline));
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void queryUCRelation() {
        final UserCollegeRelation.Query ucRelationQuery = new UserCollegeRelation.Query();
        ucRelationQuery.getTop().withCollege().withUser();

        ucRelationQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); i++) {
                        UserCollegeRelation relation = objects.get(i);
                        String collegeName = relation.getCollege().getCollegeName();
                        String userName = relation.getUser().getString("firstName");
                        Log.d("FavFrag", String.format("%s is interested in %s", userName, collegeName));
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void queryCDRelation() {
        final CollegeDeadlineRelation.Query cdRelationQuery = new CollegeDeadlineRelation.Query();
        cdRelationQuery.getTop().withCollege().withDeadline();

        cdRelationQuery.findInBackground(new FindCallback<CollegeDeadlineRelation>() {
            @Override
            public void done(List<CollegeDeadlineRelation> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); i++) {
                        CollegeDeadlineRelation relation = objects.get(i);
                        String collegeName = relation.getCollege().getCollegeName();
                        String deadline = DateTimeUtils.parseDateTime(relation.getDeadline().getDeadlineDate().toString(), "EEE MMM dd HH:mm:ss ZZZZZ yyyy", "dd-MMM-yyyy");
                        //String deadline = relation.getDeadline().getDeadlineDate().toString();
                        Log.d("FavFrag", String.format("%s has due date %s", collegeName, deadline));
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
