package me.juliasson.unipath.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
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
import me.juliasson.unipath.adapters.CollegeAdapter;
import me.juliasson.unipath.adapters.MyExpandableListAdapter;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.rows.ChildRow;
import me.juliasson.unipath.rows.ParentRow;

public class SearchFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{
    private SearchManager searchManager;
    private android.widget.SearchView searchView;
    private MyExpandableListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<ParentRow> parentList = new ArrayList<>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<>();
    private MenuItem searchItem;
    private Activity activity;
    private Context context;


    private RecyclerView mRecyclerView;
    private ArrayList<College> colleges;
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

        //android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) activity.findViewById(R.id.toolbar);
        //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);

        colleges = new ArrayList<>();


        initViews();
        loadTopColleges();


//        parentList = new ArrayList<>();
//        showTheseParentList = new ArrayList<>();
//
//        // The app will crash if display list is not called here.
//        displayList();
//
//        // This expands the list.
//        expandAll();
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

//        search(searchView);
//        inflater.inflate(R.menu.menu_search, menu);
//        searchItem = menu.findItem(R.id.action_search);
//        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
//        searchView.setIconifiedByDefault(false);
//        searchView.setOnQueryTextListener(this);
//        searchView.setOnCloseListener(this);
//        searchView.requestFocus();
//        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onClose() {
        listAdapter.filterData("");
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        listAdapter.filterData(s);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        listAdapter.filterData(s);
        expandAll();
        return false;
    }

    private void loadData() {
        ArrayList<ChildRow> childRows =  new ArrayList<>();
        ParentRow parentRow = null;

        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Harvard University"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "University of Illinois, Urbana-Champaign"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Stanford University"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "University of California, Berkeley"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Yale"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Princeton University"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Massachusetts Institute of Technology"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "California Institute of Technology"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "University of Pennsylvania"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Duke University"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Brown University"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Claremont McKenna College"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Dartmouth College"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Williams College"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Columbia University"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Cornell University"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Amherst College"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Harvey Mudd College"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Swarthmore Collegeo"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "United States Naval Academy"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Georgetown University"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "Rice University"));



        //parentRow = new ParentRow("First Group", childRows);
        //parentList.add(parentRow);

        //childRows = new ArrayList<>();

        parentRow = new ParentRow("Colleges", childRows);
        parentList.add(parentRow);
    }

    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i =0; i < count; i+=1) {
            myList.expandGroup(i);
        }
    }


    private void initViews(){
        mRecyclerView = (RecyclerView) activity.findViewById(R.id.card_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void loadTopColleges() {
        Toast.makeText(getActivity(), "Loading colleges!", Toast.LENGTH_SHORT).show();
        final College.Query postsQuery = new College.Query();
        postsQuery.limit20();
        collegeAdapter = new CollegeAdapter(colleges);
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
                        collegeAdapter.notifyItemInserted(colleges.size() - 1);
                    }
                } else {
                    //Toast.makeText(getActivity(), "null?", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (collegeAdapter != null) collegeAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }
}