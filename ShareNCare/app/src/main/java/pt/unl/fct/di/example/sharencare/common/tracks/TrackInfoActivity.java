package pt.unl.fct.di.example.sharencare.common.tracks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.jetbrains.annotations.NotNull;

import java.io.File;
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
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackMedia;
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

    private LatLng start, end;
    private Repository repository;
    private ImageButton play, stop, camera;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private boolean requestingLocationUpdates;
    private LatLng lastKnownLatLng;
    private LocationCallback locationCallback;

    private List<TrackMedia> media;

    List<LatLng> points;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        gson = new Gson();

        //  mapboxMap.addOnMapClickListener(TrackInfoActivity.this);

        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.track_info_map);
        mapFragment.getMapAsync(this);

        repository = repository.getInstance();

        play = findViewById(R.id.activity_track_info_play);
        stop = findViewById(R.id.activity_track_info_stop);
        camera = findViewById(R.id.activity_track_info_camera);

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

                // startActivity(mapIntent);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInfo = sharedpreferences.getString("USER", null);
                UserInfo user = gson.fromJson(userInfo, UserInfo.class);
                
                repository.getTracksService().addMediaToTrack(user.getToken(), media).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Media Saved", Toast.LENGTH_LONG);
                        else
                            Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_LONG);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG);
                    }
                });
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

        String trackTitle = getIntent().getStringExtra("title_track");
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
        map.addMarker(new MarkerOptions().position(loc).title(event.getName()));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                String markerTitle = marker.getTitle();
                Intent i = new Intent(getApplicationContext(), EventsInfoActivity.class);
                i.putExtra("name_event", markerTitle);
                i.putExtra("type", "add");
                startActivity(i);
                return false;
            }
        });
    }

    private void onPlay() {
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

    public void openCamera(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getResources().getString(R.string.app_name));

        if(!imagesFolder.exists())
            imagesFolder.mkdirs();

        File image = new File(imagesFolder, System.currentTimeMillis()+".jpg");
        Uri uriSavedImage = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName()+".provider", image);

        TrackMedia m = new TrackMedia(uriSavedImage, lastKnownLatLng);
        media.add(m);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivity(intent);

    }

}