package pt.unl.fct.di.example.sharencare.common.tracks;

import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.institution.main_menu.ui.events.RegisterEvent;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackMedia;
import pt.unl.fct.di.example.sharencare.user.map.TrackData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface TracksService {

    @POST("/rest/map/registerTrack")
    Call<ResponseBody> registerTrack(@Header("Cookie") List<String> cookie, @Body TrackData data);

    @POST("/rest/map/addMedia")
    Call<ResponseBody> addMediaToTrack(@Header("Cookie") List<String> cookie, @Body List<TrackMedia> data);

    @GET("/rest/map/listUserTrack")
    Call<ResponseBody> getUserTrack(@Header("Cookie") List<String> cookie);

    @GET("/rest/map/listAllTrack")
    Call<ResponseBody> getAllTracks(@Header("Cookie") List<String> cookie);


}
