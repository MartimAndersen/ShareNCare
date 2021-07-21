package pt.unl.fct.di.example.sharencare.user.main_menu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wefika.horizontalpicker.HorizontalPicker;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.map.TrackData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LiveTrackActivity extends FragmentActivity implements
        OnMapReadyCallback,
        com.google.android.gms.location.LocationListener  {

    private static final String REQUESTING_LOCATION_UPDATES_KEY = "location_key";

        private GoogleMap map;
        private Polyline gpsTrack;
        private List<Polyline> polylines;
        private SupportMapFragment mapFragment;
        private LatLng lastKnownLatLng;
        private LocationCallback locationCallback;
        private FusedLocationProviderClient fusedLocationClient;
        private LocationRequest locationRequest;
        private Gson gson;
        private List<LatLng> points;
        private boolean requestingLocationUpdates;
        private Repository tracksService;
        private SharedPreferences sharedpreferences;
        private int distanceTraveled;

        private ImageButton play, stop;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_live_track);
            distanceTraveled = 0;

            polylines = new ArrayList<Polyline>();

            sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            tracksService = tracksService.getInstance();

            play = findViewById(R.id.activity_live_track_play);
            stop = findViewById(R.id.activity_live_track_stop);

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPlay();
                }
            });

            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPause();
                    saveTrack();
                }
            });

            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.live_map);
            mapFragment.getMapAsync(this);

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(LiveTrackActivity.this);
            requestingLocationUpdates = false;

            locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            points = new ArrayList<>();
            gson = new Gson();

            updateValuesFromBundle(savedInstanceState);

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        // Update UI with location data
                        if(requestingLocationUpdates) {
                            updateTrack();
                        }
                        // ...
                    }
                }
            };

        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            LatLng fct = new LatLng(38.66115666594457, 9.205866646456688);
            map.moveCamera(CameraUpdateFactory.newLatLng(fct));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(fct, 9));

            getGps();
            gpsTrack.setPoints(points);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
        }

        private void onPlay(){
            requestingLocationUpdates = true;
            play.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.VISIBLE);
            startLocationUpdates();
        }

        @Override
        protected void onPause() {
            super.onPause();
            requestingLocationUpdates = false;
            play.setVisibility(View.VISIBLE);
            stop.setVisibility(View.INVISIBLE);
            stopLocationUpdates();
        }

        @Override
        protected void onResume() {
            super.onResume();
            if (requestingLocationUpdates) {
                startLocationUpdates();
            }
        }

        @Override
        public void onLocationChanged(Location location) {
            LatLng last = lastKnownLatLng;
            float[] results = new float[3];
            lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Location.distanceBetween(last.latitude, last.longitude, lastKnownLatLng.latitude, lastKnownLatLng.longitude, results);
            distanceTraveled += results[0];
            updateTrack();
        }

        protected void startLocationUpdates() {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }

        protected void stopLocationUpdates() {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        private void updateTrack() {
            points = gpsTrack.getPoints();
            points.add(lastKnownLatLng);
            gpsTrack.setPoints(points);
        }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                requestingLocationUpdates);
        outState.putString("points", gson.toJson(points));
        // ...
        super.onSaveInstanceState(outState);
    }

    private void getGps(){
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.CYAN);
        polylineOptions.width(10);
        gpsTrack = map.addPolyline(polylineOptions);
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }
        String p = savedInstanceState.getString("points");
        Type t = new TypeToken<ArrayList<LatLng>>(){}.getType();
        List<LatLng> pnts = gson.fromJson(p, t);
        points = pnts;
        if(!pnts.isEmpty())
            onPlay();
    }

    private void saveTrack(){
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LiveTrackActivity.this);
            View view = getLayoutInflater().inflate(R.layout.popup_save_live_track, null);
            dialogBuilder.setView(view);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            EditText title = view.findViewById(R.id.popup_save_live_track_title);
            EditText description = view.findViewById(R.id.popup_save_live_track_description);
            Slider difficulty = view.findViewById(R.id.popup_save_live_track_difficulty);
            Button cancel = view.findViewById(R.id.popup_save_live_track_cancel);
            Button save = view.findViewById(R.id.popup_save_live_track_save);

            String userInfo = sharedpreferences.getString("USER", null);
            UserInfo user = gson.fromJson(userInfo, UserInfo.class);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrackData t = new TrackData(
                            description.getText().toString(),
                            Math.round(difficulty.getValue()),
                            gson.toJson(distanceTraveled),
                            gson.toJson(points),
                            title.getText().toString(),
                            "live"
                    );

                    tracksService.getTracksService().registerTrack(user.getToken(), t).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                            if(r.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Track Registered!", Toast.LENGTH_LONG);
                                dialog.hide();

                            } else
                                Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_LONG);
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG);
                        }
                    });
                }
            });
    }


}