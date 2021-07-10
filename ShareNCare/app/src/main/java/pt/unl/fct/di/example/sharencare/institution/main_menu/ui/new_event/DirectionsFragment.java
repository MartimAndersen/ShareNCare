package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.new_event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.register.Repository;
import pt.unl.fct.di.example.sharencare.databinding.FragmentDirectionsBinding;
import pt.unl.fct.di.example.sharencare.institution.login.InstitutionInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.AddEventData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionsFragment extends Fragment {

    private SharedPreferences sharedpreferences;
    private Gson gson;

    private FragmentDirectionsBinding binding;
    private EditText name, dateFrom, dateTo, time, location, minParticipants, maxParticipants, description;
    private Spinner durability;
    private ChipGroup tags;
    private Button register;
    private Repository eventsRepository;
    private Double lat, lon;
    private String amPm, date1, date2, time1;

    final Calendar myCalendar = Calendar.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_directions, container, false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedpreferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        gson = new Gson();
        eventsRepository = eventsRepository.getInstance();

        name = getView().findViewById(R.id.fragment_create_events_name);
        dateFrom = getView().findViewById(R.id.fragment_create_events_date_from);
        dateTo = getView().findViewById(R.id.fragment_create_events_date_to);
        location = getView().findViewById(R.id.fragment_create_events_location);
        time = getView().findViewById(R.id.fragment_create_events_time);

        minParticipants = getView().findViewById(R.id.fragment_create_events_min_participants);
        maxParticipants = getView().findViewById(R.id.fragments_create_events_max_participants);
        durability = getView().findViewById(R.id.fragment_create_events_durability);
        description = getView().findViewById(R.id.fragment_create_events_description);
        tags = getView().findViewById(R.id.fragment_create_events_tags);
        register = getView().findViewById(R.id.fragment_create_events_register);

        Places.initialize(getContext(), "AIzaSyBfzRwOxqBJx1Lb3hzeHOF4ildEbCN4GJk");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.events_durability_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durability.setAdapter(adapter);

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

                new DatePickerDialog(getContext(), dateDialog, myCalendar
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

                new DatePickerDialog(getContext(), dateDialog, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = myCalendar.get(Calendar.HOUR);
                int minute = myCalendar.get(Calendar.MINUTE);

                TimePickerDialog timeDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
                       /* if (hourOfDay >= 12)
                            amPm = "PM";
                        else
                            amPm = "AM";*/
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
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(getActivity());
                startActivityForResult(intent, 100);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventData e = new EventData(
                        name.getText().toString(),
                        description.getText().toString(),
                        minParticipants.getText().toString(),
                        maxParticipants.getText().toString(),
                        time1,
                        durability.getSelectedItem().toString(),
                        date1,
                        date2,
                        tags.getCheckedChipIds(),
                        lat,
                        lon
                        );

                String institutionInfo = sharedpreferences.getString("USER", null);
                InstitutionInfo ins = gson.fromJson(institutionInfo, InstitutionInfo.class);

                eventsRepository.getEventsService().registerEvent(ins.getTokenId(), e).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            addToInstitution(ins.getTokenId(), e.getName());
                            Toast.makeText(getContext(), "Event " + name + " Registered!", Toast.LENGTH_SHORT);
                        } else
                            Toast.makeText(getContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT);
                    }
                });
            }
        });
    }

    private void addToInstitution(String tokenId, String eventId){
        AddEventData add = new AddEventData(
                tokenId,
                eventId);

        eventsRepository.getEventsService().addEventToInstitution(add).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if(r.isSuccessful())
                    Toast.makeText(getContext(), "Event Added!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == getActivity().RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            location.setText(place.getAddress());
            lat = place.getLatLng().latitude;
            lon = place.getLatLng().longitude;
        } else {
            Toast.makeText(getContext(), "FAILED", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLabelDateFrom() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        date1 = sdf.format(myCalendar.getTime());
        dateFrom.setText("From: " +date1);
    }

    private void updateLabelDateTo() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        date2 = sdf.format(myCalendar.getTime());
        dateTo.setText("To: " + date2);
    }

}