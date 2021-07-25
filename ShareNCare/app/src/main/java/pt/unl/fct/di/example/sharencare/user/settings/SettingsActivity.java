package pt.unl.fct.di.example.sharencare.user.settings;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.login.LoginActivity;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.lang.ref.WeakReference;

public class SettingsActivity extends AppCompatActivity {

    TextView email, password, deleteAccount, logout, abandonEvent, about;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private SharedPreferences sharedpreferences;
    private Gson gson;
    private Repository service;
    private static WeakReference<LoginActivity> loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        service = service.getInstance();

        email = findViewById(R.id.settings_space_email);
        password = findViewById(R.id.settings_space_password);
        deleteAccount = findViewById(R.id.settings_space_delete_account);
        logout = findViewById(R.id.settings_space_logout);
        abandonEvent = findViewById(R.id.settings_space_abandon_event);
        about = findViewById(R.id.settings_space_about_us);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                final View popupEmail = getLayoutInflater().inflate(R.layout.popup_change_email, null);
                setEmailPopup(popupEmail);
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                final View popupPassword = getLayoutInflater().inflate(R.layout.popup_change_password, null);
                setPasswordPopup(popupPassword);
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                final View popupDelete = getLayoutInflater().inflate(R.layout.popup_delete_account, null);
                setDeleteAccountPopup(popupDelete);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        abandonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, AbandonEventActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, AboutUsActivity.class));
            }
        });
    }

    private void setEmailPopup(View view){
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        TextView email;
        EditText newEmail, password;
        Button cancel, save;

        email = view.findViewById(R.id.popup_email_text);
        newEmail = view.findViewById(R.id.popup_change_email_new);
        password = view.findViewById(R.id.popup_change_email_password);
        cancel = view.findViewById(R.id.popup_change_email_cancel);
        save = view.findViewById(R.id.popup_change_email_save);

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        email.setText(user.getEmail());

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeEmailData data = new ChangeEmailData(
                  user.getEmail(),
                  newEmail.getText().toString(),
                  password.getText().toString()
                );

                service.getProfileService().changeEmail(user.getToken(), data).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            user.setEmail(newEmail.getText().toString());
                            dialog.hide();

                            SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                            String u = gson.toJson(user);
                            prefsEditor.putString("USER", u);
                            prefsEditor.apply();
                            Toast.makeText(getApplicationContext(), "HELLLLLLLLLO"+r.code(), Toast.LENGTH_SHORT);
                        }
                        else
                            Toast.makeText(getApplicationContext(), "CODE: "+r.code(), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    private void setPasswordPopup(View view){
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        EditText oldPassword, newPassword, confirmation;
        Button cancel, save;

        oldPassword = view.findViewById(R.id.popup_change_password_old);
        newPassword = view.findViewById(R.id.popup_change_password_new);
        confirmation = view.findViewById(R.id.popup_change_password_confirmation);
        cancel = view.findViewById(R.id.popup_change_password_cancel);
        save = view.findViewById(R.id.popup_change_password_save);

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangePasswordData data = new ChangePasswordData(
                        oldPassword.getText().toString(),
                        newPassword.getText().toString(),
                        confirmation.getText().toString()
                );

                service.getProfileService().changePassword(user.getToken(), data).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            dialog.hide();
                        }
                        else
                            Toast.makeText(getApplicationContext(), "CODE: "+r.code(), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

    }

    private void setDeleteAccountPopup(View view){
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        Button cancel, delete;

        cancel = view.findViewById(R.id.popup_delete_account_cancel);
        delete = view.findViewById(R.id.popup_delete_account_delete);

        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                service.getDeleteService().deleteUser(user.getToken(), user.getUsername()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            dialog.hide();
                            logoutUser();
                            Handler mHandler = new Handler();
                            mHandler.postDelayed(() -> startActivity(new Intent(SettingsActivity.this, LoginActivity.class)), 1000L);
                        }
                        else
                            Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    public void logoutUser(){
        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        service.getProfileService().userLogout(user.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()) {
                    loginActivity.get().logoutUser();
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT);
            }
        });
    }

    public static void updateActivity(LoginActivity activity) {
        loginActivity = new WeakReference<LoginActivity>(activity);
    }


    /*  @Override
            public void onClick(View v) {
                service.getDeleteService().deleteUser(user.getToken(), user.getUsername()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful())
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }*/

}