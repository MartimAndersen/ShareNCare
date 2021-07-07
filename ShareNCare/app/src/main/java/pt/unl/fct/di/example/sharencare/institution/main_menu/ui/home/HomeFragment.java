package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.events.EventsInfoActivity;
import pt.unl.fct.di.example.sharencare.common.register.Repository;
import pt.unl.fct.di.example.sharencare.databinding.FragmentHomeBinding;
import pt.unl.fct.di.example.sharencare.institution.login.InstitutionInfo;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.EventData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private Repository eventsRepository;
    private Gson gson;
    private GoogleMap map;

    private SharedPreferences sharedpreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_institution, container, false);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        eventsRepository = eventsRepository.getInstance();
        sharedpreferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        gson = new Gson();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map_institution);

        mapFragment.getMapAsync(googleMap -> {
            map = googleMap;
        });

        String institutionInfo = sharedpreferences.getString("USER", null);
        InstitutionInfo ins = gson.fromJson(institutionInfo, InstitutionInfo.class);

        eventsRepository.getEventsService().getUserEvents(ins.getTokenId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()) {
                    try {
                        List<EventData> events = new ArrayList<>();
                        JSONArray array = new JSONArray(r.body().string());
                        for (int i = 0; i < array.length(); i++) {
                            List<LinkedTreeMap> list = gson.fromJson(array.get(i).toString(), List.class);
                            List<String> event = new ArrayList<>();

                            for (int j = 0; j < list.size(); j++) {
                                event.add(list.get(j).get("value").toString());
                            }

                            EventData e = new EventData(
                                    event.get(7),
                                    event.get(1),
                                    event.get(6),
                                    event.get(5),
                                    event.get(3),
                                    event.get(9),
                                    event.get(4),
                                    event.get(2),
                                    getTags(event.get(8)),
                                    getLatLon(event.get(0)).first,
                                    getLatLon(event.get(0)).second
                            );

                            showEvents(e);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                        else{
                            Toast.makeText(getActivity(), "CODE: "+r.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getActivity(), "NO", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showEvents(EventData event){
        LatLng loc = new LatLng(event.getLat(), event.getLon());
        map.addMarker(new MarkerOptions().position(loc).title(event.getName()));
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                String markerTitle = marker.getTitle();
                Intent i = new Intent(getContext(), EventsInfoActivity.class);
                i.putExtra("name_event", markerTitle);
                i.putExtra("type", "info");
                startActivity(i);
                return false;
            }
        });
    }

    private Pair<Double, Double> getLatLon(String coordinates){
        String[] c = coordinates.split(" ");
        Pair<Double, Double> latLon = new Pair<Double, Double>(new Double(c[0]), new Double(c[1]));
        return latLon;
    }

    private List<Integer> getTags(String tags){
        return gson.fromJson(tags, List.class);
    }


}