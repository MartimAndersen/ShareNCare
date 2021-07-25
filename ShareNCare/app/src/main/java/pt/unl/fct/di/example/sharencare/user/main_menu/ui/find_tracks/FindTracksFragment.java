package pt.unl.fct.di.example.sharencare.user.main_menu.ui.find_tracks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.tracks.TrackInfo;
import pt.unl.fct.di.example.sharencare.common.tracks.TrackMethods;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindTracksFragment extends Fragment {

    private ListView listView;
    private SharedPreferences sharedpreferences;
    private Repository eventsRepository;
    private Gson gson = new Gson();

    private TextView text;

    private String[] titles;
    private String[] usernames;
    private String[] descriptions;
    private float[] difficulties;
    private float[] ratings;

    public FindTracksFragment() {
        // Required empty public constructor
    }

    public static FindTracksFragment newInstance(String param1, String param2) {
        FindTracksFragment fragment = new FindTracksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find_tracks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences("Preferences",Context.MODE_PRIVATE);
        eventsRepository = eventsRepository.getInstance();

        listView = getView().findViewById(R.id.fragment_find_tracks_list_view);
        text = getView().findViewById(R.id.fragment_find_tracks_text);
        listView.setEmptyView(text);
        getTracks();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tracks = sharedpreferences.getString("TRACKS", null);
                TextView titleTrack = view.findViewById(R.id.row_track_others_title);
                String title = titleTrack.getText().toString();

                Intent i = new Intent(getActivity(), TrackOthersInfoActivity.class);
                i.putExtra("title_track", title);
                i.putExtra("tracks", tracks);
                startActivity(i);
            }
        });
    }

    public void getTracks(){
        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        eventsRepository.getTracksService().getAllTracks(user.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if (r.isSuccessful()) {
                    List<TrackInfo> tracks = new ArrayList<TrackInfo>();
                    tracks = TrackMethods.getMultipleTracks(r);

                    SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                    String json = gson.toJson(tracks);
                    prefsEditor.putString("TRACKS", json);
                    prefsEditor.apply();

                    listTracks(tracks);

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

    private void listTracks(List<TrackInfo> tracks){
        titles = new String[tracks.size()];
        usernames = new String[tracks.size()];
        descriptions = new String[tracks.size()];
        difficulties = new float[tracks.size()];
        ratings = new float[tracks.size()];

        for (int i = 0; i < tracks.size(); i++) {
            titles[i] = tracks.get(i).getTitle();
            usernames[i] = tracks.get(i).getUsername();
            descriptions[i] = tracks.get(i).getDescription();
            difficulties[i] = tracks.get(i).getDifficulty();
            ratings[i] = tracks.get(i).getRating();
        }

        FindTracksFragment.MyAdapter myAdapter = new FindTracksFragment.MyAdapter(getContext(), titles, usernames, descriptions, difficulties, ratings);
        listView.setAdapter(myAdapter);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] titles;
        String[] usernames;
        String[] descriptions;
        float[] difficulties;
        float[] ratings;

        MyAdapter(Context context, String[] titles, String[] usernames, String[] descriptions, float[] difficulties, float[] ratings){
            super(context, R.layout.row_track_others, R.id.row_track_others_title, titles);
            this.context = context;
            this.titles = titles;
            this.usernames = usernames;
            this.descriptions = descriptions;
            this.difficulties = difficulties;
            this.ratings = ratings;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.row_track_others, parent, false);

            TextView title = row.findViewById(R.id.row_track_others_title);
            TextView username = row.findViewById(R.id.row_track_others_user);
            TextView description = row.findViewById(R.id.row_track_others_description);
            RatingBar difficulty = row.findViewById(R.id.row_track_others_difficulty);
            RatingBar rating = row.findViewById(R.id.row_track_others_rating);

            title.setText(titles[position]);
            username.setText(usernames[position]);
            description.setText(descriptions[position]);
            difficulty.setRating(difficulties[position]);
            rating.setRating(ratings[position]);

            return row;
        }
    }

}