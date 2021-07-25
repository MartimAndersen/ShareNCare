package pt.unl.fct.di.example.sharencare.user.main_menu.ui.events;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.events.EventData;
import pt.unl.fct.di.example.sharencare.common.events.EventMethods;
import pt.unl.fct.di.example.sharencare.common.events.EventsInfoActivity;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EventsFragment extends Fragment {

    private EventsViewModel mViewModel;
    private SharedPreferences sharedpreferences;
    private Repository eventsRepository;
    private Gson gson = new Gson();

    private ListView listView;
    private TextView text;
    private String[] names, dates, hours, locations;

    private Calendar myCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventsViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        sharedpreferences = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        eventsRepository = eventsRepository.getInstance();

        listView = getView().findViewById(R.id.fragment_events_list_view);
        text = getView().findViewById(R.id.fragment_events_text);
        listView.setEmptyView(text);
        getUserEvents();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView nameText = view.findViewById(R.id.row_name_label);
                String name = nameText.getText().toString();

                Intent i = new Intent(getActivity(), EventsInfoActivity.class);
                i.putExtra("name_event", name);
                i.putExtra("type", "info");
                startActivity(i);
            }
        });
    }

    class MyAdapter extends ArrayAdapter<String>{
        Context context;
        String[] names;
        String[] dates;
        String[] hours;
        String[] locations;

        MyAdapter(Context context, String[] names, String[] dates, String[] hours, String[] locations){
            super(context, R.layout.row, R.id.row_name_label, names);
            this.context = context;
            this.names = names;
            this.dates = dates;
            this.hours = hours;
            this.locations = locations;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View row = layoutInflater.inflate(R.layout.row, parent, false);

            TextView name = row.findViewById(R.id.row_name_label);
            TextView date = row.findViewById(R.id.row_date_label);
            TextView hour = row.findViewById(R.id.row_hour_label);
            TextView location = row.findViewById(R.id.row_location_label);

            String[] split = dates[position].split(" - ");

            name.setText(names[position]);
            date.setText(split[0]);
            hour.setText(hours[position]);
            location.setText(locations[position]);

            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
            String cDate = sdf.format(myCalendar.getTime());
            try {
                Date currentDate = sdf.parse(cDate);
                Date eventDate = sdf.parse(split[1]);
                if(currentDate.after(eventDate)){
                    name.setBackground(getResources().getDrawable(R.drawable.rounded_top_corner_grey));
                    date.setBackground(getResources().getDrawable(R.color.greyscale_green));
                    hour.setBackground(getResources().getDrawable(R.color.greyscale_green));
                    location.setBackground(getResources().getDrawable(R.drawable.rounded_bottom_corner_grey));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return row;
        }
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
                        Toast.makeText(getActivity(), "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getActivity(), "NO", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void listEvents(List<EventData> events){
        Geocoder geocoder;
        geocoder = new Geocoder(getContext(), Locale.getDefault());

        names = new String[events.size()];
        dates = new String[events.size()];
        hours = new String[events.size()];
        locations = new String[events.size()];

        for (int i = 0; i < events.size(); i++) {
            names[i] = events.get(i).getName();
            dates[i] = events.get(i).getInitialDate() + " - " + events.get(i).getEndingDate();
            hours[i] = events.get(i).getTime();
            try {
                locations[i] = geocoder.getFromLocation(events.get(i).getLat(), events.get(i).getLon(), 1).get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MyAdapter myAdapter = new MyAdapter(getContext(), names, dates, hours, locations);
        listView.setAdapter(myAdapter);
    }

}