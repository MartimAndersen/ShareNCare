package pt.unl.fct.di.example.sharencare.user.main_menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.CommonMethods;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventData;
import pt.unl.fct.di.example.sharencare.common.events.EventMethods;
import pt.unl.fct.di.example.sharencare.institution.settings.AutocompleteActivity;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.FilterData;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.home.HomeFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterActivity extends AppCompatActivity {

    private Gson gson;
    private SharedPreferences sharedpreferences;
    private Repository eventsRepository;

    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_search_filters);

        gson = new Gson();
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        eventsRepository = eventsRepository.getInstance();

        setPopup();

    }

    public void setPopup(){
      /*  AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FilterActivity.this);
        View view = getLayoutInflater().inflate(R.layout.popup_search_filters, null);
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();*/

        EditText location = findViewById(R.id.popup_search_location);
        EditText date = findViewById(R.id.popup_search_date);
        EditText institution = findViewById(R.id.popup_search_institution);
        EditText name = findViewById(R.id.popup_search_name);
        ChipGroup tags = findViewById(R.id.popup_search_tags);
        Spinner popularity = findViewById(R.id.popup_search_popularity);
        Button apply = findViewById(R.id.popup_search_apply);
        String coordinates = "";

        CommonMethods.setTags(FilterActivity.this, tags);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FilterActivity. this, AutocompleteActivity.class);
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

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("filter", gson.toJson(events));
                            setResult(101, resultIntent);
                            finish();

                        } else {
                            Toast.makeText(FilterActivity.this, "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(FilterActivity.this, "NO", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private List<Integer> getTags(ChipGroup tags){
        List<Integer> t = new ArrayList<>();
        for(int i = 0; i < tags.getChildCount(); i++) {
            Chip chip = (Chip) tags.getChildAt(i);
            if (chip.isChecked())
                t.add(i);
        }
        return t;
    }
}