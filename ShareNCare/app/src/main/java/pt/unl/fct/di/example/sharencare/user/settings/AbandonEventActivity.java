package pt.unl.fct.di.example.sharencare.user.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.common.events.EventData;
import pt.unl.fct.di.example.sharencare.common.events.EventMethods;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AbandonEventActivity extends AppCompatActivity {

    private ListView listView;
    private TextView text;
    private SharedPreferences sharedpreferences;
    private Gson gson;
    private String selectedEvent;
    private Repository eventsRepository;
    private CheckBox mBox;

    private String[] names;
    private String[] dates;
    private String[] locations;

    private TextView header;
    private Button footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abandon_event);
        gson = new Gson();
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        eventsRepository = eventsRepository.getInstance();

        selectedEvent = null;
        mBox = null;

        listView = findViewById(R.id.activity_abandon_events_listView);
        text = findViewById(R.id.activity_abandon_events_text);
        listView.setEmptyView(text);
        getUserEvents();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = view.findViewById(R.id.row_event_abandon_checkbox);
                TextView name = view.findViewById(R.id.row_event_abandon_name);

                String eventId = name.getText().toString();

                if(checkBox.isChecked()){
                    selectedEvent = null;
                    checkBox.setChecked(false);
                    mBox = null;
                } else {
                    selectedEvent = eventId;
                    if(mBox != null)
                        mBox.setChecked(false);
                    mBox = checkBox;
                    checkBox.setChecked(true);
                }
            }
        });
    }
    public void getUserEvents(){
        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        String e = sharedpreferences.getString("EVENTS", null);
        List<EventData> events;

        if(e != null) {
            Type listType = new TypeToken<ArrayList<EventData>>(){}.getType();
            events = gson.fromJson(e, listType);
            listEvents(events);
        } else {
            eventsRepository.getEventsService().getUserEvents(user.getToken()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                    if (r.isSuccessful()) {
                            List<EventData> events = new ArrayList<>();
                            events = EventMethods.getMultipleEvents(r);

                            SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                            String json = gson.toJson(events);
                            prefsEditor.putString("EVENTS", json);
                            prefsEditor.apply();

                            listEvents(events);
                    } else {
                        Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void listEvents(List<EventData> events){
        Geocoder geocoder;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        names = new String[events.size()];
        dates = new String[events.size()];
        locations = new String[events.size()];

        for (int i = 0; i < events.size(); i++) {
            names[i] = events.get(i).getName();
            dates[i] = getDate(events.get(i).getInitialDate(), events.get(i).getTime());
            try {
                locations[i] = geocoder.getFromLocation(events.get(i).getLat(), events.get(i).getLon(), 1).get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        AbandonEventActivity.MyAdapter myAdapter = new MyAdapter(getApplicationContext(), names, dates, locations);

        setHeaderAndFooter();
        listView.addHeaderView(header);
        listView.addFooterView(footer);
        listView.setAdapter(myAdapter);
    }

    private void setHeaderAndFooter(){
        String userInfo = sharedpreferences.getString("USER", null);
        UserInfo user = gson.fromJson(userInfo, UserInfo.class);

        header = new TextView(this);
        header.setText("Choose an Event to Abandon");
        header.setTextSize(20);
        header.setHeight(100);
        header.setGravity(Gravity.CENTER);

        footer = new Button(this);
        footer.setText("Abandon Event");
        footer.setGravity(Gravity.CENTER);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeaveEventData d = new LeaveEventData(
                        selectedEvent
                );
                eventsRepository.getEventsService().leaveEvent(user.getToken(), d).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                        if(r.isSuccessful()){
                            String e = sharedpreferences.getString("EVENTS", null);
                            List<EventData> events;

                            Type listType = new TypeToken<ArrayList<EventData>>() {}.getType();
                            events = gson.fromJson(e, listType);

                            for(EventData event : events)
                                if(event.getName().equals(selectedEvent))
                                    events.remove(event);

                            SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                            String json = gson.toJson(events);
                            prefsEditor.putString("EVENTS", json);
                            prefsEditor.apply();

                            startActivity(new Intent(AbandonEventActivity.this, SettingsActivity.class));

                        } else
                            Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "FAIL", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String getDate(String initialDate, String time){
        String date;
        SimpleDateFormat sdfStart = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
        SimpleDateFormat sdfEnd = new SimpleDateFormat("EEE, d MMM yyyy", Locale.UK);
        try {
            Date iDate = sdfStart.parse(initialDate);
            String iOutput = sdfEnd.format(iDate);;
            date = iOutput + " " + time;
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Pair<Double, Double> getLatLon(String coordinates){
        String[] c = coordinates.split(" ");
        Pair<Double, Double> latLon = new Pair<Double, Double>(new Double(c[0]), new Double(c[1]));
        return latLon;
    }

    private List<Integer> getTags(String tags){
        return gson.fromJson(tags, List.class);
    }

    private List<String> getMembers(String members){
        return gson.fromJson(members, List.class);
    }

    private List<Integer> getRating(String rating){
        return gson.fromJson(rating, List.class);
    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] names;
        String[] dates;
        String[] locations;

        MyAdapter(Context context, String[] names, String[] dates, String[] locations){
            super(context, R.layout.row_event_abandon, R.id.row_event_abandon_name, names);
            this.context = context;
            this.names = names;
            this.dates = dates;
            this.locations = locations;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.row_event_abandon, parent, false);

            TextView name = row.findViewById(R.id.row_event_abandon_name);
            TextView date = row.findViewById(R.id.row_event_abandon_date_time);
            TextView location = row.findViewById(R.id.row_event_abandon_location);

            name.setText(names[position]);
            date.setText(dates[position]);
            location.setText(locations[position]);

            return row;
        }
    }
}