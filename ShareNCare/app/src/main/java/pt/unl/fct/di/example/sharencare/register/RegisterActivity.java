package pt.unl.fct.di.example.sharencare.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.Repository;
import pt.unl.fct.di.example.sharencare.login.LoginActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, password, confirmation, email, address, mobile, postal, secondAddress, landLine, profileType;
    private Button send;

    private Repository registerRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.activity_register_username);
        password = findViewById(R.id.activity_register_password);
        confirmation = findViewById(R.id.activity_register_confirmation);
        email = findViewById(R.id.activity_register_email);
/*
        address = findViewById(R.id.activity_register_address);
        mobile = findViewById(R.id.activity_register_mobile);
        postal = findViewById(R.id.activity_register_postal);
        secondAddress = findViewById(R.id.activity_register_second_address);
        landLine = findViewById(R.id.activity_register_landLine);
        profileType = findViewById(R.id.activity_register_profileType);*/
        send = findViewById(R.id.activity_register_send);

        registerRepository = registerRepository.getInstance();

        send.setOnClickListener(v -> {
            RegisterUser u = new RegisterUser(
                    username.getText().toString(),
                    password.getText().toString(),
                    confirmation.getText().toString(),
                    email.getText().toString(),/*,
                    address.getText().toString(),
                    mobile.getText().toString(),
                    postal.getText().toString(),
                    secondAddress.getText().toString(),
                    landLine.getText().toString(),
                    profileType.getText().toString()*/
                    "","","","","",""

            );



                    registerRepository.getRegisterService().registerUser(u).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                            if(r.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "User Registered", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            }
                           else{
                                Toast.makeText(getApplicationContext(), "Credentials are invalid: " + r, Toast.LENGTH_SHORT).show();
                            }

                        }
                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Error registering User: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;


        });
    }
}