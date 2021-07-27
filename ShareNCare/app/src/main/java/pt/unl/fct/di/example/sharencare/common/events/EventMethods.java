package pt.unl.fct.di.example.sharencare.common.events;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.unl.fct.di.example.sharencare.R;
import retrofit2.Response;
import okhttp3.ResponseBody;



public class EventMethods {

    private static Gson gson = new Gson();

    public static List<Integer> getTags(ChipGroup tags){
        List<Integer> t = new ArrayList<>();
        for(int i = 0; i < tags.getChildCount(); i++) {
            Chip chip = (Chip) tags.getChildAt(i);
            if (chip.isChecked())
                t.add(i);
        }
        return t;
    }

    public static EventData getEvent(Response<ResponseBody> r){
        List<LinkedTreeMap> list;
        EventData e = null;
        try {
            list = gson.fromJson(r.body().string(), List.class);

        List<String> event = new ArrayList<>();

        for (int j = 0; j < list.size(); j++) {
            event.add(list.get(j).get("value").toString());
        }

        e = new EventData(
                event.get(1),
                event.get(2),
                event.get(3),
                event.get(4),
                event.get(5),
                event.get(6),
                getLatLon(event.get(0)).first,
                getLatLon(event.get(0)).second,
                event.get(7),
                getMembers(event.get(8)),
                event.get(9),
                event.get(10),
                event.get(11),
                getRating(event.get(12)),
                getTags(event.get(13)),
                event.get(14)
        );

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return e;
    }

    public static List<EventData> getMultipleEvents(Response<ResponseBody> r) {
        List<EventData> events = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(r.body().string());
            for (int i = 0; i < array.length(); i++) {
                List<LinkedTreeMap> list = null;

                list = gson.fromJson(array.get(i).toString(), List.class);

                List<String> event = new ArrayList<>();

                for (int j = 0; j < list.size(); j++)
                    event.add(list.get(j).get("value").toString());

                EventData e = new EventData(
                        event.get(1),
                        event.get(2),
                        event.get(3),
                        event.get(4),
                        event.get(5),
                        event.get(6),
                        getLatLon(event.get(0)).first,
                        getLatLon(event.get(0)).second,
                        event.get(7),
                        getMembers(event.get(8)),
                        event.get(9),
                        event.get(10),
                        event.get(11),
                        getRating(event.get(12)),
                        getTags(event.get(13)),
                        event.get(14)
                );

                events.add(e);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return events;
    }

    private static Pair<Double, Double> getLatLon(String coordinates) {
        String[] c = coordinates.split(" ");
        Pair<Double, Double> latLon = new Pair<Double, Double>(new Double(c[0]), new Double(c[1]));
        return latLon;
    }

    public static List<Integer> getTags(String tags) {
       List<Double> db = gson.fromJson(tags, List.class);
       List<Integer> intTags = new ArrayList<Integer>();
       for(Double d : db)
           intTags.add(d.intValue());
        return intTags;
    }

    private static List<Integer> getRating(String rating) {
        return gson.fromJson(rating, List.class);
    }

    private static List<String> getMembers(String members) {
        return gson.fromJson(members, List.class);
    }



}
