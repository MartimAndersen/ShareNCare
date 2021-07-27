package pt.unl.fct.di.example.sharencare.common.tracks;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.ReviewData;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackMarkers;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackDangerZones;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackMedia;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackNotes;
import retrofit2.Response;

public class TrackMethods {

    private static Gson gson = new Gson();

    public static List<TrackInfo> getMultipleTracks(Response<ResponseBody> r) {
        List<TrackInfo> tracks = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(r.body().string());
            for (int i = 0; i < array.length(); i++) {
                List<LinkedTreeMap> list = null;

                list = gson.fromJson(array.get(i).toString(), List.class);

                List<String> track = new ArrayList<>();

                for (int j = 0; j < list.size(); j++)
                    track.add(list.get(j).get("value").toString());

                String rating = track.get(0);
                if(rating.equals(""))
                    rating = String.valueOf(0);

                TrackInfo t = new TrackInfo(
                        Float.parseFloat(rating),
                        track.get(1),
                        track.get(2),
                        Integer.parseInt(track.get(3)),
                        track.get(4),
                        getMarkers(track.get(5)),
                        track.get(6),
                        track.get(7),
                        track.get(8),
                        getZones(track.get(9)),
                        getMedia(track.get(10)),
                        getNotes(track.get(11)),
                        track.get(12),
                        track.get(13)
                );
                tracks.add(t);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tracks;
    }

    public static List<ReviewData> getMultipleComments(Response<ResponseBody> r){
        List<ReviewData> reviews = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(r.body().string());
            Type t = new TypeToken<List<ReviewData>>(){}.getType();
            reviews = gson.fromJson(array.toString(), t);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return reviews;
    }

    private static List<TrackMarkers> getMarkers(String markers){
        Type t = new TypeToken<List<String>>(){}.getType();
        Type t1 = new TypeToken<TrackMarkers>(){}.getType();
        List<String> strings = gson.fromJson(markers, t);
        List<TrackMarkers> markerList = new ArrayList<>();

        if(markers.equals("[]"))
            return markerList;

        for(String s : strings)
            markerList.add(gson.fromJson(s, t1));
        return markerList;
    }

    private static List<TrackDangerZones> getZones(String zones){
        Type t = new TypeToken<List<String>>(){}.getType();
        Type t1 = new TypeToken<TrackDangerZones>(){}.getType();
        List<TrackDangerZones> zoneList = new ArrayList<>();

        if(zones.equals("[]"))
            return zoneList;

        List<String> strings = gson.fromJson(zones, t);

        for(String s : strings)
            zoneList.add(gson.fromJson(s, t1));
        return zoneList;
    }

    private static List<TrackMedia> getMedia(String media){
        Type t = new TypeToken<List<String>>(){}.getType();
        Type t1 = new TypeToken<TrackMedia>(){}.getType();
        List<TrackMedia> mediaList = new ArrayList<>();

        if(media.equals("[]"))
            return mediaList;

        List<String> strings = gson.fromJson(media, t);

        for(String s : strings)
            mediaList.add(gson.fromJson(s, t1));
        return mediaList;
    }

    private static List<TrackNotes> getNotes(String notes){
        Type t = new TypeToken<List<String>>(){}.getType();
        Type t1 = new TypeToken<TrackNotes>(){}.getType();
        List<TrackNotes> noteList = new ArrayList<>();

        if(notes.equals("[]"))
            return noteList;

        List<String> strings = gson.fromJson(notes, t);

        for(String s : strings)
            noteList.add(gson.fromJson(s, t1));
        return noteList;
    }

}
