package me.juliasson.unipath.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.FrameLayout;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.R;
import me.juliasson.unipath.activities.MapActivity;
import me.juliasson.unipath.activities.SearchFilteringDialog;
import me.juliasson.unipath.adapters.CollegeAdapter;
import me.juliasson.unipath.adapters.MyExpandableListAdapter;
import me.juliasson.unipath.internal.GetCollegeAddedToFavList;
import me.juliasson.unipath.internal.GetItemDetailOpenedInterface;
import me.juliasson.unipath.internal.GetCollegeLikedOnSearchListView;
import me.juliasson.unipath.internal.LikedRefreshInterface;
import me.juliasson.unipath.internal.SearchInterface;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.rows.ParentRow;
import me.juliasson.unipath.utils.Constants;

import static android.app.Activity.RESULT_OK;

public class SearchFragment extends Fragment implements SearchInterface, LikedRefreshInterface, GetCollegeLikedOnSearchListView, GetItemDetailOpenedInterface {

    private FrameLayout touchInterceptor;

    private Context mContext;
    private SearchManager searchManager;
    private android.widget.SearchView searchView;
    private MyExpandableListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<ParentRow> parentList = new ArrayList<>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<>();
    private MenuItem searchItem;
    private Activity activity;
    private Context context;
    private View mView;

    private RecyclerView mRecyclerView;
    private ArrayList<College> colleges;
    private ArrayList<College> everyCollege;
    private ArrayList<College> filteredColleges;
    private ArrayList<College> refreshList;
    private CollegeAdapter collegeAdapter;
    private TextView notFound;

    private int sizeIndex = -1;
    private int inStateCostIndex = -1;
    private int outStateCostIndex = -1;
    private int acceptanceRateIndex = -1;
    private String stateValue;
    private boolean isDetailsOpened = false;

    private static GetCollegeAddedToFavList collegeListChangedInterface;

    private final String DEFAULT_MAX_VAL = "2147483647";
    private final String DEFAULT_MIN_VAL = "0";
    private static final int REQUEST_FILTER_CODE = 1034;

    private String query = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        mContext = parent.getContext();
        touchInterceptor = new FrameLayout(mContext);
        touchInterceptor.setClickable(true);
        View v = inflater.inflate(R.layout.fragment_search, parent, false);
        mView = v;
        //initFCM();
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
        notFound = (TextView) view.findViewById(R.id.tvNotFound);

        initViews();
        loadTopColleges();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
                search(searchView);
                break;
            case R.id.search_filter:
                Intent intent = new Intent(mContext, SearchFilteringDialog.class);
                startActivityForResult(intent, REQUEST_FILTER_CODE);
                break;
            case R.id.toggle_map:
                // The list of 'liked' colleges is can simply be sent to map activity
                Intent i = new Intent(mContext, MapActivity.class);
                Bundle bundle = new Bundle();
                if (filteredColleges == null) { filteredColleges = colleges; }
                bundle.putParcelableArrayList("favoritedList", filteredColleges);
                i.putExtras(bundle);
                startActivity(i);
                break;
        }
        return true;
    }

    @Override
    public void onPause() {
        if (touchInterceptor.getParent() == null && isDetailsOpened) {
            ((ViewGroup) mView.getRootView()).addView(touchInterceptor);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        ((ViewGroup) mView.getRootView()).removeView(touchInterceptor);
        super.onResume();
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
        LikedRefreshInterface lrInterface = this;
        SearchInterface sInterface = this;
        GetCollegeLikedOnSearchListView closlInterface = this;
        GetItemDetailOpenedInterface cdoInterface = this;
        collegeAdapter = new CollegeAdapter(colleges, sInterface, lrInterface, closlInterface, cdoInterface);
        mRecyclerView.setAdapter(collegeAdapter);
        postsQuery.orderByAscending(Constants.KEY_COLLEGE_NAME);

        postsQuery.findInBackground(new FindCallback<College>() {
            @Override
            public void done(List<College> objects, ParseException e) {
                if (e == null) {
                    //Toast.makeText(getActivity(), "Add Posts", Toast.LENGTH_SHORT).show();
                    Log.d("Search", Integer.toString(objects.size()));
                    for (int i = 0; i < objects.size(); i++) {
                        College college = objects.get(i);
                        colleges.add(college);
                        refreshList.add(college);
                        everyCollege.add(college);
                        collegeAdapter.notifyItemInserted(colleges.size() - 1);
                    }
                } else {
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
                searchRef(newText);
                return false;
            }
        });
    }

    public void refresh() {
        collegeAdapter.clearWithFilter();
        collegeAdapter.addAllFiltered(filteredColleges);
        collegeAdapter.clear();
        collegeAdapter.addAll(everyCollege);
        searchRef(query);
    }

    @Override
    public void setValues(ArrayList<College> filtered) {
        filteredColleges = filtered;
        if (filtered.isEmpty()) {
            displayNotFound();
        } else {
            hideNotFound();
        }
    }

    public void searchRef(String query) {
        this.query = query;

        if (collegeAdapter != null) {
            collegeAdapter.getFilter().filter(query.toLowerCase());
            Log.d("Search", query);
            System.out.print(query);
        }
        if (query.isEmpty()) {
            refreshList.clear();
            refreshList.addAll(colleges);
        } else {
            refreshList.clear();
            refreshList.addAll(filteredColleges);
        }
    }

    public static void setCollegeListChangedInterface(GetCollegeAddedToFavList collegeListChanged) {
        collegeListChangedInterface = collegeListChanged;
    }


    //----------------------------Filter Dialog Responses-------------------------------

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILTER_CODE && resultCode == RESULT_OK) {
            //assign filter values
            sizeIndex = data.getIntExtra(Constants.SIZE, 0);
            String sizeValue = assignSizeValue(sizeIndex);

            inStateCostIndex = data.getIntExtra(Constants.IN_STATE_COST, 0);
            String isCostValue = assignCostValue(inStateCostIndex);

            outStateCostIndex = data.getIntExtra(Constants.OUT_STATE_COST, 0);
            String osCostValue = assignCostValue(outStateCostIndex);

            acceptanceRateIndex = data.getIntExtra(Constants.ACCEPTANCE_RATE, 0);
            String acceptanceRateValue = assignAcceptanceRateValue(acceptanceRateIndex);

            stateValue = data.getStringExtra(Constants.STATE);

            //use new values to filter college list
            if (collegeAdapter != null) {
                /*
                CORRECT FORMAT TO PASS IN DATA FOR FILTERING:
                "lower_bound_pop upper_bound_pop, lower_bound_iscost upper_bound_iscost, lower_bound_oscost upper_bound_oscost, acceptance_rate, address"
                 */
                String filter_string = String.format("%s, %s, %s, %s, %s", sizeValue, isCostValue, osCostValue, acceptanceRateValue, stateValue);
                collegeAdapter.getSelectionFilter().filter(filter_string);
            }
        }
    }

    public String assignSizeValue(int sizeIndex) {
        switch (sizeIndex) {
            case 0:     //Any
                return String.format("%s %s", DEFAULT_MIN_VAL, DEFAULT_MAX_VAL);
            case 1:     //Small
                return String.format("%s %s", DEFAULT_MIN_VAL, "5000");
            case 2:     //Medium
                return String.format("%s %s", "5000", "15000");
            case 3:     //Large
                return String.format("%s %s", "15000", DEFAULT_MAX_VAL);
        }
        return null;
    }

    public String assignCostValue(int costIndex) {
        switch (costIndex) {
            case 0:     //Any
                return String.format("%s %s", DEFAULT_MIN_VAL, DEFAULT_MAX_VAL);
            case 1:     //<$20k
                return String.format("%s %s", DEFAULT_MIN_VAL, "20000");
            case 2:     //$20k-$40k
                return String.format("%s %s", "20000", "40000");
            case 3:     //>$40k
                return String.format("%s %s", "40000", DEFAULT_MAX_VAL);
        }
        return null;
    }

    public String assignAcceptanceRateValue(int acceptanceRateIndex) {
        switch (acceptanceRateIndex) {
            case 0:
                return DEFAULT_MIN_VAL;
            case 1:
                return "5";
            case 2:
                return "10";
            case 3:
                return "15";
            case 4:
                return "20";
            case 5:
                return "25";
            case 6:
                return "30";
            case 7:
                return "35";
            case 8:
                return "40";
            case 9:
                return "45";
            case 10:
                return "50";
            case 11:
                return "55";
            case 12:
                return "60";
            case 13:
                return "65";
            case 14:
                return "70";
            case 15:
                return "75";
            case 16:
                return "80";
            case 17:
                return "85";
            case 18:
                return "90";
            case 19:
                return "95";
        }
        return null;
    }

    @Override
    public void setValues(boolean isChanged) {
        if (isChanged) {
            collegeListChangedInterface.getCollegeListChanged(true);
            refresh();
        }
    }

    @Override
    public void getCollegeLikedOnSearchListView(boolean isChanged) {
        if (isChanged) {
            collegeListChangedInterface.getCollegeListChanged(true);
        }
    }

    public void displayNotFound() {
        notFound.setVisibility(View.VISIBLE);
    }

    private void hideNotFound() {
        notFound.setVisibility(View.INVISIBLE);
    }

    @Override
    public void getItemDetailOpened(boolean isOpened) {
        isDetailsOpened = isOpened;
    }

}