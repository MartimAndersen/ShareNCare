package pt.unl.fct.di.example.teste3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private EditText username, password;
    private Button send;
    private Button register;

    private Repository loginRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.activity_login_username);
        password = findViewById(R.id.activity_login_password);
        send = findViewById(R.id.activity_login_send);
        register = findViewById(R.id.activity_login_register);
        register.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "HERE ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        loginRepository = loginRepository.getInstance();

        send.setOnClickListener(v -> {
            findViewById(R.id.loading).setVisibility(View.VISIBLE);
            LoginUser u = new LoginUser(
                    username.getText().toString(),
                    password.getText().toString()
            );

            loginRepository.getLoginService().loginUser(u).enqueue(new Callback<LoginUser>() {
                @Override
                public void onResponse(Call<LoginUser> call, Response<LoginUser> r) {
                    findViewById(R.id.loading).setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "RESPONSE: " + r, Toast.LENGTH_SHORT).show();
                    if(r.isSuccessful()){
                        startActivity(new Intent(LoginActivity.this, MapActivity.class));

                    } else{
                        findViewById(R.id.wrong_credentials).setVisibility(View.VISIBLE);
                    }
                }
                @Override
                public void onFailure(Call<LoginUser> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error Logging User: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return;

        });
    }
}