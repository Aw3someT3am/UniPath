package me.juliasson.unipath.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;

public class CalculatorFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private Spinner spCitizenship;
    private Spinner spStateResidence;
    private EditText etParentIncome;
    private EditText etStudentAssets;
    private EditText etParentAssets;
    private EditText etEFC;
    private Button btnCalculate;
    private TextView tvTotal;

    private Context mContext;

    private ArrayAdapter<CharSequence> citizenshipAdapter;
    private ArrayAdapter<CharSequence> stateResidenceAdapter;

    private int parentIncome;
    private int studentAssets;
    private int parentAssets;
    private int efc;

    private String citzenshipStatus;
    private String state;

    private College college;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = container.getContext();
        return inflater.inflate(R.layout.fragment_calculator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        college = (College) getArguments().getParcelable(College.class.getSimpleName());

        spCitizenship = (Spinner) view.findViewById(R.id.spCitizenship);
        spCitizenship.setOnItemSelectedListener(this);
        spStateResidence = (Spinner) view.findViewById(R.id.spStateResidence);
        spStateResidence.setOnItemSelectedListener(this);
        etParentIncome = (EditText) view.findViewById(R.id.etParentIncome);
        etStudentAssets = (EditText) view.findViewById(R.id.etStudentAsset);
        etParentAssets = (EditText) view.findViewById(R.id.etParentAsset);
        etEFC = (EditText) view.findViewById(R.id.etEFC);
        btnCalculate = (Button) view.findViewById(R.id.btnTotalNet);
        tvTotal = (TextView) view.findViewById(R.id.tvTotalNetCost);

        citizenshipAdapter = ArrayAdapter.createFromResource(mContext, R.array.citizenship_status, R.layout.item_search_filter_spinner);
        stateResidenceAdapter = ArrayAdapter.createFromResource(mContext, R.array.state_options, R.layout.item_search_filter_spinner);

        citizenshipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateResidenceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCitizenship.setAdapter(citizenshipAdapter);
        spStateResidence.setAdapter(stateResidenceAdapter);

        final String address = college.getAddress();

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parentIncome = (Integer.parseInt((etParentIncome.getText().toString().equals("") || !etParentIncome.getText().toString().matches("^[0-9]+$") ? "0" : etParentIncome.getText().toString())));
                studentAssets = Integer.parseInt((etStudentAssets.getText().toString().equals("") || !etStudentAssets.getText().toString().matches("^[0-9]+$") ? "0" : etStudentAssets.getText().toString()));
                parentAssets = Integer.parseInt((etParentAssets.getText().toString().equals("") || !etParentAssets.getText().toString().matches("^[0-9]+$") ? "0" : etParentAssets.getText().toString()));
                efc = Integer.parseInt((etEFC.getText().toString().equals("") || !etEFC.getText().toString().matches("^[0-9]+$") ? "0" : etEFC.getText().toString()));
                int netCost = parentIncome + studentAssets + parentAssets + efc;
                parentIncome = (int) Math.round(parentIncome + parentIncome*0.5);
                int pellGrant = 5000;
                int cost = 0;
                if(address.contains(String.format(", %s", state))) {
                    cost = college.getInStateCost();
                } else {
                    cost = college.getOutOfStateCost();
                }
//                tvTotal.setText(Integer.toString(netCost));
                tvTotal.setText(Integer.toString(cost - parentIncome - studentAssets - parentAssets - pellGrant));
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(adapterView.getId()) {
            case R.id.spCitizenship:
                citzenshipStatus = spCitizenship.getItemAtPosition(i).toString();
                break;
            case R.id.spStateResidence:
                state = adapterView.getItemAtPosition(i).toString();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
