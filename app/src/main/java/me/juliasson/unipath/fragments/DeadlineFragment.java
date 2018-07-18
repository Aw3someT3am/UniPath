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

public class DeadlineFragment extends Fragment{
    TextView tvCollegeName;
    TextView tvEarlyDeadline;
    TextView tvRegularDeadline;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragmnet_general_info, container, false);

        tvCollegeName = (TextView) rootView.findViewById(R.id.tvCollegeName);
        tvEarlyDeadline = (TextView) rootView.findViewById(R.id.tvDescription);
        tvRegularDeadline = (TextView) rootView.findViewById(R.id.tvRegular);

        return rootView;
    }
}
