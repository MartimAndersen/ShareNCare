package pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.slider.Slider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventsInfoActivity;
import pt.unl.fct.di.example.sharencare.common.tracks.TrackInfo;
import pt.unl.fct.di.example.sharencare.common.tracks.TrackInfoActivity;
import pt.unl.fct.di.example.sharencare.common.tracks.TrackMethods;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TracksFragment extends Fragment {

    private Repository tracksRepository;
    private SharedPreferences sharedpreferences;
    private Gson gson = new Gson();

    private ListView listView;
    private TextView text;

    public TracksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tracksRepository = tracksRepository.getInstance();
        sharedpreferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        listView = view.findViewById(R.id.fragment_tracks_list_view);
        text = view.findViewById(R.id.fragment_tracks_text);

        listView.setEmptyView(text);

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        String tracksInfo = sharedpreferences.getString("TRACKS", null);

        tracksRepository.getTracksService().getUserTrack(user.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()){
                    List<TrackInfo> tracks = TrackMethods.getMultipleTracks(r);

                    String[] titles = new String[tracks.size()];
                    String[] descriptions = new String[tracks.size()];
                    float[] difficulties = new float[tracks.size()];
                    List<List<LatLng>> solidarityPoints = new ArrayList<List<LatLng>>(tracks.size());

                    Type t = new TypeToken<List<LatLng>>(){}.getType();

                    for (int i = 0; i < tracks.size(); i++) {
                        titles[i] = tracks.get(i).getTitle();
                        descriptions[i] = tracks.get(i).getDescription();
                        difficulties[i] = (float) tracks.get(i).getDifficulty();
                        List<LatLng> points = gson.fromJson(tracks.get(i).getPoints(), t);
                        solidarityPoints.add(points);
                    }

                    SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                    String json = gson.toJson(tracks);
                    prefsEditor.putString("TRACKS", json);
                    prefsEditor.apply();

                    TracksFragment.MyAdapter myAdapter = new TracksFragment.MyAdapter(getContext(), titles, descriptions, difficulties, solidarityPoints);
                    listView.setAdapter(myAdapter);
                }
                else
                    Toast.makeText(getContext(), "CODE: " + r.code(), Toast.LENGTH_LONG);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "FAIL", Toast.LENGTH_LONG);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tracks = sharedpreferences.getString("TRACKS", null);

                TextView title = view.findViewById(R.id.row_track_title);
                Intent i = new Intent(getActivity(), TrackInfoActivity.class);
                i.putExtra("title_track", title.getText().toString());
                i.putExtra("tracks", tracks);
                startActivity(i);
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] titles;
        String[] descriptions;
        float[] difficulties;
        List<List<LatLng>> solidarityPoints;
        private GoogleMap map;

        MyAdapter(Context context,  String[] titles, String[] descriptions, float[] difficulties, List<List<LatLng>> solidarityPoints){
            super(context, R.layout.row_track, R.id.row_track_title, titles);
            this.context = context;
            this.titles = titles;
            this.descriptions = descriptions;
            this.difficulties = difficulties;
            this.solidarityPoints = solidarityPoints;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.row_track, parent, false);

            TextView title = row.findViewById(R.id.row_track_title);
            TextView description = row.findViewById(R.id.row_track_description);
            RatingBar difficulty = row.findViewById(R.id.row_track_difficulty);

            title.setText(titles[position]);
            description.setText(descriptions[position]);
            difficulty.setRating(difficulties[position]);

            return row;
        }
    }
}