package pt.unl.fct.di.example.sharencare.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.institution.main_menu.MainMenuInstitutionActivity;
import pt.unl.fct.di.example.sharencare.user.main_menu.MainMenuUserActivity;

public class WelcomeActivity extends AppCompatActivity {

    private String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Intent intent = getIntent();
        String username = intent.getStringExtra("name_key");
        int type = intent.getIntExtra("user_type", 0);

        final TextView text = findViewById(R.id.activity_welcome_text);
        text.setText("Welcome " + username);

        Handler mHandler = new Handler();
        if (type == 0){
            mHandler.postDelayed(() -> startActivity(new Intent(WelcomeActivity.this, MainMenuUserActivity.class)), 1000L);
        }
        if(type == 1){
            mHandler.postDelayed(() -> startActivity(new Intent(WelcomeActivity.this, MainMenuInstitutionActivity.class)), 1000L);
        }
    }
}