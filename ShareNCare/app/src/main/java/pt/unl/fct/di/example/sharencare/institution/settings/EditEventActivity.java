package pt.unl.fct.di.example.sharencare.institution.settings;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.io.IOException;
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
import pt.unl.fct.di.example.sharencare.institution.login.InstitutionInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.AddEventData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEventActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;
    private Gson gson;

    private enum tag {animals, environment, children, elderly, supplies, homeless}

    private EditText dateFrom, dateTo, time, location, minParticipants, maxParticipants, description;
    private TextView name;
    private Spinner durability;
    private ChipGroup tags;
    private ImageButton edit, save;
    private Repository eventsRepository;
    private Double lat, lon;
    private String date1, date2, time1;

    private String eventName;

    final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        gson = new Gson();
        eventsRepository = eventsRepository.getInstance();

        eventName = getIntent().getStringExtra("event_name");

        name = findViewById(R.id.activity_edit_events_text);

        dateFrom = findViewById(R.id.activity_edit_events_date_from);
        dateTo = findViewById(R.id.activity_edit_events_date_to);
        location = findViewById(R.id.activity_edit_events_location);
        time = findViewById(R.id.activity_edit_events_time);
        minParticipants = findViewById(R.id.activity_edit_events_min_participants);
        maxParticipants = findViewById(R.id.activity_edit_events_max_participants);

        durability = findViewById(R.id.activity_edit_events_durability);
        description = findViewById(R.id.activity_edit_events_description);

        tags = findViewById(R.id.activity_edit_events_tags);
        edit = findViewById(R.id.activity_edit_events_edit);
        save = findViewById(R.id.activity_edit_events_save);

        date1 = "";
        date2 = "";
        time1 = "";

        CommonMethods.setTags(EditEventActivity.this, tags);

        dateFrom.setEnabled(false);
        dateTo.setEnabled(false);
        location.setEnabled(false);
        time.setEnabled(false);
        minParticipants.setEnabled(false);
        maxParticipants.setEnabled(false);
        durability.setEnabled(false);
        description.setEnabled(false);
        tags.setEnabled(false);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditEventActivity.this,
                R.array.events_durability_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durability.setAdapter(adapter);

        setAttributes();

        dateFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabelDateFrom();
                    }
                };

                new DatePickerDialog(EditEventActivity.this, dateDialog, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        dateTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateDialog = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabelDateTo();
                    }
                };

                new DatePickerDialog(EditEventActivity.this, dateDialog, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = myCalendar.get(Calendar.HOUR);
                int minute = myCalendar.get(Calendar.MINUTE);

                TimePickerDialog timeDialog = new TimePickerDialog(EditEventActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                        time1 = String.format("%02d:%02d", hourOfDay, minutes);
                        time.setText(String.format("%02d:%02d", hourOfDay, minutes));
                    }
                }, hour, minute, true);

                timeDialog.show();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditEventActivity. this, AutocompleteActivity.class);
                startActivityForResult(intent, 100);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFrom.setEnabled(true);
                dateTo.setEnabled(true);
                location.setEnabled(true);
                time.setEnabled(true);
                minParticipants.setEnabled(true);
                maxParticipants.setEnabled(true);
                durability.setEnabled(true);
                description.setEnabled(true);
                tags.setEnabled(true);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String institutionInfo = sharedpreferences.getString("USER", null);
                InstitutionInfo ins = gson.fromJson(institutionInfo, InstitutionInfo.class);

                EditEventData e = new EditEventData(
                        eventName,
                        description.getText().toString(),
                        durability.getSelectedItem().toString(),
                        date2,
                        date1,
                        String.valueOf(lat),
                        String.valueOf(lon),
                        maxParticipants.getText().toString(),
                        minParticipants.getText().toString(),
                        EventMethods.getTags(tags),
                        time1
                );

                dateFrom.setEnabled(false);
                dateTo.setEnabled(false);
                location.setEnabled(false);
                time.setEnabled(false);
                minParticipants.setEnabled(false);
                maxParticipants.setEnabled(false);
                durability.setEnabled(false);
                description.setEnabled(false);
                tags.setEnabled(false);

                eventsRepository.getEventsService().editEvent(ins.getToken(), e).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            addToInstitution(ins.getToken(), e.getName());
                            Toast.makeText(getApplicationContext(), "Event Changed! Please reload 'My Events'", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Fail", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void addToInstitution(List<String> token, String eventId){
        AddEventData add = new AddEventData(
                eventId);

        eventsRepository.getEventsService().addEventToInstitution(token, add).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful())
                    ;
                else
                    Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String lat_lon = data.getStringExtra("lat_lon");
        String[] split = lat_lon.split(" ");
        lat = Double.parseDouble(split[0]);
        lon = Double.parseDouble(split[1]);

        String address = data.getStringExtra("address");
        location.setText(address);
    }

    private void updateLabelDateFrom() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        date1 = sdf.format(myCalendar.getTime());
        dateFrom.setText("From: " +date1);
    }

    private void updateLabelDateTo() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        date2 = sdf.format(myCalendar.getTime());
        dateTo.setText("To: " + date2);
    }

    private void setAttributes(){
        eventsRepository.getEventsService().getEvent(eventName).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful()){
                    EventData event = EventMethods.getEvent(r);

                    String myFormat = "dd/MM/yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                    date1 = event.getInitialDate();
                    date2 = event.getEndingDate();

                    name.setText("Edit " + eventName);
                    dateFrom.setText("Fr: " + event.getInitialDate());
                    dateTo.setText("To: " + event.getEndingDate());
                    time.setText(event.getTime());
                    minParticipants.setText(event.getMinParticipants());
                    maxParticipants.setText(event.getMaxParticipants());
                    description.setText(event.getDescription());

                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(EditEventActivity.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(event.getLat(), event.getLon(), 1);
                        String address = addresses.get(0).getAddressLine(0);
                        location.setText(address);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    List<Integer> t = event.getTags();

                    for(int i = 0; i < t.size(); i++) {
                        Chip chip = (Chip) tags.getChildAt(t.get(i));
                        tags.check(chip.getId());
                    }

                    switch(event.getDurability()){
                        case "Temporary":
                            durability.setSelection(0);
                                break;
                        case "Weekly":
                            durability.setSelection(1);
                                break;
                        case "Monthly":
                            durability.setSelection(2);
                                break;
                        case "Annual":
                            durability.setSelection(3);
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}