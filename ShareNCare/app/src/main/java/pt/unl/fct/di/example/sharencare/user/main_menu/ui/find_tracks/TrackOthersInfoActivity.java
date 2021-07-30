package pt.unl.fct.di.example.sharencare.user.main_menu.ui.find_tracks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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
import pt.unl.fct.di.example.sharencare.common.tracks.TrackInfo;
import pt.unl.fct.di.example.sharencare.common.tracks.TrackInfoActivity;
import pt.unl.fct.di.example.sharencare.common.tracks.TrackMethods;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.map.TrackData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackOthersInfoActivity extends AppCompatActivity implements
        OnMapReadyCallback, RoutingListener {

    private SharedPreferences sharedpreferences;
    private GoogleMap map;
    private SupportMapFragment mapFragment;
    private Polyline gpsTrack;
    private Gson gson;

    private LatLng start, end;
    private Repository repository;

    private List<LatLng> points;
    private String trackTitle;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.track_info_others, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_others_info);
        gson = new Gson();
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.track_others_info_map);
        mapFragment.getMapAsync(this);

        repository = repository.getInstance();

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);
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
            Toast.makeText(TrackOthersInfoActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
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
                i.putExtra("type", "info");
                startActivity(i);
                return false;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_comments) {
            Intent intent = new Intent(TrackOthersInfoActivity.this, CommentsActivity.class);
            intent.putExtra("track_title", trackTitle);
            startActivity(intent);
        } if (id == R.id.menu_add){
            String userInfo = sharedpreferences.getString("USER", null);
            UserInfo user = gson.fromJson(userInfo, UserInfo.class);

            repository.getTracksService().addTrackToUser(user.getToken(), trackTitle).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                    if(r.isSuccessful())
                        Toast.makeText(TrackOthersInfoActivity.this, "Track Added!", Toast.LENGTH_SHORT);
                    else
                        Toast.makeText(TrackOthersInfoActivity.this, "CODE: " + r.code(), Toast.LENGTH_SHORT);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(TrackOthersInfoActivity.this, "FAIL", Toast.LENGTH_SHORT);
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

}