package pt.unl.fct.di.example.sharencare.user.register;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.register.Repository;
import pt.unl.fct.di.example.sharencare.login.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;

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
            ProfileDataTags u = new ProfileDataTags(
                    getIntent().getStringExtra("name_key"),
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
                    Toast.makeText(TagsActivity.this, "Response Failed", Toast.LENGTH_SHORT).show();
                }
            });

        });
    }
}