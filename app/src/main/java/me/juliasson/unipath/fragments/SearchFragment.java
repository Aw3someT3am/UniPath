package me.juliasson.unipath.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.SearchInterface;
import me.juliasson.unipath.adapters.CollegeAdapter;
import me.juliasson.unipath.adapters.MyExpandableListAdapter;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.rows.ParentRow;

public class SearchFragment extends Fragment implements SearchInterface {
    private SearchManager searchManager;
    private android.widget.SearchView searchView;
    private MyExpandableListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<ParentRow> parentList = new ArrayList<>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<>();
    private MenuItem searchItem;
    private Activity activity;
    private Context context;

    private SwipeRefreshLayout swipeContainerSearch;
    private RecyclerView mRecyclerView;
    private ArrayList<College> colleges;
    private ArrayList<College> everyCollege;
    private ArrayList<College> filteredColleges;
    private ArrayList<College> refreshList;
    private CollegeAdapter collegeAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View v = inflater.inflate(R.layout.fragment_search, parent, false);
        return v;
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        activity = getActivity();
        context = view.getContext();
        searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
        colleges = new ArrayList<>();
        everyCollege = new ArrayList<>();
        refreshList = new ArrayList<>();

        initViews();
        loadTopColleges();

        swipeContainerSearch = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainerSearch);
        // Setup refresh listener which triggers new data loading
        swipeContainerSearch.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                refresh();
            }
        });
        // Configure the refreshing colors
        swipeContainerSearch.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
    }


    private void initViews(){
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void loadTopColleges() {
        final College.Query postsQuery = new College.Query();
        postsQuery.limit20();
        collegeAdapter = new CollegeAdapter(colleges, this);
        mRecyclerView.setAdapter(collegeAdapter);
        postsQuery.orderByDescending("createdAt");
        postsQuery.findInBackground(new FindCallback<College>() {
            @Override
            public void done(List<College> objects, ParseException e) {
                if (e == null){
                    //Toast.makeText(getActivity(), "Add Posts", Toast.LENGTH_SHORT).show();
                    Log.d("Search", Integer.toString(objects.size()));
                    for(int i = 0; i < objects.size(); i++) {
                        College college = objects.get(i);
                        colleges.add(college);
                        refreshList.add(college);
                        everyCollege.add(college);
                        collegeAdapter.notifyItemInserted(colleges.size() - 1);
                    }
                } else {
                    //Toast.makeText(getActivity(), "null?", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private String temp = "";
    private void search(SearchView searchView) {
        Toast.makeText(getContext(), "here it is:"+ temp, Toast.LENGTH_LONG).show();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchRef(newText);
                return false;
            }
        });
    }

    public void refresh() {
        searchRef(temp);
        collegeAdapter.clearWithFilter();
        collegeAdapter.addAllFiltered(refreshList);
        collegeAdapter.clear();
        collegeAdapter.addAll(everyCollege);
        swipeContainerSearch.setRefreshing(false);
    }

    @Override
    public void setValues(ArrayList<College> filtered) {
        filteredColleges = filtered;
    }

    public void searchRef(String query) {
        temp = query;
        //Toast.makeText(getContext(), query, Toast.LENGTH_LONG).show();
        if (collegeAdapter != null) {
            collegeAdapter.getFilter().filter(query.toLowerCase());
            Log.d("Search",query);
            System.out.print(query);
        }
        if(query.isEmpty()) {
            refreshList.clear();
            refreshList.addAll(colleges);
//            Toast.makeText(getContext(), "empty", Toast.LENGTH_LONG).show();
        } else {
            refreshList.clear();
            refreshList.addAll(filteredColleges);
//            Toast.makeText(getContext(), "Notempty"Notempty, Toast.LENGTH_LONG).show();
        }
    }
}