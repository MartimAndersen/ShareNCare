package pt.unl.fct.di.example.sharencare.user.main_menu.ui.find_tracks;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.tracks.TrackMethods;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.ReviewData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {

    private ListView listView;
    private TextView text;
    private int liked;
    private boolean isLike;

    private Repository tracksRepository;
    private SharedPreferences sharedpreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        tracksRepository = tracksRepository.getInstance();
        gson = new Gson();

        listView = findViewById(R.id.activity_comments_list_view);
        text = findViewById(R.id.activity_comments_text);
        listView.setEmptyView(text);

        liked = 0;
        isLike = false;

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        String title = getIntent().getStringExtra("track_title");
        getComments(user, title);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ImageButton like = view.findViewById(R.id.row_comment_like);
                ImageButton dislike= view.findViewById(R.id.row_comment_dislike);

                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(liked <= 0) {
                            like.setImageResource(R.drawable.like);
                            if(liked == -1)
                                dislike.setImageResource(R.drawable.thumbs_down_small);
                            liked = 1;
                        } else if(liked == 1){
                            like.setImageResource(R.drawable.thumbs_up_small);
                            liked = 0;
                            isLike = true;
                        }

                        LikeDislikeData d = new LikeDislikeData(
                                isLike,
                                liked,
                                user.getUsername(),
                                title
                        );

                        tracksRepository.getRankService().likeDislike(user.getToken(), d).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                                if(r.isSuccessful())
                                    Toast.makeText(getApplicationContext(), "You made a like", Toast.LENGTH_SHORT);
                                else
                                    Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT);
                            }
                        });
                    }
                });

                dislike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(liked >= 0) {
                            dislike.setImageResource(R.drawable.dislike);
                            if(liked == 1)
                                like.setImageResource(R.drawable.thumbs_up_small);
                            liked = -1;
                        } else if(liked == -1){
                            dislike.setImageResource(R.drawable.thumbs_down_small);
                            liked = 0;
                            isLike = false;
                        }

                        LikeDislikeData d = new LikeDislikeData(
                             isLike,
                             liked,
                             user.getUsername(),
                             title
                        );

                        tracksRepository.getRankService().likeDislike(user.getToken(), d).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                                if(r.isSuccessful())
                                    Toast.makeText(getApplicationContext(), "You made a dislike", Toast.LENGTH_SHORT);
                                else
                                    Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT);
                            }
                        });

                    }
                });
            }
        });

    }

    private void getComments(UserInfo user, String title){
        tracksRepository.getTracksService().getAllComments(user.getToken(), title).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()){
                    List<ReviewData> reviews = TrackMethods.getMultipleComments(r);

                    String[] usernames = new String[reviews.size()];
                    float[] ratings = new float[reviews.size()];
                    String[] numbers = new String[reviews.size()];
                    String[] comments = new String[reviews.size()];


                    for (int i = 0; i < reviews.size(); i++) {
                        usernames[i] = reviews.get(i).getUsername();
                        ratings[i] = Float.parseFloat(reviews.get(i).getRating());
                        numbers[i] = reviews.get(i).getRating();
                        comments[i] = reviews.get(i).getComment();
                    }

                    SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                    String json = gson.toJson(reviews);
                    prefsEditor.putString("REVIEWS", json);
                    prefsEditor.apply();

                    CommentsActivity.MyAdapter myAdapter = new CommentsActivity.MyAdapter(CommentsActivity.this, usernames, ratings, numbers, comments);
                    listView.setAdapter(myAdapter);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
       // List<byte[]> profilePics;
        String[] usernames;
        float[] ratings;
        String[] numbers;
        String[] comments;

        MyAdapter(Context context, String[] usernames, float[] ratings, String[] numbers, String[] comments){
            super(context, R.layout.row_comments, R.id.row_comment_username, usernames);
            this.context = context;
           // this.profilePics = profilePics;
            this.usernames = usernames;
            this.ratings = ratings;
            this.numbers = numbers;
            this.comments = comments;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.row_comments, parent, false);

            TextView username = row.findViewById(R.id.row_comment_username);
            RatingBar rating = row.findViewById(R.id.row_comment_rating);
            TextView number = row.findViewById(R.id.row_comment_number);
            TextView comment = row.findViewById(R.id.row_comment_comment);

            username.setText(usernames[position]);
            rating.setRating(ratings[position]);
            number.setText(numbers[position]);
            comment.setText(comments[position]);

            return row;
        }
    }
}