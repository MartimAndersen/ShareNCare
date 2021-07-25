package pt.unl.fct.di.example.sharencare.user.main_menu;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventData;
import pt.unl.fct.di.example.sharencare.common.events.EventMethods;
import pt.unl.fct.di.example.sharencare.databinding.ActivityMainMenuBinding;
import pt.unl.fct.di.example.sharencare.common.login.LoginActivity;
import pt.unl.fct.di.example.sharencare.institution.register.RegisterInstitutionActivity;
import pt.unl.fct.di.example.sharencare.institution.settings.AutocompleteActivity;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.FilterData;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.home.HomeFragment;
import pt.unl.fct.di.example.sharencare.user.settings.SettingsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainMenuUserActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainMenuBinding binding;
    private static WeakReference<LoginActivity> loginActivity;
    private static final int PERMISSION_CODE = 100;

    private String galleryFolderName = "Share&Care";
    private Gson gson;
    private SharedPreferences sharedpreferences;
    private Calendar myCalendar = Calendar.getInstance();
    private Repository eventsRepository;

    private Place place;
    private String coordinates;
    private EditText location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        eventsRepository = eventsRepository.getInstance();
        gson = new Gson();

        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View header = binding.navView.getHeaderView(0);
        setHeader(header);

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.homeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                   openCamera();
                }
                else{
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
                }
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_profile, R.id.nav_events, R.id.nav_tracks, R.id.nav_find_tracks, R.id.nav_rank)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    public void openCamera(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        File imagesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), galleryFolderName);

        if(!imagesFolder.exists())
            imagesFolder.mkdirs();

        File image = new File(imagesFolder, System.currentTimeMillis()+".jpg");
        Uri uriSavedImage = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName()+".provider", image);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivity(intent);

    }

    public void setHeader(View view){
        String json = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(json, UserInfo.class);

        ImageView profilePic = view.findViewById(R.id.nav_header_main_profile_pic);
        TextView username = view.findViewById(R.id.nav_header_main_username);
        TextView email = view.findViewById(R.id.nav_header_main_email);

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
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_CODE && (grantResults.length > 0) && (grantResults[0] + grantResults[1]  == PackageManager.PERMISSION_GRANTED)){
            openCamera();
        } else {
            Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings_user:
                startActivity(new Intent(MainMenuUserActivity.this, SettingsActivity.class));
                break;
            case R.id.action_record_track:
                startActivity(new Intent(MainMenuUserActivity.this, TrackOptionsActivity.class));
                break;
            case R.id.action_search_filters:
                startActivityForResult(new Intent(MainMenuUserActivity.this, FilterActivity.class), 101);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setPopup(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainMenuUserActivity.this);
        View view = getLayoutInflater().inflate(R.layout.popup_search_filters, null);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        EditText location = view.findViewById(R.id.popup_search_location);
        EditText date = view.findViewById(R.id.popup_search_date);
        EditText institution = view.findViewById(R.id.popup_search_institution);
        EditText name = view.findViewById(R.id.popup_search_name);
        ChipGroup tags = view.findViewById(R.id.popup_search_tags);
        Spinner popularity = view.findViewById(R.id.popup_search_popularity);
        Button apply = view.findViewById(R.id.popup_search_apply);
        String coordinates = "";

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuUserActivity. this, AutocompleteActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = "dd/MM/yy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                        date.setText(sdf.format(myCalendar.getTime()));
                    }
                };

                new DatePickerDialog(getApplicationContext(), dateDialog, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.search_popularity_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        popularity.setAdapter(adapter);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FilterData f = new FilterData(
                        coordinates,
                        date.getText().toString(),
                        institution.getText().toString(),
                        name.getText().toString(),
                        popularity.getSelectedItem().toString(),
                        getTags(tags)
                );


                String userInfo = sharedpreferences.getString("USER", null);
                UserInfo user = gson.fromJson(userInfo, UserInfo.class);

                eventsRepository.getEventsService().filterEvents(gson.toJson(f)).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                                if (r.isSuccessful()) {
                                    List<EventData> events = EventMethods.getMultipleEvents(r);

                                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_home, new HomeFragment()).commit();

                                    dialog.hide();

                                } else {
                                    Toast.makeText(MainMenuUserActivity.this, "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(MainMenuUserActivity.this, "NO", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == MainMenuUserActivity.RESULT_OK) {
            String lat_lon = data.getStringExtra("lat_lon");
            String[] split = lat_lon.split(" ");
            coordinates = Double.parseDouble(split[0]) + " " + Double.parseDouble(split[1]);

            String address = data.getStringExtra("address");
            location.setText(address);
        } else if(requestCode == 101){
            Intent intent = new Intent(MainMenuUserActivity.this, FiltersActivity.class);
            intent.putExtra("filters", data.getStringExtra("filter"));
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "FAILED", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
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


    private String getTags(ChipGroup tags){
        List<Integer> t = new ArrayList<>();
        for(int i = 0; i < tags.getChildCount(); i++) {
            Chip chip = (Chip) tags.getChildAt(i);
            if (chip.isChecked())
                t.add(i);
        }
        return gson.toJson(t);
    }
}