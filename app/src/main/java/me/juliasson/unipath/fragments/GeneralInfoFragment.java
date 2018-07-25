package me.juliasson.unipath.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;

public class GeneralInfoFragment extends Fragment{
    TextView tvAddress;
    TextView tvStudentPopulation;
    TextView tvOutOfSateCost;
    TextView tvInStateCost;
    TextView tvAcceptanceRate;

    College college;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_general_info, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        college = getArguments().getParcelable(College.class.getSimpleName());

        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        tvStudentPopulation = (TextView) view.findViewById(R.id.tvStudentPopulation);
        tvOutOfSateCost = (TextView) view.findViewById(R.id.tvCostOutState);
        tvInStateCost = (TextView) view.findViewById(R.id.tvCostInState);
        tvAcceptanceRate = (TextView) view.findViewById(R.id.tvAcceptanceRate);

        tvAddress.setText(college.getAddress());
        tvStudentPopulation.setText(String.format("%s total students", Integer.toString(college.getStudentPopulation())));
        tvInStateCost.setText(String.format("$%s", Integer.toString(college.getInStateCost())));
        tvOutOfSateCost.setText(String.format("$%s", Integer.toString(college.getOutOfStateCost())));
        tvAcceptanceRate.setText(String.format("%s%%", Double.toString(college.getAccepatnceRate())));
    }
}
