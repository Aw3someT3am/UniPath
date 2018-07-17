package me.juliasson.unipath;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GeneralInfoFragment extends Fragment{
    TextView tvCollegeName;
    TextView tvDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragmnet_general_info, container, false);

        tvCollegeName = (TextView) rootView.findViewById(R.id.tvCollegeName);
        tvDescription = (TextView) rootView.findViewById(R.id.tvDescription);

        return rootView;
    }
}
