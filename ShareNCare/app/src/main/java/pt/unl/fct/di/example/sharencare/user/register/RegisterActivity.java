package pt.unl.fct.di.example.sharencare.user.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, password, confirmation, email;
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
        send = findViewById(R.id.activity_register_send);

        registerRepository = registerRepository.getInstance();

        send.setOnClickListener(v -> {
          //  sendEmail();
            String name = username.getText().toString();
            findViewById(R.id.loading_register).setVisibility(View.VISIBLE);
            RegisterUser u = new RegisterUser(
                    name,
                    password.getText().toString(),
                    confirmation.getText().toString(),
                    email.getText().toString()
            );

                    registerRepository.getRegisterService().registerUser(u).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                            findViewById(R.id.loading_register).setVisibility(View.GONE);
                            findViewById(R.id.email_invalid).setVisibility(View.GONE);
                            email.setBackgroundResource(R.drawable.rounded_corner);
                            findViewById(R.id.username_invalid).setVisibility(View.GONE);
                            username.setBackgroundResource(R.drawable.rounded_corner);
                            findViewById(R.id.password_invalid).setVisibility(View.GONE);
                            password.setBackgroundResource(R.drawable.rounded_corner);
                            findViewById(R.id.confirmation_invalid).setVisibility(View.GONE);
                            confirmation.setBackgroundResource(R.drawable.rounded_corner);

                            if(r.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "User Registered", Toast.LENGTH_SHORT).show();
                                Intent tagActivity = new Intent(RegisterActivity.this, TagsActivity.class);
                                tagActivity.putExtra("name_user", name);
                                startActivity(tagActivity);
                            }
                           else{
                               if(r.code() == 403){
                                   findViewById(R.id.email_invalid).setVisibility(View.VISIBLE);
                                   email.setBackgroundResource(R.drawable.red_error_corner);
                               } if(r.code() == 411){
                                    findViewById(R.id.password_invalid).setVisibility(View.VISIBLE);
                                    password.setBackgroundResource(R.drawable.red_error_corner);
                               } if(r.code() == 417){
                                    findViewById(R.id.confirmation_invalid).setVisibility(View.VISIBLE);
                                    confirmation.setBackgroundResource(R.drawable.red_error_corner);
                               } if(r.code() == 409){
                                    findViewById(R.id.username_invalid).setVisibility(View.VISIBLE);
                                    username.setBackgroundResource(R.drawable.red_error_corner);
                               }
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

    protected void sendEmail(String email, String password) {
        BackgroundMail.newBuilder(this)
                .withUsername("username@gmail.com")
                .withPassword("password12345")
                .withMailto("to-email@gmail.com")
                .withSubject("Code to Share&Care")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withBody("Your Share&Care registration code: ")
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(), "HELLO", Toast.LENGTH_SHORT);
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        Toast.makeText(getApplicationContext(), ":(((", Toast.LENGTH_SHORT);
                    }
                })
                .send();
    }
}