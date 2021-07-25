package pt.unl.fct.di.example.sharencare.user.main_menu.ui.rank;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventsInfoActivity;
import pt.unl.fct.di.example.sharencare.common.rank.PointsData;
import pt.unl.fct.di.example.sharencare.common.rank.RankMethods;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.find_tracks.CommentsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RankingFragment extends Fragment {

    private SharedPreferences sharedpreferences;
    private Repository repository;
    private Gson gson = new Gson();

    private ListView listView;
    private TextView text;
    private String[] rankings, usernames, points;
    private byte[] pics;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedpreferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        repository = repository.getInstance();
        View header = getLayoutInflater().inflate(R.layout.header_ranking, null);

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        listView = getView().findViewById(R.id.fragment_ranking_list_view);
        text = getView().findViewById(R.id.fragment_ranking_text);
        listView.setEmptyView(text);
        listView.setEnabled(false);
        getTopUsers(user, header);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView nameText = view.findViewById(R.id.row_name_label);
                String name = nameText.getText().toString();

                Intent i = new Intent(getActivity(), EventsInfoActivity.class);
                i.putExtra("name_event", name);
                i.putExtra("type", "info");
                startActivity(i);
            }
        });
    }

    private void getTopUsers(UserInfo user, View header){
        repository.getRankService().getTop10(user.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()){
                    List<PointsData> top10 = RankMethods.getTopUsers(r);

                    setHeader(top10, header);

                    if(top10.size() > 3) {
                        int[] rankings = new int[top10.size()-3];
                        List<byte[]> pics = new ArrayList<>();
                        String[] usernames = new String[top10.size()-3];
                        int[] points = new int[top10.size()-3];

                        for (int i = 0; i < top10.size()-3; i++) {
                            rankings[i] = i+3;
                            pics.add(top10.get(i+3).getPic());
                            usernames[i] = top10.get(i+3).getUsername();
                            points[i] = top10.get(i+3).getTotal();
                        }

                        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                        String json = gson.toJson(top10);
                        prefsEditor.putString("TOP", json);
                        prefsEditor.apply();

                        RankingFragment.MyAdapter myAdapter = new RankingFragment.MyAdapter(getContext(), rankings, pics, usernames, points);
                        listView.addHeaderView(header, null, false);
                        listView.setAdapter(myAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void setHeader(List<PointsData> top10, View header){
        TextView rank1, rank2, rank3;
        CircleImageView image1, image2, image3;
        TextView username1, username2, username3;
        TextView points1, points2, points3;

        rank1 = header.findViewById(R.id.header_ranking_first_rank);
        rank2 = header.findViewById(R.id.header_ranking_second_rank);
        rank3 = header.findViewById(R.id.header_ranking_third_rank);

        image1 = header.findViewById(R.id.header_ranking_first);
        image2 = header.findViewById(R.id.header_ranking_second);
        image3 = header.findViewById(R.id.header_ranking_third);

        username1 = header.findViewById(R.id.header_ranking_first_username);
        username2 = header.findViewById(R.id.header_ranking_second_username);
        username3 = header.findViewById(R.id.header_ranking_third_username);

        points1 = header.findViewById(R.id.header_ranking_first_points);
        points2 = header.findViewById(R.id.header_ranking_second_points);
        points3 = header.findViewById(R.id.header_ranking_third_points);

       if(top10.size() >= 1)
           setView(0, top10, rank1, image1, username1, points1);
       if(top10.size() >= 2)
           setView(1, top10, rank2, image2, username2, points2);
       if(top10.size() >= 3)
           setView(2, top10, rank3, image3, username3, points3);


    }

    private void setView(int pos, List<PointsData> top10, TextView r, CircleImageView i, TextView u, TextView p){
        r.setText(String.valueOf(pos+1));
        if(top10.get(pos+1).getPic() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(top10.get(pos+1).getPic(), 0, top10.get(pos+1).getPic().length);
            i.setImageBitmap(bitmap);
        }
        u.setText(top10.get(pos).getUsername());
        p.setText(String.valueOf(top10.get(pos).getTotal()));
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        int[] rankings;
        List<byte[]> pics;
        String[] usernames;
        int[] points;

        MyAdapter(Context context, int[] rankings, List<byte[]> pics, String[] usernames, int[] points){
            super(context, R.layout.row_ranking, R.id.row_ranking_username, usernames);
            this.context = context;
            this.rankings = rankings;
            this.pics = pics;
            this.usernames = usernames;
            this.points = points;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.row_ranking, parent, false);

            TextView ranking = row.findViewById(R.id.row_ranking_position);
            CircleImageView pic = row.findViewById(R.id.row_ranking_pic);
            TextView username = row.findViewById(R.id.row_ranking_username);
            TextView point = row.findViewById(R.id.row_ranking_points);

            if(pics.get(position) != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(pics.get(position), 0, pics.get(position).length);
                pic.setImageBitmap(bitmap);
            }

            ranking.setText(String.valueOf(rankings[position]+1));
            username.setText(usernames[position]);
            point.setText(String.valueOf(points[position]));

            return row;
        }
    }
}