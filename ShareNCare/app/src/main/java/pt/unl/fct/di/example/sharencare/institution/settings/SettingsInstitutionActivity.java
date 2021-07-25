package pt.unl.fct.di.example.sharencare.institution.settings;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.login.LoginActivity;
import pt.unl.fct.di.example.sharencare.institution.login.InstitutionInfo;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.settings.AboutUsActivity;
import pt.unl.fct.di.example.sharencare.user.settings.ChangeEmailData;
import pt.unl.fct.di.example.sharencare.user.settings.ChangePasswordData;
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

public class SettingsInstitutionActivity extends AppCompatActivity {

    TextView email, password, deleteAccount, logout, abandonEvent, editEvent, about;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private SharedPreferences sharedpreferences;
    private Gson gson;
    private Repository service;
    private static WeakReference<LoginActivity> loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_institution);
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        gson = new Gson();

        service = service.getInstance();

        email = findViewById(R.id.settings_space_email_ins);
        password = findViewById(R.id.settings_space_password_ins);
        deleteAccount = findViewById(R.id.settings_space_delete_account_ins);
        logout = findViewById(R.id.settings_space_logout_ins);
        abandonEvent = findViewById(R.id.settings_space_abandon_event_ins);
        editEvent = findViewById(R.id.settings_space_edit_event_ins);
        about = findViewById(R.id.settings_space_about_us_ins);

        String institutionInfo = sharedpreferences.getString("USER", null);
        InstitutionInfo user = gson.fromJson(institutionInfo, InstitutionInfo.class);


        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(SettingsInstitutionActivity.this);
                final View popupEmail = getLayoutInflater().inflate(R.layout.popup_change_email, null);
                setEmailPopup(popupEmail, user);
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(SettingsInstitutionActivity.this);
                final View popupPassword = getLayoutInflater().inflate(R.layout.popup_change_password, null);
                setPasswordPopup(popupPassword, user);
            }
        });

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder = new AlertDialog.Builder(SettingsInstitutionActivity.this);
                final View popupDelete = getLayoutInflater().inflate(R.layout.popup_delete_account, null);
                setDeleteAccountPopup(popupDelete, user);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser(user);
            }
        });

        abandonEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsInstitutionActivity.this, DeleteEventActivity.class));
            }
        });

        editEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsInstitutionActivity.this, EditEventChooseActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsInstitutionActivity.this, AboutUsActivity.class));
            }
        });
    }

    private void setEmailPopup(View view, InstitutionInfo user){
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

                service.getProfileService().changeEmailInstitution(user.getToken(), data).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            user.setEmail(newEmail.getText().toString());
                            dialog.hide();

                            SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                            String u = gson.toJson(user);
                            prefsEditor.putString("USER", u);
                            prefsEditor.apply();
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

    private void setPasswordPopup(View view, InstitutionInfo user){
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

                service.getProfileService().changePasswordInstitution(user.getToken(), data).enqueue(new Callback<ResponseBody>() {
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

    private void setDeleteAccountPopup(View view, InstitutionInfo user){
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        dialog.show();

        Button cancel, delete;

        cancel = view.findViewById(R.id.popup_delete_account_cancel);
        delete = view.findViewById(R.id.popup_delete_account_delete);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                service.getDeleteService().deleteInstitution(user.getToken(), user.getNif()).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            dialog.hide();
                            logoutUser(user);
                            Handler mHandler = new Handler();
                            mHandler.postDelayed(() -> startActivity(new Intent(SettingsInstitutionActivity.this, LoginActivity.class)), 1000L);
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

    public void logoutUser(InstitutionInfo user){
        service.getProfileService().userLogout(user.getToken()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()) {
                    loginActivity.get().logoutUser();
                    Intent intent = new Intent(SettingsInstitutionActivity.this, LoginActivity.class);
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
}