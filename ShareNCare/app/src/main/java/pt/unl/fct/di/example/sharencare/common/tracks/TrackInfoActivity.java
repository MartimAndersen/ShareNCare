package pt.unl.fct.di.example.sharencare.common.tracks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
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
import pt.unl.fct.di.example.sharencare.user.main_menu.MakeTrackActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackInfoActivity extends AppCompatActivity implements
        OnMapReadyCallback, RoutingListener {

    private SharedPreferences sharedpreferences;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Polyline gpsTrack;
    private Gson gson;

    private LatLng myLocation, start, end;
    private Repository eventsRepository;
    private Button play, stop;

  /*  private DirectionsRoute currentRoute;
    private NavigationMapRoute navigationMapRoute;
    private MapboxMap mapboxMap;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);
        gson = new Gson();

      //  mapboxMap.addOnMapClickListener(TrackInfoActivity.this);

        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.track_info_map);
        mapFragment.getMapAsync(this);

        eventsRepository = eventsRepository.getInstance();

       /* play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean simulateRoute = true;
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build();
// Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(TrackInfoActivity.this, options);
            }
        });*/
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
                List<LatLng> points = gson.fromJson(track.getPoints(), t2);
                getGps(points, track.getType());
            }
    }

    private void getGps(List<LatLng> points, String type) {
        if(type.equals("live")) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .color(Color.CYAN)
                    .width(7)
                    .addAll(points);
            gpsTrack = map.addPolyline(polylineOptions);
            gpsTrack.setPoints(points);
        } else {
           // startActivity(new Intent(TrackInfoActivity.this, RouteActivity.class));
          //  for(int i = 0; i < points.size()-1; i++){
              //  getRoute(points.get(0), points.get(points.size()-1));
               /* start = points.get(i);
                end = points.get(i+1);
                getEvents(points);
                findRoutes(start, end);*/
           // }
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
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
            if(i == shortestRouteIndex) {
                polyOptions.color(Color.BLUE);
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                map.addPolyline(polyOptions);
            }
        }
    }

    @Override
    public void onRoutingCancelled() {
        findRoutes(start,end);
    }

    public void findRoutes(LatLng Start, LatLng End) {
        if(Start==null || End==null)
            Toast.makeText(TrackInfoActivity.this,"Unable to get location", Toast.LENGTH_LONG).show();
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

    private void getEvents(List<LatLng> points){
        List<String> e = new ArrayList<String>();
        for(LatLng l : points){
            String coordinates = l.latitude + " " + l.longitude;
            e.add(coordinates);
        }

        GetEventsByCoordinates ge = new GetEventsByCoordinates(e);

        eventsRepository.getEventsService().getEventsByCoordinates(ge).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()){
                    List<EventData> events = EventMethods.getMultipleEvents(r);
                    for(EventData ev : events)
                        showEvents(ev);
                }
                else
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

  /*  private void getRoute(LatLng origin, LatLng destination) {
        Point destinationPoint = Point.fromLngLat(destination.latitude, destination.longitude);
        Point originPoint = Point.fromLngLat(origin.latitude, origin.longitude);

        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(originPoint)
                .destination(destinationPoint)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Toast.makeText(getApplicationContext(), "No routes found, make sure you set the right user and access token.", Toast.LENGTH_LONG);
                        } else if (response.body().routes().size() < 1) {
                            Toast.makeText(getApplicationContext(), "No routes found.", Toast.LENGTH_LONG);
                        }

                        currentRoute = response.body().routes().get(0);

                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, map, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_LONG);
                    }
                });
    }*/

}