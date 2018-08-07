package me.juliasson.unipath.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import me.juliasson.unipath.R;
import me.juliasson.unipath.model.College;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context mContext;

    private TextView tvCollegeName;
    private TextView tvAddress;
    private ImageView smallImage;
    private College college;

    private final static String KEY_COLLEGE_IMAGE = "image";

    public CustomInfoWindowAdapter(Context ctx) {
        mContext = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.map_info_window, null);

        tvCollegeName = view.findViewById(R.id.tvCollegeName);
        tvAddress = view.findViewById(R.id.tvAddress);

        tvCollegeName.setText(marker.getTitle());
        tvAddress.setText(marker.getSnippet());

        return view;
    }
}
