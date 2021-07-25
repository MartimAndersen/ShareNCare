package pt.unl.fct.di.example.sharencare.common.tracks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventData;
import pt.unl.fct.di.example.sharencare.common.events.EventMethods;
import pt.unl.fct.di.example.sharencare.common.events.EventsInfoActivity;
import pt.unl.fct.di.example.sharencare.common.events.GetEventsByCoordinates;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.find_tracks.CommentsActivity;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.find_tracks.TrackOthersInfoActivity;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.FinishedTrack;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.GetMediaPic;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackMarkers;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.ReviewData;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackDangerZones;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackMedia;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackNotes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackInfoActivity extends AppCompatActivity implements
        OnMapReadyCallback, RoutingListener, com.google.android.gms.location.LocationListener {

    private SharedPreferences sharedpreferences;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Polyline gpsTrack;
    private Gson gson;

    private LatLng start, end, myLocation;
    private Repository repository;
    private ImageButton play, stop, camera, note, danger, marker, comments;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private boolean requestingLocationUpdates;
    private LatLng lastKnownLatLng;
    private LocationCallback locationCallback;

    private List<String> media;
    private List<String> notes;
    private List<String> zones;
    private List<String> markers;

    private Double lat, lon;
    private String title;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private List<LatLng> points;
    private String trackTitle;

    private static final int PERMISSION_CODE = 100;

    private String[] pins = {"accessible", "park", "food", "volunteering", "health", "mask", "parking", "rest", "restroom"};
    private int[] pinImages = {R.drawable.acessible_pin, R.drawable.park_pin, R.drawable.food_pin, R.drawable.volunteering_pin, R.drawable.health_pin, R.drawable.mask_pin, R.drawable.parking_pin, R.drawable.rest_pin, R.drawable.wc_pin};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        gson = new Gson();

        media = new ArrayList<String>();
        notes = new ArrayList<String>();
        zones = new ArrayList<String>();
        markers = new ArrayList<String>();

        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.track_info_map);
        mapFragment.getMapAsync(this);

        repository = repository.getInstance();

        play = findViewById(R.id.activity_track_info_play);
        stop = findViewById(R.id.activity_track_info_stop);
        camera = findViewById(R.id.activity_track_info_camera);
        note = findViewById(R.id.activity_track_info_notes);
        danger = findViewById(R.id.activity_track_info_danger);
        marker = findViewById(R.id.activity_track_info_markers);

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String waypoints = points.get(0).latitude + "," + points.get(0).longitude;
                for (int i = 1; i < points.size() - 1; i++)
                    waypoints.concat("%7" + points.get(i).latitude + "," + points.get(i).longitude);

                Uri gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination="
                        + end.latitude + "," + end.longitude +
                        "&waypoints=" + waypoints + "&travelmode=driving&dir_action=navigate");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                onPlay();
                popupGoogleMaps(mapIntent);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    openCamera(user);
                }
                else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                }
            }
        });

        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNote(user);
            }
        });

        danger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dangerZone(user);
            }
        });

        marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarker(user);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInfo = sharedpreferences.getString("USER", null);
                UserInfo user = gson.fromJson(userInfo, UserInfo.class);
                saveMarkers(user);
                finishTrack(user);
                onPause();
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(TrackInfoActivity.this);
        requestingLocationUpdates = false;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    lastKnownLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                }
            }
        };


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng fct = new LatLng(38.66115666594457, -9.205866646456688);
        map.moveCamera(CameraUpdateFactory.newLatLng(fct));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(fct, 7));

        trackTitle = getIntent().getStringExtra("title_track");
        String tracksString = getIntent().getStringExtra("tracks");

        Type t = new TypeToken<List<TrackInfo>>() {
        }.getType();
        List<TrackInfo> tracks = gson.fromJson(tracksString, t);
        for (TrackInfo track : tracks)
            if (track.getTitle().equals(trackTitle)) {
                Type t2 = new TypeToken<List<LatLng>>() {
                }.getType();
                points = gson.fromJson(track.getPoints(), t2);
                getGps(points, track.getType());
                getPins(track);
            }
    }

    private void getGps(List<LatLng> points, String type) {
        if (type.equals("live")) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(Color.CYAN)
                    .width(7)
                    .addAll(points);
            gpsTrack = map.addPolyline(polylineOptions);
            gpsTrack.setPoints(points);
            end = points.get(points.size() - 1);
        } else {
            for (int i = 0; i < points.size() - 1; i++) {
                start = points.get(i);
                end = points.get(i + 1);
                getEvents(points);
                findRoutes(start, end);
            }
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onRoutingStart() {
    }

    //If Route finding success..
    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        PolylineOptions polyOptions = new PolylineOptions();

        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {
            if (i == shortestRouteIndex) {
                polyOptions.color(Color.BLUE);
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                map.addPolyline(polyOptions);
            }
        }
    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(start, end);
    }

    public void findRoutes(LatLng Start, LatLng End) {
        if (Start == null || End == null)
            Toast.makeText(TrackInfoActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
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

    private void getEvents(List<LatLng> points) {
        List<String> e = new ArrayList<String>();
        for (LatLng l : points) {
            String coordinates = l.latitude + " " + l.longitude;
            e.add(coordinates);
        }

        GetEventsByCoordinates ge = new GetEventsByCoordinates(e);

        repository.getEventsService().getEventsByCoordinates(ge).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if (r.isSuccessful()) {
                    List<EventData> events = EventMethods.getMultipleEvents(r);
                    for (EventData ev : events)
                        showEvents(ev);
                } else
                    Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_LONG);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG);
            }
        });
    }

    private void showEvents(EventData event) {
        LatLng loc = new LatLng(event.getLat(), event.getLon());
        map.addMarker(new MarkerOptions().position(loc).title(event.getName()).snippet("EVENT"));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                markerClickEvent(marker, marker.getSnippet());
                return false;
            }
        });
    }

    private void onPlay() {
        requestingLocationUpdates = true;
        getLocation();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        play.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.VISIBLE);
        camera.setVisibility(View.VISIBLE);
        note.setVisibility(View.VISIBLE);
        danger.setVisibility(View.VISIBLE);
        marker.setVisibility(View.VISIBLE);
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(last, 15));
        Location.distanceBetween(last.latitude, last.longitude, lastKnownLatLng.latitude, lastKnownLatLng.longitude, results);
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        map.setMyLocationEnabled(true);
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    protected void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void openCamera(UserInfo user){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name));

        if(!imagesFolder.exists())
            imagesFolder.mkdirs();

        File image = new File(imagesFolder, System.currentTimeMillis()+".jpg");
        Uri uriSavedImage = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName()+".provider", image);

        byte[] inputData = null;

        try {
            InputStream iStream = null;
            iStream = getContentResolver().openInputStream(uriSavedImage);
            inputData = getBytes(iStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        LatLng loc = lastKnownLatLng;
        String location = loc.latitude + " " + loc.longitude;

        TrackMedia m = new TrackMedia(image.getName(), inputData, user.getUsername(), location, 0);
        media.add(gson.toJson(m));

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);;
        startActivity(intent);

        play.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.VISIBLE);

        map.addMarker(new MarkerOptions()
                .position(loc).title(gson.toJson(m)).snippet("MEDIA")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_pin)));
    }

    public void makeNote(UserInfo user){
        dialogBuilder = new AlertDialog.Builder(TrackInfoActivity.this);
        View popup = getLayoutInflater().inflate(R.layout.popup_note, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.show();

        LatLng loc = lastKnownLatLng;
        String location = loc.latitude + " " + loc.longitude;

        TextView text = popup.findViewById(R.id.popup_note_text);
        EditText description = popup.findViewById(R.id.popup_note_description);
        Button cancel = popup.findViewById(R.id.popup_note_cancel);
        Button save = popup.findViewById(R.id.popup_note_save);

        text.setText("What would you like your note to say?");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackNotes n = new TrackNotes(user.getUsername(), description.getText().toString(), location, 0);

                notes.add(gson.toJson(n));

                map.addMarker(new MarkerOptions()
                        .position(loc).title(gson.toJson(n)).snippet("NOTE")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.note_pin)));

                dialog.hide();
            }
        });
    }

    private void dangerZone(UserInfo user){
        dialogBuilder = new AlertDialog.Builder(TrackInfoActivity.this);
        View popup = getLayoutInflater().inflate(R.layout.popup_note, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.show();

        String location = lastKnownLatLng.latitude + " " + lastKnownLatLng.longitude;

        TextView text = popup.findViewById(R.id.popup_note_text);
        EditText description = popup.findViewById(R.id.popup_note_description);
        Button cancel = popup.findViewById(R.id.popup_note_cancel);
        Button save = popup.findViewById(R.id.popup_note_save);

        text.setText("Why is this a danger zone?");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackDangerZones d = new TrackDangerZones(user.getUsername(), description.getText().toString(), location, 0);

                zones.add(gson.toJson(d));

                LatLng location = lastKnownLatLng;
                map.addMarker(new MarkerOptions()
                        .position(location).title(gson.toJson(d)).snippet("DANGER_ZONE")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.danger_pin)));

                dialog.hide();
            }
        });
    }

    private void setMarker(UserInfo user){
        dialogBuilder = new AlertDialog.Builder(TrackInfoActivity.this);
        View popup = getLayoutInflater().inflate(R.layout.popup_markers, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();
        dialog.show();

        View accessible = popup.findViewById(R.id.c1);
        View park = popup.findViewById(R.id.popup_markers_park);
        View food = popup.findViewById(R.id.popup_markers_food);
        View volunteering = popup.findViewById(R.id.popup_markers_volunteering);
        View health = popup.findViewById(R.id.popup_markers_health);
        View mask = popup.findViewById(R.id.popup_markers_mask);
        View parking = popup.findViewById(R.id.popup_markers_parking);
        View rest = popup.findViewById(R.id.popup_markers_rest);
        View restroom = popup.findViewById(R.id.popup_markers_wc);

        LatLng loc = lastKnownLatLng;
        String location = loc.latitude + " " + loc.longitude;

        accessible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackMarkers m = new TrackMarkers(
                    0,
                        location,
                        pins[0],
                        user.getUsername()
                );

                markers.add(gson.toJson(m));

                map.addMarker(new MarkerOptions()
                        .position(loc).snippet("MARKER")
                        .icon(BitmapDescriptorFactory.fromResource(pinImages[0])));

                dialog.hide();
            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackMarkers m = new TrackMarkers(
                        0,
                        location,
                        pins[1],
                        user.getUsername()
                );

                markers.add(gson.toJson(m));

                map.addMarker(new MarkerOptions()
                        .position(loc).snippet("MARKER")
                        .icon(BitmapDescriptorFactory.fromResource(pinImages[1])));

                dialog.hide();
            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackMarkers m = new TrackMarkers(
                        0,
                        location,
                        pins[2],
                        user.getUsername()
                );

                markers.add(gson.toJson(m));

                map.addMarker(new MarkerOptions()
                        .position(loc).snippet("MARKER")
                        .icon(BitmapDescriptorFactory.fromResource(pinImages[2])));

                dialog.hide();
            }
        });

        volunteering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackMarkers m = new TrackMarkers(
                        0,
                        location,
                        pins[3],
                        user.getUsername()
                );

                markers.add(gson.toJson(m));

                map.addMarker(new MarkerOptions()
                        .position(loc).snippet("MARKER")
                        .icon(BitmapDescriptorFactory.fromResource(pinImages[3])));

                dialog.hide();
            }
        });

        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackMarkers m = new TrackMarkers(
                        0,
                        location,
                        pins[4],
                        user.getUsername()
                );

                markers.add(gson.toJson(m));

                map.addMarker(new MarkerOptions()
                        .position(loc).snippet("MARKER")
                        .icon(BitmapDescriptorFactory.fromResource(pinImages[4])));

                dialog.hide();
            }
        });

        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackMarkers m = new TrackMarkers(
                        0,
                        location,
                        pins[5],
                        user.getUsername()
                );

                markers.add(gson.toJson(m));

                map.addMarker(new MarkerOptions()
                        .position(loc).snippet("MARKER")
                        .icon(BitmapDescriptorFactory.fromResource(pinImages[5])));

                dialog.hide();
            }
        });

        parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackMarkers m = new TrackMarkers(
                        0,
                        location,
                        pins[6],
                        user.getUsername()
                );

                markers.add(gson.toJson(m));

                map.addMarker(new MarkerOptions()
                        .position(loc).snippet("MARKER")
                        .icon(BitmapDescriptorFactory.fromResource(pinImages[6])));

                dialog.hide();
            }
        });

        rest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackMarkers m = new TrackMarkers(
                        0,
                        location,
                        pins[7],
                        user.getUsername()
                );

                markers.add(gson.toJson(m));

                map.addMarker(new MarkerOptions()
                        .position(loc).snippet("MARKER")
                        .icon(BitmapDescriptorFactory.fromResource(pinImages[7])));

                dialog.hide();
            }
        });

        restroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TrackMarkers m = new TrackMarkers(
                        0,
                        location,
                        pins[8],
                        user.getUsername()
                );

                markers.add(gson.toJson(m));

                map.addMarker(new MarkerOptions()
                        .position(loc).snippet("MARKER")
                        .icon(BitmapDescriptorFactory.fromResource(pinImages[8])));

                dialog.hide();
            }
        });

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
                    findRoutes(myLocation, points.get(0));
                    map.addMarker(new MarkerOptions().position(myLocation).title(title).snippet("USER").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    map.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                    map.moveCamera(CameraUpdateFactory.zoomTo(7));
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public void popupGoogleMaps(Intent intent){
        dialogBuilder = new AlertDialog.Builder(TrackInfoActivity.this);
        View popupGoogleMaps = getLayoutInflater().inflate(R.layout.popup_google_maps, null);
        dialogBuilder.setView(popupGoogleMaps);
        dialog = dialogBuilder.create();
        dialog.show();

        Button cancel, open;

            cancel = popupGoogleMaps.findViewById(R.id.popup_google_maps_cancel);
            open = popupGoogleMaps.findViewById(R.id.popup_google_maps_open);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.hide();
                }
            });

            open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intent);
                    dialog.hide();
                }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        if(requestCode == PERMISSION_CODE && (grantResults.length > 0) && (grantResults[0] + grantResults[1]  == PackageManager.PERMISSION_GRANTED))
            openCamera(user);
        else if(requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            startLocationUpdates();
        } else {
            Toast.makeText(TrackInfoActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMarkers(UserInfo user){
        FinishedTrack f = new FinishedTrack(
                markers,
                media,
                notes,
                trackTitle,
                zones
        );

        repository.getTracksService().finishedTrack(user.getToken(), f).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful())
                    Toast.makeText(getApplicationContext(), "Updated Track!", Toast.LENGTH_SHORT);
                else
                    Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT);
            }
        });
    }

    private void finishTrack(UserInfo user){
        dialogBuilder = new AlertDialog.Builder(TrackInfoActivity.this);
        View popupReview = getLayoutInflater().inflate(R.layout.popup_review, null);
        dialogBuilder.setView(popupReview);
        dialog = dialogBuilder.create();
        dialog.show();

        EditText comment = popupReview.findViewById(R.id.popup_review_comment);
        RatingBar rating = popupReview.findViewById(R.id.popup_review_rating_bar);
        Button save = popupReview.findViewById(R.id.popup_review_save);
        Button cancel = popupReview.findViewById(R.id.popup_review_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewData review = new ReviewData(
                        comment.getText().toString(),
                        gson.toJson(rating.getRating()),
                        trackTitle,
                        user.getUsername()
                );

                repository.getTracksService().comment(user.getToken(), review).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Review Accepted", Toast.LENGTH_LONG);
                            dialog.hide();
                        }else
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_comments_my) {
            Intent intent = new Intent(TrackInfoActivity.this, CommentsActivity.class);
            intent.putExtra("track_title", trackTitle);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPins(TrackInfo track){
        List<TrackMarkers> m1 = track.getMarkers();
        List<TrackMedia> m2 = track.getMedia();
        List<TrackDangerZones> m3 = track.getZones();
        List<TrackNotes> m4 = track.getNotes();

        for(TrackMarkers t : m1){
            String[] latlon = t.getLocation().split(" ");
            LatLng loc = new LatLng(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1]));

            String type = t.getType();

            int position = -1;

            for(int i = 0; i < pins.length; i++)
                if(pins[i].equals(type))
                    position = i;

            map.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(gson.toJson(t))
                    .snippet("MARKER")
                    .icon(BitmapDescriptorFactory.fromResource(pinImages[position])));

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                    markerClickEvent(marker, marker.getSnippet());
                    return false;
                }
            });
        }

        for(TrackMedia t : m2){
            String[] latlon = t.getLocation().split(" ");
            LatLng loc = new LatLng(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1]));

            map.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(gson.toJson(t))
                    .snippet("MEDIA")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.image_pin)));

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                    markerClickEvent(marker, marker.getSnippet());
                    return true;
                }
            });
        }

        for(TrackDangerZones t : m3){
            String[] latlon = t.getLocation().split(" ");
            LatLng loc = new LatLng(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1]));

            map.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(gson.toJson(t))
                    .snippet("DANGER_ZONE")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.danger_pin)));

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                    markerClickEvent(marker, marker.getSnippet());
                    return true;
                }
            });
        }

        for(TrackNotes t : m4){
            String[] latlon = t.getLocation().split(" ");
            LatLng loc = new LatLng(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1]));

            map.addMarker(new MarkerOptions()
                    .position(loc)
                    .title(gson.toJson(t))
                    .snippet("NOTE")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.note_pin)));

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                    markerClickEvent(marker, marker.getSnippet());
                    return true;
                }
            });
        }
    }

    private void markerClickEvent(Marker marker, String type){
        if(type != null){
        switch(type) {
            case "EVENT":
                clickEvent(marker);
                break;
            case "MEDIA":
                clickMedia(marker);
                break;
            case "DANGER_ZONE":
                clickDangerZone(marker);
                break;
            case "NOTE":
                clickNote(marker);
                break;
            case "MARKER":
            case "USER":
                break;
        }
        }
    }

    private void clickEvent(Marker marker){
        String markerTitle = marker.getTitle();
        Intent i = new Intent(getApplicationContext(), EventsInfoActivity.class);
        i.putExtra("name_event", markerTitle);
        i.putExtra("type", "info");
        startActivity(i);
    }

    private void clickMedia(Marker marker){
        Type type = new TypeToken<TrackMedia>(){}.getType();
        TrackMedia media = gson.fromJson(marker.getTitle(), type);

        dialogBuilder = new AlertDialog.Builder(TrackInfoActivity.this);
        View popup = getLayoutInflater().inflate(R.layout.track_media_show, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();

        ImageView pic = popup.findViewById(R.id.track_media_show_pic);
        TextView username = popup.findViewById(R.id.track_media_show_username);

        username.setText(media.getUsername());

        GetMediaPic mp = new GetMediaPic(
               media.getImageName(),
               trackTitle

        );

        String json = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(json, UserInfo.class);

        repository.getTracksService().getPic(user.getToken(), mp).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()){
                    try {
                        byte[] byteArray = gson.fromJson(r.body().string(), byte[].class);
                        if(byteArray != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            pic.setImageBitmap(bitmap);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT);
            }
        });

        dialog.show();
    }

    private void clickDangerZone(Marker marker){
        Type type = new TypeToken<TrackDangerZones>(){}.getType();
        TrackDangerZones zones = gson.fromJson(marker.getTitle(), type);

        dialogBuilder = new AlertDialog.Builder(TrackInfoActivity.this);
        View popup = getLayoutInflater().inflate(R.layout.track_zones_notes, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();

        ImageView danger = popup.findViewById(R.id.track_zone_notes_type);
        TextView description = popup.findViewById(R.id.track_zones_notes_description);
        TextView username = popup.findViewById(R.id.track_zone_notes_username);

        danger.setBackground(getDrawable(R.drawable.danger));
        danger.setImageResource(R.drawable.danger_black);
        description.setText(zones.getNote());
        username.setText(zones.getUsername());

        dialog.show();
    }


    private void clickNote(Marker marker){
        Type type = new TypeToken<TrackNotes>(){}.getType();
        TrackNotes notes = gson.fromJson(marker.getTitle(), type);

        dialogBuilder = new AlertDialog.Builder(TrackInfoActivity.this);
        View popup = getLayoutInflater().inflate(R.layout.track_zones_notes, null);
        dialogBuilder.setView(popup);
        dialog = dialogBuilder.create();

        ImageView danger = popup.findViewById(R.id.track_zone_notes_type);
        TextView description = popup.findViewById(R.id.track_zones_notes_description);
        TextView username = popup.findViewById(R.id.track_zone_notes_username);

        danger.setBackground(getDrawable(R.drawable.note));
        danger.setImageResource(R.drawable.note_black);
        description.setText(notes.getNote());
        username.setText(notes.getUsername());

        dialog.show();
    }

    public byte[] getBytes(InputStream inputStream){
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        try{
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteBuffer.toByteArray();
    }

}