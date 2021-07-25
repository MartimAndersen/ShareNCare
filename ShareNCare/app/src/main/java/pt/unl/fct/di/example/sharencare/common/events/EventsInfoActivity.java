package pt.unl.fct.di.example.sharencare.common.events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.R;
import pt.unl.fct.di.example.sharencare.common.Repository;
import pt.unl.fct.di.example.sharencare.user.login.UserInfo;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.AddEventData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
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
    private int[] images = {R.drawable.event_black, R.drawable.description_black, R.drawable.location_black, R.drawable.person_black, R.drawable.heart_black};
    private int[] tagIcons = {R.drawable.animals_black, R.drawable.environment_black, R.drawable.children_black, R.drawable.elderly_black, R.drawable.supplies_black, R.drawable.homeless_black};
    private ChipGroup chipGroup;
    private EventData e;

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
                    String e = sharedpreferences.getString("EVENT", null);
                    Type listType = new TypeToken<EventData>(){}.getType();
                    EventData event = gson.fromJson(e, listType);

                    String userInfo = sharedpreferences.getString("USER", null);
                    UserInfo user = gson.fromJson(userInfo, UserInfo.class);

                    AddEventData add = new AddEventData(
                            event.getName()
                    );

                    eventsRepository.getEventsService().joinEventUser(user.getToken(), add).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                            if (r.isSuccessful()) {
                                String e = sharedpreferences.getString("EVENTS", null);
                                List<EventData> events = new ArrayList<EventData>();

                                if(e != null) {
                                    Type listType = new TypeToken<ArrayList<EventData>>() {
                                    }.getType();
                                    events = gson.fromJson(e, listType);
                                }
                                events.add(event);

                                SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                                String json = gson.toJson(events);
                                prefsEditor.putString("EVENTS", json);
                                prefsEditor.apply();

                                Toast.makeText(getApplicationContext(), "Event Added!", Toast.LENGTH_SHORT).show();
                            } else
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
            TextView textView = info.findViewById(R.id.info_box_text);

            image.setImageResource(images[position]);
            textView.setText(texts[position]);
            textView.setMaxLines(5);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(textView.getLineCount() > 5)
                        textView.setMaxLines(texts[position].length());
                    else
                        textView.setMaxLines(5);
                }
            });

            return info;
        }
    }

    private void getEvent(String eventId, View footer){
        eventsRepository.getEventsService().getEvent(eventId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> r) {
                if (r.isSuccessful()) {
                    EventData e = EventMethods.getEvent(r);
                    texts = new String[5];

                    String eventName = e.getName();
                    String institutionName = e.getInstitutionName();

                    SharedPreferences.Editor prefsEditor = sharedpreferences.edit();
                    prefsEditor.putString("EVENT", gson.toJson(e));
                    prefsEditor.apply();

                    header(eventName, institutionName);
                    texts[0] = getDate(e.getInitialDate(), e.getEndingDate(), e.getTime(), e.getDurability());
                    texts[1] = e.getDescription();
                    texts[2] = getLocation(e.getLat(), e.getLon());
                    texts[3] = getParticipants(e.getMinParticipants(), e.getMaxParticipants());
                    texts[4] = institutionName;
                    List<Integer> tags = e.getTags();
                    chipGroup = footer.findViewById(R.id.activity_events_info_chip_group);
                    for(int i = 0; i < tags.size(); i++) {
                        setChip(tags.get(i));
                    }

                    MyAdapter myAdapter = new MyAdapter(getApplicationContext(), texts, images);
                    listView.addHeaderView(nameEvent);
                    listView.addFooterView(footer);
                    listView.setAdapter(myAdapter);

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
        SimpleDateFormat sdfStart = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
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

    private String getLocation(Double lat, Double lon){
        Pair<Double, Double> latLon = new Pair<Double, Double>(lat, lon);
        try {
            Geocoder geocoder;
            geocoder = new Geocoder(this, Locale.getDefault());
            Address address = geocoder.getFromLocation(latLon.first, latLon.second, 1).get(0);

            String thoroughfare = address.getThoroughfare();
            String location = "location";
            if(thoroughfare != null) {
                location = address.getThoroughfare() +
                        "\n" + address.getPostalCode() + " " + address.getLocality() +
                        "\n" + address.getAdminArea() + ", " + address.getCountryName();
            }
            else{
                location = address.getPostalCode() + " " + address.getLocality() +
                        "\n" + address.getAdminArea() + ", " + address.getCountryName();
            }
            return location;
        } catch (IOException e) {
            e.printStackTrace();
        }
     return null;
    }

    private void header(String eventName, String institutionName){
        String ss1 = "\n" + "by " + institutionName;
        SpannableString ss2 = new SpannableString(ss1);
        ss2.setSpan(new AbsoluteSizeSpan(50), 0, ss1.length(), 0);
        nameEvent.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        nameEvent.setPadding(0,40,0,40);
        nameEvent.setText(TextUtils.concat(eventName, ss2));
    }

    private String getParticipants(String min, String max){
        String participants = "Min num of participants: " + min + "\n"
                + "Max num of participants: " + max;
        return participants;
    }

    private void setChip(int position){
        Chip chip = new Chip(this);
        chip.setChipIcon(getResources().getDrawable(tagIcons[position], getTheme()));
        chip.setText(tagNames[position]);
        chipGroup.addView(chip);
    }
}