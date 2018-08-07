package me.juliasson.unipath.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;

public class GeneralInfoFragment extends Fragment{

    private TextView tvWebsite;
    private TextView tvAddress;
    private TextView tvStudentPopulation;
    private TextView tvOutOfSateCost;
    private TextView tvInStateCost;
    private TextView tvAcceptanceRate;

    private College college;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_general_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        college = getArguments().getParcelable(College.class.getSimpleName());

        tvWebsite = (TextView) view.findViewById(R.id.tvWebsite);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        tvStudentPopulation = (TextView) view.findViewById(R.id.tvStudentPopulation);
        tvOutOfSateCost = (TextView) view.findViewById(R.id.tvCostOutState);
        tvInStateCost = (TextView) view.findViewById(R.id.tvCostInState);
        tvAcceptanceRate = (TextView) view.findViewById(R.id.tvAcceptanceRate);

        SpannableString content = new SpannableString(college.getCollegeName());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvWebsite.setText(content);
        tvWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(college.getWebsite()));
                startActivity(browser);
            }
        });

        tvAddress.setText(college.getAddress());
        tvStudentPopulation.setText(String.format("%s students", Integer.toString(college.getStudentPopulation())));
        tvInStateCost.setText(String.format("$%s", Integer.toString(college.getInStateCost())));
        tvOutOfSateCost.setText(String.format("$%s", Integer.toString(college.getOutOfStateCost())));
        tvAcceptanceRate.setText(String.format("%s%%", Double.toString(college.getAcceptanceRate())));
    }
}
