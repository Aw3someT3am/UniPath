package me.juliasson.unipath.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.BounceInterpolator;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import me.juliasson.unipath.Manifest;
import me.juliasson.unipath.R;
import me.juliasson.unipath.adapters.CollegeAdapter;
import me.juliasson.unipath.adapters.CustomInfoWindowAdapter;
import me.juliasson.unipath.model.College;
import me.juliasson.unipath.utils.Constants;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

@RuntimePermissions
public class MapActivity extends AppCompatActivity implements
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerClickListener {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private ArrayList<College> everyCollege;
    private CollegeAdapter collegeAdapter;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private final static String KEY_LOCATION = "location";
    private static final LatLng center_us = new LatLng(39.809860, -98.555183);

    private List<LatLng> mLocationsList = new ArrayList<>();
    private ArrayList<College> filteredColleges;

    private int sizeIndex = -1;
    private int inStateCostIndex = -1;
    private int outStateCostIndex = -1;
    private int acceptanceRateIndex = -1;
    private String stateValue;
    private final String DEFAULT_MAX_VAL = "2147483647";
    private final String DEFAULT_MIN_VAL = "0";
    private static final int REQUEST_FILTER_CODE = 1034;
    private String query = "";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
//        getSupportActionBar().hide();

        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key ya dummy");
        }
        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    loadMap(map);
                }
            });
        } else {
            Toast toast = Toast.makeText(this, "Error - Map Fragment was null", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        String code = getIntent().getStringExtra("Itsa me, Mario!");

        if (code != null && code.equals("Nighty nighty. Ah spaghetti. Ah, ravioli. Ahh, mama mia.")) {
            getMenuInflater().inflate(R.menu.menu_map_search, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
//                SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
//                search(searchView);
                break;
            case R.id.search_filter:
                Intent intent = new Intent(MapActivity.this, SearchFilteringDialog.class);
                startActivityForResult(intent, REQUEST_FILTER_CODE);
                break;
            case R.id.list:
                finish();
                break;
        }
        return true;

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

    public void searchRef(String query) {
        this.query = query;

        if (collegeAdapter != null) {
            collegeAdapter.getFilter().filter(query.toLowerCase());
            Log.d("Search", query);
            System.out.print(query);
        }
        if (query.isEmpty()) {
//            refreshList.clear();
//            refreshList.addAll(colleges);
        } else {
//            refreshList.clear();
//            refreshList.addAll(filteredColleges);
        }
    }

    protected void loadMap(GoogleMap googleMap) {

        // Add '+' and '-' icons for zooming
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

        //Disable Map Toolbar:
        map.getUiSettings().setMapToolbarEnabled(false);

        if (map != null) {
            // Map is ready

            MapActivityPermissionsDispatcher.getMyLocationWithPermissionCheck(this);
            MapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
            map.setOnMapLongClickListener(this);
        } else {
            Toast toast = Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, Constants.TOAST_X_OFFSET, Constants.TOAST_Y_OFFSET);
            toast.show();
        }

        map.moveCamera(CameraUpdateFactory.newLatLng(center_us));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(center_us, 2));

        loadCollegeMarkers();

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                showDialogForCollege(marker);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @SuppressWarnings({"MissingPermission"})
    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        map.setMyLocationEnabled(true);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * Called when the Activity is no longer visible.
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status
        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        } else {
            Log.d( "GPS", "Current location unreachable, please enable GPS on your emulator.");
        }
        MapActivityPermissionsDispatcher.startLocationUpdatesWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }

        // Report to the UI that the location was updated
        mCurrentLocation = location;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }


    private void showDialogForCollege(final Marker marker) {
        // Marker should come with info bundle of college
        College college = (College) marker.getTag();
        Intent intent = new Intent(MapActivity.this, CollegeDetailsDialog.class);
        intent.putExtra(College.class.getSimpleName(), Parcels.wrap(college));
        startActivity(intent);
    }

    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        return false;
    }

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1000;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                }
            }
        });
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends android.support.v4.app.DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    public void loadCollegeMarkers() {
        filteredColleges = this.getIntent().getParcelableArrayListExtra("favoritedList");
//        collegeAdapter = new CollegeAdapter(filteredColleges, sInterface, lrInterface, closlInterface, cdoInterface);
        if (filteredColleges != null) {
            for (int i = 0; i < filteredColleges.size(); i++) {
                College college = filteredColleges.get(i);

                Log.d("college", college.getCollegeName());

                // Note coords must have 6 decimal places to appear in correct location
                Double lat = college.getLatitude();
                Double lng = college.getLongitude();

                String name = college.getCollegeName();
                String city = college.getAddress();

                // Create LatLng for each deadline and add to CompactCalendarView
                LatLng coords = new LatLng(lat, lng);

                Drawable circleDrawable = getResources().getDrawable(R.drawable.show_map);
                circleDrawable.mutate().setColorFilter(getResources().getColor(R.color.background_orange), PorterDuff.Mode.MULTIPLY);
                BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);

                Marker mCollege = map.addMarker(new MarkerOptions()
                        .position(coords)
                        .title(name)
                        .snippet(city)
                        .icon(markerIcon));

                CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(this);
                map.setInfoWindowAdapter(customInfoWindow);

                mCollege.setTag(college);
                dropPinEffect(mCollege);

                mLocationsList.add(coords);
            }
        }
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

}