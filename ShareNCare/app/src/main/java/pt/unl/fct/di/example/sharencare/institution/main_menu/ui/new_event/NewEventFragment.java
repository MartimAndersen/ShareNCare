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
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.CommonMethods;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventMethods;
import pt.unl.fct.di.example.sharencare.databinding.FragmentNewEventBinding;
import pt.unl.fct.di.example.sharencare.institution.login.InstitutionInfo;
import pt.unl.fct.di.example.sharencare.institution.main_menu.ui.events.EventData;
import pt.unl.fct.di.example.sharencare.institution.settings.AutocompleteActivity;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.AddEventData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewEventFragment extends Fragment {

    private SharedPreferences sharedpreferences;
    private Gson gson;

    private enum tag {animals, environment, children, elderly, supplies, homeless}

    private FragmentNewEventBinding binding;
    private EditText name, dateFrom, dateTo, time, location, minParticipants, maxParticipants, description;
    private Spinner durability;
    private ChipGroup tags;
    private Button register;
    private Repository eventsRepository;
    private Double lat, lon;
    private String date1, date2, time1;

    final Calendar myCalendar = Calendar.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_event, container, false);
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

        CommonMethods.setTags(getContext(), tags);

        Places.initialize(getContext(),  getResources().getString(R.string.google_maps_key));

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
                Intent intent = new Intent(getActivity(), AutocompleteActivity.class);
                startActivityForResult(intent, 100);
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lat == null || lon == null){
                    Toast.makeText(getContext(), "Please insert location!", Toast.LENGTH_SHORT);
                } else {
                    String institutionInfo = sharedpreferences.getString("USER", null);
                    InstitutionInfo ins = gson.fromJson(institutionInfo, InstitutionInfo.class);

                    EventData e = new EventData(
                            description.getText().toString(),
                            durability.getSelectedItem().toString(),
                            date2,
                            date1,
                            lat,
                            lon,
                            maxParticipants.getText().toString(),
                            new ArrayList<>(),
                            minParticipants.getText().toString(),
                            name.getText().toString(),
                            EventMethods.getTags(tags),
                            time1
                    );

                    eventsRepository.getEventsService().registerEvent(ins.getToken(), e).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                            if (r.isSuccessful()) {
                                addToInstitution(ins.getToken(), e.getName());
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
        dateFrom.setText("Fr: " +date1);
    }

    private void updateLabelDateTo() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
        date2 = sdf.format(myCalendar.getTime());
        dateTo.setText("To: " + date2);
    }

}