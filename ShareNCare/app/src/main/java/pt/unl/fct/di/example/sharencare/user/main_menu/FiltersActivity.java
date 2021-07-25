package pt.unl.fct.di.example.sharencare.user.main_menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventData;
import pt.unl.fct.di.example.sharencare.common.events.EventsInfoActivity;
import pt.unl.fct.di.example.sharencare.databinding.FragmentHomeBinding;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;

public class FiltersActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FragmentHomeBinding binding;
    private GoogleMap map;

    private SharedPreferences sharedpreferences;
    Gson gson;

    private Repository eventsRepository;

    private static final String ARG_KEY = "filters";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        gson = new Gson();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.filter_map);
        mapFragment.getMapAsync(this);

        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        String s = getIntent().getStringExtra("filters");
        Type t = new TypeToken<List<EventData>>(){}.getType();
        List<EventData> events = gson.fromJson(s, t);

            for(EventData e : events)
                showEvents(e);

    }

    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
        map = googleMap;


        LatLng fct = new LatLng(38.66115666594457, -9.205866646456688);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(fct, 9));
    }

    private void showEvents(EventData event) {
        LatLng loc = new LatLng(event.getLat(), event.getLon());
        map.addMarker(new MarkerOptions().position(loc).title(event.getName()));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                String markerTitle = marker.getTitle();
                Intent i = new Intent(FiltersActivity.this, EventsInfoActivity.class);
                i.putExtra("name_event", markerTitle);
                i.putExtra("type", "add");
                startActivity(i);
                return false;
            }
        });
    }
}