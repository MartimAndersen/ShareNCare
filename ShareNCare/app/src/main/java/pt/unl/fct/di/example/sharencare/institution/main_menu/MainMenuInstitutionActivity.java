package pt.unl.fct.di.example.sharencare.institution.main_menu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.login.LoginActivity;
import pt.unl.fct.di.example.sharencare.databinding.ActivityMainMenuInstitutionBinding;
import pt.unl.fct.di.example.sharencare.institution.login.InstitutionInfo;
import pt.unl.fct.di.example.sharencare.institution.settings.SettingsInstitutionActivity;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;

public class MainMenuInstitutionActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainMenuInstitutionBinding binding;
    private static WeakReference<LoginActivity> loginActivity;

    private Gson gson;
    private SharedPreferences sharedpreferences;
    private Repository eventsRepository;
    private String galleryFolderName = "Share&Care";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        eventsRepository = eventsRepository.getInstance();
        gson = new Gson();

        binding = ActivityMainMenuInstitutionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View header = binding.navViewInstitution.getHeaderView(0);
        setHeader(header);


        setSupportActionBar(binding.appBarInstitution.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navViewInstitution;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home_institution, R.id.nav_profile_institution, R.id.nav_directions_institution, R.id.nav_events_institution)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_institution);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu_institution, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings_user:
                startActivity(new Intent(MainMenuInstitutionActivity.this, SettingsInstitutionActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setHeader(View view){
        String json = sharedpreferences.getString("USER", null);
        InstitutionInfo user = gson.fromJson(json, InstitutionInfo.class);

        ImageView profilePic = view.findViewById(R.id.nav_header_institution_profile_pic);
        TextView username = view.findViewById(R.id.nav_header_institution_username);
        TextView email = view.findViewById(R.id.nav_header_institution_email);

        String profilePicString = sharedpreferences.getString("PIC", null);

        if(profilePicString != null){
            byte[] decode = Base64.decode(profilePicString.getBytes(), 1);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
            profilePic.setImageBitmap(bitmap);
        }

        username.setText(user.getUsername());
        email.setText(user.getEmail());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_institution);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onAttachFragment(@NonNull @NotNull Fragment fragment) {
        super.onAttachFragment(fragment);
    }

    public void logoutUser(){
        loginActivity.get().logoutUser();
    }

    public static void updateActivity(LoginActivity activity) {
        loginActivity = new WeakReference<LoginActivity>(activity);
    }
}