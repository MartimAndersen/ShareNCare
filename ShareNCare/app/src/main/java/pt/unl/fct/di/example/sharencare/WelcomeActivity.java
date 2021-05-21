package pt.unl.fct.di.example.sharencare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Intent intent = getIntent();
        String username = intent.getStringExtra("name_key");

        final TextView text = findViewById(R.id.activity_welcome_text);
        text.setText("Welcome " + username);

        Handler mHandler = new Handler();
        mHandler.postDelayed(() -> startActivity(new Intent(WelcomeActivity.this, MainMenuActivity.class)), 3000L);

    }
}