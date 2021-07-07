package pt.unl.fct.di.example.sharencare.user.register;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.login.LoginActivity;
import pt.unl.fct.di.example.sharencare.common.register.Repository;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

public class TagsActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;
    private Gson gson;
    private ChipGroup tags;
    private Button next;
    private Button skip;
    private Repository profileRepository;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        Intent intent = getIntent();
        username = intent.getStringExtra("name_user");

        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        tags = findViewById(R.id.activity_tags_tags);
        next = findViewById(R.id.activity_tags_next);
        skip = findViewById(R.id.activity_tags_skip);

        profileRepository = profileRepository.getInstance();

        skip.setOnClickListener(v -> {
            startActivity(new Intent(TagsActivity.this, LoginActivity.class));
        });

        next.setOnClickListener(v ->{
            ProfileDataTags u = new ProfileDataTags(
                    username,
                    tags.getCheckedChipIds()

            );

            profileRepository.getProfileService().changeProfileTags(u).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                    if(r.isSuccessful())
                        startActivity(new Intent(TagsActivity.this, LoginActivity.class));
                    else
                        Toast.makeText(TagsActivity.this, "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(TagsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}