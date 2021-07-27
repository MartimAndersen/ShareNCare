package pt.unl.fct.di.example.sharencare.user.main_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventData;
import pt.unl.fct.di.example.sharencare.common.events.EventMethods;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.map.TrackData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MakeTrackActivity extends AppCompatActivity implements RoutingListener {

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private Double lat, lon;
    private String title;
    private List<LatLng> addedEvents;
    private Button saveTrack;
    private int distanceTraveled;

    private boolean set;

    private TextView eventName, eventDate, eventInstitution;
    private ChipGroup eventTags;

    private LatLng myLocation, start, end;

    private Polyline gpsTrack;
    private List<Polyline> polylines = null;
    private List<LatLng> points = null;

    private String[] tagNames = {"Animals", "Environment", "Children", "Elderly", "Supplies", "Homeless"};
    private int[] tagIcons = {R.drawable.animals_black, R.drawable.environment_black, R.drawable.children_black, R.drawable.elderly_black, R.drawable.supplies_black, R.drawable.homeless_black};

    private SharedPreferences sharedpreferences;
    Gson gson;

    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_track);
        repository = repository.getInstance();
        gson = new Gson();
        distanceTraveled = 0;
        //  getLocation = view.findViewById(R.id.fragment_home_get_location);

        addedEvents = new ArrayList<LatLng>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.track_map);
        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
        });

        saveTrack = findViewById(R.id.activity_make_track_save);
        start = new LatLng( 38.66115666594457, -9.205866646456688);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        repository.getEventsService()
                .getUserEvents(user.getToken())
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if (r.isSuccessful()) {
                            getEvents(r);
                        } else {
                            Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT).show();
                    }
                });

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        saveTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrack(user.getToken());
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getLocation();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Location> task) {
                    Location location = task.getResult();
                    lat = 38.66115666594457;
                    lon = -9.205866646456688;
                    title = "Nova School of Science & Technology";
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        title = "Current Location";
                    } else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull @NotNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();

                                lat = location1.getLatitude();
                                lon = location1.getLongitude();
                                title = "Current Location";
                            }
                        };
                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    myLocation = new LatLng(lat, lon);
                    end = myLocation;
                    addedEvents.add(myLocation);
                    map.addMarker(new MarkerOptions().position(myLocation).title(title).snippet("USER").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    map.moveCamera(CameraUpdateFactory.zoomTo(7));
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void getEvents(Response<ResponseBody> r) {
        List<EventData> events = EventMethods.getMultipleEvents(r);
        for (EventData e : events)
            showEvents(e);
    }

    private void showEvents(EventData event) {
        set = false;

        LatLng loc = new LatLng(event.getLat(), event.getLon());
        String data = gson.toJson(event);

        map.addMarker(new MarkerOptions().position(loc).title(data).snippet("MARKER"));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                markerClickEvent(marker, marker.getSnippet());
                return false;
            }
        });
    }

    private String getDateAndTime(String initialDate, String endingDate, String time) {
        String date = null;
        SimpleDateFormat sdfStart = new SimpleDateFormat("dd/MM/yy", Locale.UK);
        SimpleDateFormat sdfEnd = new SimpleDateFormat("EEE, d MMM yyyy", Locale.UK);
        try {

            Date iDate = sdfStart.parse(initialDate);
            Date eDate = sdfStart.parse(endingDate);
            String iOutput = sdfEnd.format(iDate);
            String eOutput = sdfEnd.format(eDate);

            if (initialDate.equals(endingDate))
                date = iOutput + ", " + time;
            else
                date = iOutput + " - " + eOutput + ", " + time;
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    private void setChip(int position, Context context){
        Chip chip = new Chip(context);
        chip.setChipIcon(getResources().getDrawable(tagIcons[position], getTheme()));
        chip.setText(tagNames[position]);
        eventTags.addView(chip);
    }

    // function to find Routes.
    public void findRoutes(LatLng Start, LatLng End) {
        if(Start==null || End==null)
            Toast.makeText(MakeTrackActivity.this,"Unable to get location", Toast.LENGTH_LONG).show();
        else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getResources().getString(R.string.google_maps_key))
                    .build();
            routing.execute();
        }
    }

    //Routing call back functions.
    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//        Findroutes(start,end);
    }

    @Override
    public void onRoutingStart() {
        Toast.makeText(MakeTrackActivity.this,"Finding Route...",Toast.LENGTH_LONG).show();
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        PolylineOptions polyOptions = new PolylineOptions();

        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {
            if(i == shortestRouteIndex) {
                polyOptions.color(Color.BLUE);
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                gpsTrack = map.addPolyline(polyOptions);
            }
        }
    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(start,end);
    }

    private void saveTrack(List<String> token){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MakeTrackActivity.this);
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

                PolylineOptions polyOptions = new PolylineOptions();
                polyOptions.color(Color.BLUE);
                polyOptions.width(7);
                polyOptions.addAll(gpsTrack.getPoints());

                TrackData t = new TrackData(
                        description.getText().toString(),
                        Math.round(difficulty.getValue()),
                        gson.toJson(distanceTraveled),
                        String.valueOf(0),
                        gson.toJson(addedEvents),
                        title.getText().toString(),
                        "pre-made",
                        user.getUsername()
                );

                repository.getTracksService().registerTrack(user.getToken(), t).enqueue(new Callback<ResponseBody>() {
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

    private void markerClickEvent(Marker marker, String type){
        if(type != null){
            switch(type) {
                case "MARKER":
                    showInfo(marker);
                case "USER":
                    break;
            }
        }
    }

    private void showInfo(Marker marker){
        View infoView = findViewById(R.id.info_view);
        infoView.setVisibility(View.VISIBLE);

        Type t = new TypeToken<EventData>(){}.getType();
        EventData e = gson.fromJson(marker.getTitle(), t);

        eventName = infoView.findViewById(R.id.fragment_event_info_name);
        eventDate = infoView.findViewById(R.id.fragment_event_info_date);
        eventInstitution = infoView.findViewById(R.id.fragment_event_info_institution);
        eventTags = infoView.findViewById(R.id.fragment_event_info_tags);
        Button select = infoView.findViewById(R.id.activity_make_track_select);

        String markerTitle = marker.getTitle();

        eventName.setText(e.getName());
        eventDate.setText(getDateAndTime(e.getInitialDate(), e.getEndingDate(), e.getTime()));
        eventInstitution.setText(e.getInstitutionName());
        List<Integer> tag = EventMethods.getTags(gson.toJson(e.getTags()));
        if(!set) {
            for (int chip : tag)
                setChip(chip, infoView.getContext());
            set = true;
        }

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng coordinates = new LatLng(e.getLat(), e.getLon());
                start = end;
                end = coordinates;
                if(!addedEvents.contains(coordinates)) {
                    addedEvents.add(coordinates);
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    findRoutes(start, end);
                } else{
                    int position = -1;
                    for(int i = 0; i < addedEvents.size() && position == -1; i++){
                        if(addedEvents.get(i) == coordinates)
                            position = i;
                    }
                    addedEvents.remove(coordinates);
                    polylines.remove(position);
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
            }
        });
    }

}
