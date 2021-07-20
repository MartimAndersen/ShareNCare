package pt.unl.fct.di.example.sharencare.user.main_menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import pt.unl.fct.di.example.sharencare.R;

public class TrackOptionsActivity extends AppCompatActivity {

    private RadioGroup options;
    private Button cancel, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_options);

        options = findViewById(R.id.activity_track_options_radio);
        cancel = findViewById(R.id.track_options_cancel);
        next = findViewById(R.id.track_options_next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(options.getCheckedRadioButtonId()){
                    case R.id.track_options_live:
                        startActivity(new Intent(TrackOptionsActivity.this, LiveTrackActivity.class));
                        break;
                    case R.id.track_options_own:
                        startActivity(new Intent(TrackOptionsActivity.this, MakeTrackActivity.class));
                        break;
                    case R.id.track_options_premade:
                    //    startActivity(new Intent(TrackOptionsActivity.this, ));
                        break;
                }

            }
        });


    }
}