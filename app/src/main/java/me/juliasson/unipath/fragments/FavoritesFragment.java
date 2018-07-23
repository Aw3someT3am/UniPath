package me.juliasson.unipath.fragments;

import android.os.Bundle;
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
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.CollegeAdapter;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.model.UserCollegeRelation;

public class FavoritesFragment extends Fragment {

    private SwipeRefreshLayout swipeContainer;
    CollegeAdapter collegeAdapter;
    ArrayList<College> colleges;
    RecyclerView rvColleges;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_favorites, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        rvColleges = view.findViewById(R.id.rvCollegeList);
        colleges = new ArrayList<>();
        collegeAdapter = new CollegeAdapter(colleges);
        rvColleges.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvColleges.setAdapter(collegeAdapter);

        loadFavoriteColleges();

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
    }

    public void loadFavoriteColleges() {
        final UserCollegeRelation.Query ucRelationQuery = new UserCollegeRelation.Query();
        ucRelationQuery.getTop().withCollege().withUser();

        ucRelationQuery.findInBackground(new FindCallback<UserCollegeRelation>() {
            @Override
            public void done(List<UserCollegeRelation> objects, ParseException e) {
                if (e == null) {
                    for(int i = 0; i < objects.size(); i++) {
                        UserCollegeRelation relation = objects.get(i);
                        if (ParseUser.getCurrentUser().getObjectId().equals(relation.getUser().getObjectId())) {
                            College college = relation.getCollege();
                            colleges.add(college);
                            collegeAdapter.notifyItemInserted(colleges.size()-1);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void refresh() {
        collegeAdapter.clear();
        loadFavoriteColleges();
        collegeAdapter.addAll(colleges);
        // Now we call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }
}
