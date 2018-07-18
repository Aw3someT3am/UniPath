package me.juliasson.unipath.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.CollegeDeadlineRelation;
import me.juliasson.unipath.utils.DateTimeUtils;

public class FavoritesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_favorites, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        final ParseUser currentUser = ParseUser.getCurrentUser();


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
