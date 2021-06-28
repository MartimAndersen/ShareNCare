package pt.unl.fct.di.example.sharencare.register;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.Repository;
import pt.unl.fct.di.example.sharencare.login.LoginActivity;
import pt.unl.fct.di.example.sharencare.ui.profile.ProfileUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class TagsActivity extends AppCompatActivity {

    private ChipGroup tags;
    private Button next;
    private Button skip;
    private Repository profileRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);
        tags = findViewById(R.id.activity_tags_tags);
        next = findViewById(R.id.activity_tags_next);
        skip = findViewById(R.id.activity_tags_skip);

        profileRepository = profileRepository.getInstance();

        skip.setOnClickListener(v -> {
            startActivity(new Intent(TagsActivity.this, LoginActivity.class));
        });

        next.setOnClickListener(v ->{
            ProfileUser u = new ProfileUser(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "public",
                    "",
                    tags.getCheckedChipIds(),
                    null,
                    ""
            );

            profileRepository.getProfileService().changeProfile(u).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    startActivity(new Intent(TagsActivity.this, LoginActivity.class));
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        });
    }
}