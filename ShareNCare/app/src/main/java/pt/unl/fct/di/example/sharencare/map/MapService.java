package pt.unl.fct.di.example.sharencare.map;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.map.TrackData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MapService {

    @POST("/rest/map/registerTrack")
    Call<ResponseBody> registerTrack(@Body TrackData track);

}
