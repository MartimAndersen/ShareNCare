package pt.unl.fct.di.example.sharencare.common.rank;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import okhttp3.ResponseBody;

public class RankMethods {

    public static Gson gson = new Gson();

    public static List<PointsData> getTopUsers(Response<ResponseBody> r){
        List<PointsData> users = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(r.body().string());
            Type t = new TypeToken<List<PointsData>>(){}.getType();

            users = gson.fromJson(array.toString(), t);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

}
