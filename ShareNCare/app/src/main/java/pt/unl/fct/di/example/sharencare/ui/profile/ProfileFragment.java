package pt.unl.fct.di.example.sharencare.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.session.MediaSessionManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import kotlin.reflect.KCallable;
import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.MainMenuActivity;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.Repository;
import pt.unl.fct.di.example.sharencare.login.LoginActivity;
import pt.unl.fct.di.example.sharencare.register.RegisterUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedpreferences;
    private ProfileViewModel mViewModel;

    private TextView email;
    private TextView mobile;
    private TextView landline;
    private TextView address;
    private TextView secondAddress;
    private TextView zipCode;

    private Switch publicProfile;

    public ChipGroup tags;

    private EditText editedEmail;
    private EditText editedMobile;
    private EditText editedLandline;
    private EditText editedAddress;
    private EditText editedSecondAddress;
    private EditText editedZipCode;

    private ImageButton changeAttributes;
    private boolean changing;

    private ImageButton save;
    private Button logout;

    private Repository profileRepository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        profileRepository = profileRepository.getInstance();
        changing = false;

        email = getView().findViewById(R.id.email);
        address = getView().findViewById(R.id.address);
        secondAddress = getView().findViewById(R.id.second_address);
        mobile = getView().findViewById(R.id.mobile);
        landline = getView().findViewById(R.id.landline);
        zipCode = getView().findViewById(R.id.zip);

        publicProfile = getView().findViewById(R.id.fragment_profile_public);

        tags = getView().findViewById(R.id.fragment_profile_tags);

        editedEmail = getView().findViewById(R.id.fragment_profile_email);
        editedAddress = getView().findViewById(R.id.fragment_profile_address);
        editedSecondAddress = getView().findViewById(R.id.fragment_profile_second_address);
        editedMobile = getView().findViewById(R.id.fragment_profile_mobile);
        editedLandline = getView().findViewById(R.id.fragment_profile_landline);
        editedZipCode = getView().findViewById(R.id.fragment_profile_zip_code);

        changeAttributes = getView().findViewById(R.id.edit_attributes);

        changeAttributes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changing = true;

                email.setVisibility(View.GONE);
                editedEmail.setVisibility(View.VISIBLE);

                address.setVisibility(View.INVISIBLE);
                editedAddress.setVisibility(View.VISIBLE);

                secondAddress.setVisibility(View.INVISIBLE);
                editedSecondAddress.setVisibility(View.VISIBLE);

                mobile.setVisibility(View.INVISIBLE);
                editedMobile.setVisibility(View.VISIBLE);

                landline.setVisibility(View.INVISIBLE);
                editedLandline.setVisibility(View.VISIBLE);

                zipCode.setVisibility(View.INVISIBLE);
                editedZipCode.setVisibility(View.VISIBLE);
            }
        });

        save = getView().findViewById(R.id.save_changes);
        save.setOnClickListener(v -> {
            if(changing){

                String profileType;
                if(publicProfile.isChecked())
                    profileType = "public";
                else
                    profileType = "private";

            ProfileUser u = new ProfileUser(
                    editedEmail.getText().toString(),
                    editedMobile.getText().toString(),
                    editedLandline.getText().toString(),
                    editedAddress.getText().toString(),
                    editedZipCode.getText().toString(),
                    profileType,
                    editedSecondAddress.getText().toString(),
                    tags.getCheckedChipIds(),
                    //"",
                    null,
                    "a6ff8031-96d2-4b08-9cfc-726c0a280859"

            );

            profileRepository.getProfileService().changeProfile(u).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                    if(r.isSuccessful()){
                        Toast.makeText(getContext(), "Managed to change Attributes", Toast.LENGTH_SHORT).show();
                        email.setText(editedEmail.getText().toString());
                        editedEmail.setVisibility(View.INVISIBLE);
                        email.setVisibility(View.VISIBLE);

                        address.setText(editedAddress.getText().toString());
                        editedAddress.setVisibility(View.INVISIBLE);
                        address.setVisibility(View.VISIBLE);

                        secondAddress.setText(editedSecondAddress.getText().toString());
                        editedSecondAddress.setVisibility(View.INVISIBLE);
                        secondAddress.setVisibility(View.VISIBLE);

                        mobile.setText(editedMobile.getText().toString());
                        editedMobile.setVisibility(View.INVISIBLE);
                        mobile.setVisibility(View.VISIBLE);

                        landline.setText(editedLandline.getText().toString());
                        editedLandline.setVisibility(View.INVISIBLE);
                        landline.setVisibility(View.VISIBLE);

                        zipCode.setText(editedZipCode.getText().toString());
                        editedZipCode.setVisibility(View.INVISIBLE);
                        zipCode.setVisibility(View.VISIBLE);
                    }
                    else {
                        Toast.makeText(getContext(), "error code: "+r.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        }});

        logout = getView().findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity act = getActivity();
                if (act instanceof MainMenuActivity)
                    ((MainMenuActivity) act).logoutUser();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
              //  intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(intent);
            }
        });
    }
}