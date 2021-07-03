package pt.unl.fct.di.example.sharencare.user.map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MapService {

    @POST("/rest/map/registerTrack")
    Call<ResponseBody> registerTrack(@Body TrackData track);

}
