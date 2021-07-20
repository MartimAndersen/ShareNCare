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
import pt.unl.fct.di.example.sharencare.common.events.EventData;
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

                TrackInfo t = new TrackInfo(
                        track.get(0),
                        track.get(1),
                        Integer.parseInt(track.get(2)),
                        track.get(3),
                        track.get(4),
                        track.get(5),
                        track.get(6)
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

}
