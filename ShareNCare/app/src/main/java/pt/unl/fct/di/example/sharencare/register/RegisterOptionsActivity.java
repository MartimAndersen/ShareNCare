package pt.unl.fct.di.example.sharencare.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;

import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.institution.register.RegisterInstitutionActivity;
import pt.unl.fct.di.example.sharencare.user.register.RegisterActivity;

public class RegisterOptionsActivity extends AppCompatActivity {

    private RadioButton volunteer;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_options);

        volunteer = findViewById(R.id.activity_options_volunteer);
        next = findViewById(R.id.activity_options_continue);


        next.setOnClickListener(v -> {
            if(volunteer.isChecked()){
                startActivity(new Intent(RegisterOptionsActivity.this, RegisterActivity.class));
            } else {
                startActivity(new Intent(RegisterOptionsActivity.this, RegisterInstitutionActivity.class));
            }

        });

    }
}