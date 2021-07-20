package pt.unl.fct.di.example.sharencare.common.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.register.RegisterOptionsActivity;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.institution.login.InstitutionInfo;
import pt.unl.fct.di.example.sharencare.institution.main_menu.MainMenuInstitutionActivity;
import pt.unl.fct.di.example.sharencare.institution.login.LoginInstitution;
import pt.unl.fct.di.example.sharencare.user.login.LoginUser;
import pt.unl.fct.di.example.sharencare.user.login.PointsData;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.MainMenuUserActivity;

import pt.unl.fct.di.example.sharencare.user.main_menu.SettingsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Serializable {
    private EditText username, password, nif;
    private TextView switchI, switchU;
    private Button send, sendInstitution, register;
    private ImageButton darkMode, lightMode;
    private CheckBox remember;
    private SharedPreferences sharedpreferences;

    private Repository loginRepository;

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainMenuUserActivity.updateActivity(this);
        MainMenuInstitutionActivity.updateActivity(this);
        SettingsActivity.updateActivity(this);

        sharedpreferences = getApplicationContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        String login = sharedpreferences.getString("LOGIN", null);
        Intent openWelcomeActivity = new Intent(LoginActivity.this, WelcomeActivity.class);

        if (login != null) {
            List<String> user = gson.fromJson(login, List.class);
            openWelcomeActivity.putExtra("name_key", user.get(0));
            openWelcomeActivity.putExtra("user_type", Integer.parseInt(user.get(1)));
            startActivity(openWelcomeActivity);
        } else {
            setContentView(R.layout.activity_login);
            username = findViewById(R.id.activity_login_username);
            nif = findViewById(R.id.activity_login_nif);
            password = findViewById(R.id.activity_login_password);
            send = findViewById(R.id.activity_login_send);
            sendInstitution = findViewById(R.id.activity_login_send_institution);
            register = findViewById(R.id.activity_login_register);
            remember = findViewById(R.id.remember_me);

            switchI = findViewById(R.id.activity_login_institution);
            switchU = findViewById(R.id.activity_login_user);

            lightMode = findViewById(R.id.activity_login_light_mode);
            darkMode = findViewById(R.id.activity_login_dark_mode);

            username.setVisibility(View.VISIBLE);
            switchI.setVisibility(View.VISIBLE);
           // darkMode.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);

            switchI.setOnClickListener(v -> {
            /*    setTheme(R.style.Theme_Institution);
                setContentView(R.layout.activity_login);
                username = findViewById(R.id.activity_login_username);
                password = findViewById(R.id.activity_login_password);
                nif = findViewById(R.id.activity_login_nif);

                switchU = findViewById(R.id.activity_login_user);
                switchI = findViewById(R.id.activity_login_institution);

                send = findViewById(R.id.activity_login_send);
                sendInstitution = findViewById(R.id.activity_login_send_institution);
                register = findViewById(R.id.activity_login_register);

                lightMode = findViewById(R.id.activity_login_light_mode);
                darkMode = findViewById(R.id.activity_login_dark_mode);*/

                username.setVisibility(View.INVISIBLE);
                switchI.setVisibility(View.INVISIBLE);
                send.setVisibility(View.INVISIBLE);
                nif.setVisibility(View.VISIBLE);
                switchU.setVisibility(View.VISIBLE);
                sendInstitution.setVisibility(View.VISIBLE);
              //  lightMode.setVisibility(View.VISIBLE);
              //  darkMode.setVisibility(View.INVISIBLE);
            });

            switchU.setOnClickListener(v -> {
             /*   setTheme(R.style.Theme_Teste3);
                setContentView(R.layout.activity_login);
                username = findViewById(R.id.activity_login_username);
                nif = findViewById(R.id.activity_login_nif);
                password = findViewById(R.id.activity_login_password);
                send = findViewById(R.id.activity_login_send);
                sendInstitution = findViewById(R.id.activity_login_send_institution);
                register = findViewById(R.id.activity_login_register);
                remember = findViewById(R.id.remember_me);

                switchI = findViewById(R.id.activity_login_institution);
                switchU = findViewById(R.id.activity_login_user);

                lightMode = findViewById(R.id.activity_login_light_mode);
                darkMode = findViewById(R.id.activity_login_dark_mode);*/

                nif.setVisibility(View.INVISIBLE);
                switchU.setVisibility(View.INVISIBLE);
                sendInstitution.setVisibility(View.INVISIBLE);
                username.setVisibility(View.VISIBLE);
                switchI.setVisibility(View.VISIBLE);
                send.setVisibility(View.VISIBLE);
              //  darkMode.setVisibility(View.VISIBLE);
              //  lightMode.setVisibility(View.INVISIBLE);
            });


            register.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterOptionsActivity.class));
            });

            loginRepository = loginRepository.getInstance();

            send.setOnClickListener(v -> {
                findViewById(R.id.loading).setVisibility(View.VISIBLE);
                LoginUser u = new LoginUser(
                        username.getText().toString(),
                        password.getText().toString(),
                        false
                );

                loginRepository.getLoginService().loginUser(u).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        findViewById(R.id.loading).setVisibility(View.GONE);
                        if (r.isSuccessful()) {
                            if (remember.isChecked()) {
                                loginUser(u.getUsername(), 0);
                            }

                            saveUser(r);

                            openWelcomeActivity.putExtra("name_key", username.getText().toString());
                            openWelcomeActivity.putExtra("user_type", 0);
                            startActivity(openWelcomeActivity);
                        } else {
                            username.setBackgroundResource(R.drawable.red_error_corner);
                            password.setBackgroundResource(R.drawable.red_error_corner);
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

            sendInstitution.setOnClickListener(v -> {
                findViewById(R.id.loading).setVisibility(View.VISIBLE);
                LoginInstitution i = new LoginInstitution(
                        nif.getText().toString(),
                        password.getText().toString(),
                        false
                );

                loginRepository.getLoginService().loginInstitution(i).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        findViewById(R.id.loading).setVisibility(View.GONE);
                        if (r.isSuccessful()) {
                            saveInstitution(r);

                            String institutionInfo = sharedpreferences.getString("USER", null);
                            InstitutionInfo ins = gson.fromJson(institutionInfo, InstitutionInfo.class);

                            if (remember.isChecked()) {
                                loginUser(ins.getUsername(), 1);
                            }

                            openWelcomeActivity.putExtra("name_key", ins.getUsername());
                            openWelcomeActivity.putExtra("user_type", 1);
                            startActivity(openWelcomeActivity);
                        } else {
                            nif.setBackgroundResource(R.drawable.red_error_corner);
                            password.setBackgroundResource(R.drawable.red_error_corner);
                            findViewById(R.id.wrong_credentials).setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Error Logging Institution: " + r.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Error Logging Institution: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
                return;

            });
        }
    }

    public void loginUser(String username, int type) {
        List<String> user = new ArrayList<String>(2);
        user.add(username);
        user.add(String.valueOf(type));
        String json = gson.toJson(user);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("LOGIN", json);
        editor.apply();
    }

    public void logoutUser() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void saveUser(Response<ResponseBody> r){
        if (r.isSuccessful()) {
            try {
                JSONArray array = new JSONArray(r.body().string());
                List<LinkedTreeMap> list = gson.fromJson(array.toString(), List.class);
                List<String> values = new ArrayList<String>();
                for(int i = 0; i < list.size(); i++)
                    values.add(list.get(i).get("value").toString());

                String profileType = values.get(9);

                UserInfo u = new UserInfo(
                        values.get(0),
                        values.get(1),
                        values.get(2),
                        getEvents(values.get(3)),
                        values.get(4),
                        values.get(5),
                        getTracks(values.get(6)),
                        getPoints(values.get(8)),
                        values.get(9),
                        values.get(11),
                        getTags(values.get(13)),
                        r.headers().values("Set-Cookie"),
                        values.get(14),
                        values.get(15)
                );

                        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                        String json = gson.toJson(u);
                        prefsEditor.putString("USER", json);
                        prefsEditor.apply();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(this, "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
    }

    public void saveInstitution(Response<ResponseBody> r){
        if (r.isSuccessful()) {
            try {
                JSONArray array = new JSONArray(r.body().string());
                List<LinkedTreeMap> list = gson.fromJson(array.toString(), List.class);
                List<String> values = new ArrayList<String>();
                for(int i = 0; i < list.size(); i++)
                    values.add(list.get(i).get("value").toString());

                InstitutionInfo ins = new InstitutionInfo(
                        values.get(0),
                        values.get(1),
                        values.get(2),
                        values.get(3),
                        getEvents(values.get(4)),
                        values.get(5),
                        values.get(6),
                        values.get(7),
                        values.get(8),
                        values.get(9),
                        values.get(10),
                        values.get(14),
                        values.get(15),
                        values.get(16),
                        values.get(17),
                        values.get(18),
                        r.headers().values("Set-Cookie")
                );

                SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                String json = gson.toJson(ins);
                prefsEditor.putString("USER", json);
                prefsEditor.apply();

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            Toast.makeText(this, "FAILED: " + r.code(), Toast.LENGTH_SHORT).show();
    }

    private List<Integer> getTags(String tags){
        return gson.fromJson(tags, List.class);
    }

    private List<String> getEvents(String events){
        return gson.fromJson(events, List.class);
    }

    private List<String> getTracks(String tracks){
        return gson.fromJson(tracks, List.class);
    }

    private PointsData getPoints(String points){
        Type t = new TypeToken<PointsData>(){}.getType();
        return gson.fromJson(points, t);
    }

}