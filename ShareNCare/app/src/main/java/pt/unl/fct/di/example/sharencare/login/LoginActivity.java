package pt.unl.fct.di.example.sharencare.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.register.RegisterActivity;
import pt.unl.fct.di.example.sharencare.Repository;
import pt.unl.fct.di.example.sharencare.WelcomeActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText username, password;
    private Button send;
    private Button register;
    private CheckBox remember;
    private SharedPreferences sharedpreferences;

    private Repository loginRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedpreferences = getApplicationContext().getSharedPreferences("Preferences", 0);
        String login = sharedpreferences.getString("LOGIN", null);
        Intent openWelcomeActivity = new Intent(LoginActivity.this, WelcomeActivity.class);
        // input username
        openWelcomeActivity.putExtra("name_key","hello");

        if (login != null)
            startActivity(openWelcomeActivity);
        else {
            setContentView(R.layout.activity_login);
            username = findViewById(R.id.activity_login_username);
            password = findViewById(R.id.activity_login_password);
            send = findViewById(R.id.activity_login_send);
            register = findViewById(R.id.activity_login_register);
            remember = findViewById(R.id.remember_me);

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

                loginRepository.getLoginService().loginUser(u).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        findViewById(R.id.loading).setVisibility(View.GONE);
                        if (r.isSuccessful()) {
                            if(remember.isChecked()) {
                                loginUser(u.getUsername());
                            }
                            openWelcomeActivity.putExtra("name_key", username.getText().toString());
                            startActivity(openWelcomeActivity);
                        } else {
                            findViewById(R.id.wrong_credentials).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error Logging User: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
                return;

            });
        }
    }

    public void loginUser(String username){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("LOGIN", username);
        editor.commit();
    }

    public void logoutUser(){
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("LOGIN");
        editor.commit();
    }
}