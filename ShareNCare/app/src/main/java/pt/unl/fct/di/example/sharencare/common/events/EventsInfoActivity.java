package pt.unl.fct.di.example.sharencare.common.events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.register.Repository;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.AddEventData;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.EventData;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.EventsFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.fonts.FontStyle;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventsInfoActivity extends AppCompatActivity {

    private TextView nameEvent;
    private ListView listView;
    private Button addEvent;
    private SharedPreferences sharedpreferences;
    private String[] texts;
    private String[] tagNames = {"Animals", "Environment", "Children", "Elderly", "Supplies", "Homeless"};
    private int[] images = {R.drawable.event_black, R.drawable.description_black, R.drawable.location_black, R.drawable.person_black};
    private int[] tagIcons = {R.drawable.animals_black, R.drawable.environment_black, R.drawable.children_black, R.drawable.elderly_black, R.drawable.supplies_black, R.drawable.homeless_black};
    private ChipGroup chipGroup;

    private Repository eventsRepository;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_info);
        View footer = getLayoutInflater().inflate(R.layout.footer, null);
        sharedpreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        eventsRepository = eventsRepository.getInstance();
        gson = new Gson();

        nameEvent = new TextView(this);
        customizeName();
        listView = findViewById(R.id.activity_events_info_list_view);

        Intent intent = getIntent();
        String type = intent.getStringExtra("type");

        if(!type.equals("info")) {
            addEvent = footer.findViewById(R.id.activity_events_info_add_event);
            addEvent.setVisibility(View.VISIBLE);

            addEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String eventName = sharedpreferences.getString("EVENT", null);

                    String userInfo = sharedpreferences.getString("USER", null);
                    UserInfo user = gson.fromJson(userInfo, UserInfo.class);

                    AddEventData add = new AddEventData(
                            user.getTokenId(),
                            eventName
                    );

                    eventsRepository.getEventsService().addEventToUser(add).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                            if (r.isSuccessful())
                                Toast.makeText(getApplicationContext(), "Event Added!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(), "CODE: " + r.code(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "Failure", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }

        getEvent(getIntent().getStringExtra("name_event"), footer);

    }

    class MyAdapter extends ArrayAdapter<String> {
        Context context;
        String[] texts;
        int[] images;

        MyAdapter(Context context, String[] texts, int[] images){
            super(context, R.layout.info_box, R.id.info_box_text, texts);
            this.context = context;
            this.texts = texts;
            this.images = images;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View info = layoutInflater.inflate(R.layout.info_box, parent, false);

            ImageView image = info.findViewById(R.id.info_box_image);
            TextView text = info.findViewById(R.id.info_box_text);

            image.setImageResource(images[position]);
            text.setText(texts[position]);

            return info;
        }
    }

    private void getEvent(String eventId, View footer){
        eventsRepository.getEventsService().getEvent(eventId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if (r.isSuccessful()) {
                    try {
                        List<LinkedTreeMap> list = gson.fromJson(r.body().string(), List.class);
                        List<String> event = new ArrayList<>();
                        texts = new String[4];

                        for (int j = 0; j < list.size(); j++) {
                            event.add(list.get(j).get("value").toString());
                        }

                        String eventName = event.get(7);

                        SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                        prefsEditor.putString("EVENT", eventName);
                        prefsEditor.apply();

                        nameEvent.setText(eventName);
                        texts[0] = getDate(event.get(4), event.get(2), event.get(3), event.get(9));
                        texts[1] = event.get(1);
                        texts[2] = getLocation(event.get(0));
                        texts[3] = getParticipants(event.get(6), event.get(5));
                        List<Double> tags = gson.fromJson(event.get(8), List.class);
                        chipGroup = footer.findViewById(R.id.activity_events_info_chip_group);
                        for(int i = 0; i < tags.size(); i++) {
                            setChip(tags.get(i).intValue());
                        }

                        EventsInfoActivity.MyAdapter myAdapter = new EventsInfoActivity.MyAdapter(getApplicationContext(), texts, images);
                        listView.addHeaderView(nameEvent);
                        listView.addFooterView(footer);
                        listView.setAdapter(myAdapter);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "CODE: "+r.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "NO", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void customizeName(){
        nameEvent.setBackgroundColor(Color.parseColor("#8BC34A"));
        nameEvent.setTextColor(Color.parseColor("#FFFFFF"));
        nameEvent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
        nameEvent.setGravity(Gravity.CENTER);
        nameEvent.setTypeface(null, Typeface.BOLD);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, (int) (height*0.2));
        nameEvent.setLayoutParams(params);
    }

    private String getDate(String initialDate, String endingDate, String time, String durability) {
        String date;
        SimpleDateFormat sdfStart = new SimpleDateFormat("dd/MM/yy", Locale.UK);
        SimpleDateFormat sdfEnd = new SimpleDateFormat("EEE, d MMM yyyy", Locale.UK);
        try {

            Date iDate = sdfStart.parse(initialDate);
            Date eDate = sdfStart.parse(endingDate);
            String iOutput = sdfEnd.format(iDate);
            String eOutput = sdfEnd.format(eDate);

            if (initialDate.equals(endingDate))
                date = iOutput + "\n" + time + "\n" + durability;
            else
                date = iOutput + " - " + eOutput + "\n" + time + "\n" + durability;
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getLocation(String coordinates){
        String[] c = coordinates.split(" ");
        Pair<Double, Double> latLon = new Pair<Double, Double>(new Double(c[0]), new Double(c[1]));
        try {
            Geocoder geocoder;
            geocoder = new Geocoder(this, Locale.getDefault());
            Address address = geocoder.getFromLocation(latLon.first, latLon.second, 1).get(0);
            String location = address.getThoroughfare() +
                    "\n" + address.getPostalCode() + " " + address.getLocality() +
                    "\n" + address.getAdminArea() + ", " + address.getCountryName();
            return location;
        } catch (IOException e) {
            e.printStackTrace();
        }
     return null;
    }

    private String getParticipants(String min, String max){
        String participants = "Min num of participants: " + min + "\n"
                + "Max num of participants: " + max;
        return participants;
    }

    private void setChip(int position){
        Chip chip = new Chip(this);
        chip.setChipIcon(getResources().getDrawable(tagIcons[position-1], getTheme()));
        chip.setText(tagNames[position-1]);
        chipGroup.addView(chip);

    }

    private Pair<Double, Double> getLatLon(String coordinates){
        String[] c = coordinates.split(" ");
        Pair<Double, Double> latLon = new Pair<Double, Double>(new Double(c[0]), new Double(c[1]));
        return latLon;
    }

    private List<Integer> getTags(String tags){
        return gson.fromJson(tags, List.class);
    }


}