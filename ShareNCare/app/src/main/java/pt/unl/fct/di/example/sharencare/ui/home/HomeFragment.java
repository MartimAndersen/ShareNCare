package pt.unl.fct.di.example.sharencare.ui.home;

import android.Manifest;
import android.app.admin.SystemUpdatePolicy;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import pt.unl.fct.di.example.sharencare.MainMenuActivity;
import pt.unl.fct.di.example.sharencare.MapsActivity;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.databinding.ActivityMapsBinding;
import pt.unl.fct.di.example.sharencare.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment{

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mapFragment.getMapAsync(googleMap -> getLocation(googleMap));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getLocation(@NonNull @NotNull GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 44);
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    Double lat;
                    Double lon;
                    String title;
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        title = "Current Location";
                    }
                    else{
                        lat = 38.66115666594457;
                        lon = -9.205866646456688;
                        title = "Nova School of Science & Technology";
                    }

                    LatLng loc = new LatLng(lat, lon);
                    map.addMarker(new MarkerOptions().position(loc).title(title));
                    map.moveCamera(CameraUpdateFactory.newLatLng(loc));
                });

    }
}