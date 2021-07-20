package pt.unl.fct.di.example.sharencare.user.main_menu.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.events.EventMethods;
import pt.unl.fct.di.example.sharencare.common.events.EventsInfoActivity;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.databinding.FragmentHomeBinding;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.common.events.EventData;
import pt.unl.fct.di.example.sharencare.user.main_menu.MainMenuUserActivity;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.FilterData;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TracksFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private Button getLocation;
    private ImageButton search;
    private Double lat, lon;
    private String title;

    private SharedPreferences sharedpreferences;
    Gson gson;

    EditText location, date, institution, name;
    ChipGroup tags;
    Spinner popularity;
    Button apply;
    Calendar myCalendar = Calendar.getInstance();

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private Repository eventsRepository;

    private static final String ARG_KEY = "filters";

    public static HomeFragment newInstance(String argValue) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_KEY, argValue);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        eventsRepository = eventsRepository.getInstance();
        gson = new Gson();
        getLocation = view.findViewById(R.id.fragment_home_get_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        sharedpreferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        if (getArguments() != null) {
            String filters = getArguments().getString("filters");
            FilterData f = gson.fromJson(filters, FilterData.class);
            eventsRepository.getEventsService().filterEvents(gson.toJson(f)).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                    if (r.isSuccessful()) {
                        getEvents(r);
                    } else
                        Toast.makeText(getActivity(), "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getActivity(), "NO", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            eventsRepository.getEventsService()
                    .getEventPreferences(user.getToken())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                            if (r.isSuccessful()) {
                                getEvents(r);
                            } else {
                                Toast.makeText(getActivity(), "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getActivity(), "NO", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getLocation();
        } else {
            Toast.makeText(getActivity(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("MissingPermission")
    public void getLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
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

                    LatLng loc = new LatLng(lat, lon);
                    map.addMarker(new MarkerOptions().position(loc).title(title));
                    map.moveCamera(CameraUpdateFactory.newLatLng(loc));
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void getEvents(Response<ResponseBody> r) {
        List<EventData> events = EventMethods.getMultipleEvents(r);
        map.clear();
        for(EventData e : events)
            showEvents(e);
    }

    private void showEvents(EventData event) {
        LatLng loc = new LatLng(event.getLat(), event.getLon());
        map.addMarker(new MarkerOptions().position(loc).title(event.getName()));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                String markerTitle = marker.getTitle();
                Intent i = new Intent(getContext(), EventsInfoActivity.class);
                i.putExtra("name_event", markerTitle);
                i.putExtra("type", "add");
                startActivity(i);
                return false;
            }
        });
    }
}
