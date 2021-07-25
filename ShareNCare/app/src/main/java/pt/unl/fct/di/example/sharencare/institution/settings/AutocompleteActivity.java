package pt.unl.fct.di.example.sharencare.institution.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import pt.unl.fct.di.example.sharencare.R;

import android.content.Intent;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class AutocompleteActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private EditText location;
    private Double lat, lon;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Button move, lock;
    private Marker loc;
    private Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autocomplete);

        Places.initialize(AutocompleteActivity.this,  getResources().getString(R.string.google_maps_key));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);

        location = findViewById(R.id.activity_autocomplete_location);

        lock = findViewById(R.id.activity_autocomplete_lock);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(AutocompleteActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("lat_lon", lat + " " + lon);
                resultIntent.putExtra("address", location.getText().toString());
                setResult(101, resultIntent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            location.setText(place.getAddress());
            lat = place.getLatLng().latitude;
            lon = place.getLatLng().longitude;

            loc = map.addMarker(new MarkerOptions()
                    .position(place.getLatLng())
                    .draggable(true));

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 9));

        } else {
            Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        LatLng fct = new LatLng(38.66115666594457, -9.205866646456688);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(fct, 9));

        map.setOnMapLongClickListener(this);
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onMapLongClick(@NonNull @NotNull LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(@NonNull @NotNull Marker marker) {

    }

    @Override
    public void onMarkerDrag(@NonNull @NotNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull @NotNull Marker marker) {
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if(addresses.size() > 0){
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
                location.setText(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}