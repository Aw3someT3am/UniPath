package me.juliasson.unipath.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import me.juliasson.unipath.R;
import me.juliasson.unipath.utils.Constants;
import me.juliasson.unipath.utils.GetFilterChoicesInterface;

public class SearchFilteringDialog extends AppCompatActivity implements AdapterView.OnItemSelectedListener, GetFilterChoicesInterface {

    private Spinner spSize;
    private Spinner spInStateCost;
    private Spinner spOutStateCost;
    private Spinner spAcceptanceRate;
    private Spinner spState;
    private Button bvSearch;

    private ArrayAdapter<CharSequence> sizeAdapter;
    private ArrayAdapter<CharSequence> inStateCostAdapter;
    private ArrayAdapter<CharSequence> OutStateCostAdapter;
    private ArrayAdapter<CharSequence> acceptanceRateAdapter;
    private ArrayAdapter<CharSequence> stateAdapter;

    private int size = 0;
    private int inStateCost = 0;
    private int outStateCost = 0;
    private int acceptanceRate = 0;
    private String state = Constants.STATE_DEFAULT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_search_filtering);
        this.setFinishOnTouchOutside(true);

        setSize();

        spSize = findViewById(R.id.spCollegeSize);
        spInStateCost = findViewById(R.id.spInStateCost);
        spOutStateCost = findViewById(R.id.spOutStateCost);
        spAcceptanceRate = findViewById(R.id.spAcceptanceRate);
        spState = findViewById(R.id.spState);
        bvSearch = findViewById(R.id.bvSearch);

        sizeAdapter = ArrayAdapter.createFromResource(this, R.array.size_options, R.layout.item_search_filter_spinner);
        inStateCostAdapter = ArrayAdapter.createFromResource(this, R.array.cost_options, R.layout.item_search_filter_spinner);
        OutStateCostAdapter = ArrayAdapter.createFromResource(this, R.array.cost_options, R.layout.item_search_filter_spinner);
        acceptanceRateAdapter = ArrayAdapter.createFromResource(this, R.array.acceptance_options, R.layout.item_search_filter_spinner);
        stateAdapter = ArrayAdapter.createFromResource(this, R.array.state_options, R.layout.item_search_filter_spinner);

        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inStateCostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        OutStateCostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acceptanceRateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spSize.setAdapter(sizeAdapter);
        spInStateCost.setAdapter(inStateCostAdapter);
        spOutStateCost.setAdapter(OutStateCostAdapter);
        spAcceptanceRate.setAdapter(acceptanceRateAdapter);
        spState.setAdapter(stateAdapter);

        bvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFilters(size, inStateCost, outStateCost, acceptanceRate, state);
            }
        });
    }

    private void setSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int height = metrics.heightPixels;

        getWindow().setLayout(ActionBar.LayoutParams.MATCH_PARENT, (3 * height)/4);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //you can retrieve item selected using parent.getItemAtPosition(position)
        switch(parent.getId()) {
            case R.id.spCollegeSize:
                size = position;
                break;
            case R.id.spInStateCost:
                inStateCost = position;
                break;
            case R.id.spOutStateCost:
                outStateCost = position;
                break;
            case R.id.spAcceptanceRate:
                acceptanceRate = position;
                break;
            case R.id.spState:
                state = parent.getItemAtPosition(position).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void getFilters(int size, int inStateCost, int outStateCost, int acceptanceRate, String state) {
        Intent intent = new Intent();
        intent.putExtra(Constants.SIZE, size);
        intent.putExtra(Constants.IN_STATE_COST, inStateCost);
        intent.putExtra(Constants.OUT_STATE_COST, outStateCost);
        intent.putExtra(Constants.ACCEPTANCE_RATE, acceptanceRate);
        intent.putExtra(Constants.STATE, state);
        setResult(RESULT_OK);
        finish();
    }
}
