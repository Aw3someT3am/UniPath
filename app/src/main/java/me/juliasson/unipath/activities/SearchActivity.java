package me.juliasson.unipath.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import java.util.ArrayList;

import me.juliasson.unipath.ChildRow;
import me.juliasson.unipath.rows.ParentRow;
import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.MyExpandableListAdapter;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener{
    private SearchManager searchManager;
    private android.widget.SearchView searchView;
    private MyExpandableListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<ParentRow> parentList = new ArrayList<>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<>();
    private MenuItem searchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        parentList = new ArrayList<>();
        showTheseParentList = new ArrayList<>();

        // The app will crash if display list is not called here.
        displayList();

        // This expands the list.
        expandAll();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
        searchView.requestFocus();

        return true;
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

        childRows.add(new ChildRow(R.mipmap.ic_launcher, "New list in search"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "college"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "A new Emoticon"));
        //parentRow = new ParentRow("First Group", childRows);
        //parentList.add(parentRow);

        //childRows = new ArrayList<>();

        childRows.add(new ChildRow(R.mipmap.ic_launcher, "YEAH"));
        childRows.add(new ChildRow(R.mipmap.ic_launcher, "A"));

        parentRow = new ParentRow("Second Group", childRows);
        parentList.add(parentRow);
    }

    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i =0; i < count; i+=1) {
            myList.expandGroup(i);
        }
    }

    private void displayList() {
        loadData();

        myList = (ExpandableListView) findViewById(R.id.expandableListView_search);
        listAdapter = new MyExpandableListAdapter(SearchActivity.this, parentList);

        myList.setAdapter(listAdapter);
    }
}
