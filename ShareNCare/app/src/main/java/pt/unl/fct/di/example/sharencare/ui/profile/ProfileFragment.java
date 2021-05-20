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

import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;

import pt.unl.fct.di.example.sharencare.MainMenuActivity;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.login.LoginActivity;

public class ProfileFragment extends Fragment {

    private SharedPreferences sharedpreferences;
    private ProfileViewModel mViewModel;
    private Button logout;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

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
        logout = getView().findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Activity act = getActivity();
                if (act instanceof MainMenuActivity) ((MainMenuActivity) act).logoutUser();


                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
    }
}