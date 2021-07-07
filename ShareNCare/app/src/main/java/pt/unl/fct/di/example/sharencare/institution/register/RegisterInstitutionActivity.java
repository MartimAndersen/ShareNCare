package pt.unl.fct.di.example.sharencare.institution.register;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.login.LoginActivity;
import pt.unl.fct.di.example.sharencare.common.register.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterInstitutionActivity extends AppCompatActivity {
    private EditText nif, username, password, confirmation, email;
    private Button send;

    private Repository registerRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_institution);
        nif = findViewById(R.id.activity_register_institution_nif);
        username = findViewById(R.id.activity_register_institution_username);
        password = findViewById(R.id.activity_register_institution_password);
        confirmation = findViewById(R.id.activity_register_institution_confirmation);
        email = findViewById(R.id.activity_register_institution_email);
        send = findViewById(R.id.activity_register_institution_send);

        registerRepository = registerRepository.getInstance();

        send.setOnClickListener(v -> {
            findViewById(R.id.loading_register_institution).setVisibility(View.VISIBLE);
            RegisterInstitution i = new RegisterInstitution(
                    username.getText().toString(),
                    nif.getText().toString(),
                    email.getText().toString(),
                    password.getText().toString(),
                    confirmation.getText().toString()
                    );

            registerRepository.getRegisterService().registerInstitution(i).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                    findViewById(R.id.loading_register_institution).setVisibility(View.GONE);
                    findViewById(R.id.institution_nif_invalid).setVisibility(View.GONE);
                    nif.setBackgroundResource(R.drawable.rounded_corner);
                    findViewById(R.id.institution_email_invalid).setVisibility(View.GONE);
                    email.setBackgroundResource(R.drawable.rounded_corner);
                    findViewById(R.id.institution_username_invalid).setVisibility(View.GONE);
                    username.setBackgroundResource(R.drawable.rounded_corner);
                    findViewById(R.id.institution_password_invalid).setVisibility(View.GONE);
                    password.setBackgroundResource(R.drawable.rounded_corner);
                    findViewById(R.id.institution_confirmation_invalid).setVisibility(View.GONE);
                    confirmation.setBackgroundResource(R.drawable.rounded_corner);

                    if (r.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Institution Registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterInstitutionActivity.this, LoginActivity.class));
                    } else {
                        if (r.code() == 406) {
                            findViewById(R.id.institution_nif_invalid).setVisibility(View.VISIBLE);
                            nif.setBackgroundResource(R.drawable.red_error_corner);
                        }
                        if (r.code() == 403) {
                            findViewById(R.id.institution_email_invalid).setVisibility(View.VISIBLE);
                            email.setBackgroundResource(R.drawable.red_error_corner);
                        }
                        if (r.code() == 411) {
                            findViewById(R.id.institution_password_invalid).setVisibility(View.VISIBLE);
                            password.setBackgroundResource(R.drawable.red_error_corner);
                        }
                        if (r.code() == 417) {
                            findViewById(R.id.institution_confirmation_invalid).setVisibility(View.VISIBLE);
                            confirmation.setBackgroundResource(R.drawable.red_error_corner);
                        }
                        if (r.code() == 409) {
                            findViewById(R.id.institution_username_invalid).setVisibility(View.VISIBLE);
                            username.setBackgroundResource(R.drawable.red_error_corner);
                        }
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Error registering Institution: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return;


        });
    }
}
