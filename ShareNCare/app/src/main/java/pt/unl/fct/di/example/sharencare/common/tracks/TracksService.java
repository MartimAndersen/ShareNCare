package pt.unl.fct.di.example.sharencare.common.tracks;

import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.find_tracks.LikeDislikeData;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.FinishedTrack;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.GetMediaPic;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.ReviewData;
import pt.unl.fct.di.example.sharencare.user.map.TrackData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TracksService {

    @POST("/rest/map/registerTrack")
    Call<ResponseBody> registerTrack(@Header("Cookie") List<String> cookie, @Body TrackData data);

    @POST("/rest/map/finishedTrack")
    Call<ResponseBody> finishedTrack(@Header("Cookie") List<String> cookie, @Body FinishedTrack data);

    @POST("/rest/map/comment")
    Call<ResponseBody> comment(@Header("Cookie") List<String> cookie, @Body ReviewData data);

    @GET("/rest/map/listUserTrack")
    Call<ResponseBody> getUserTrack(@Header("Cookie") List<String> cookie);

    @GET("/rest/map/listAllTrack")
    Call<ResponseBody> getAllTracks(@Header("Cookie") List<String> cookie);

    @GET("rest/map/getAllComments")
    Call<ResponseBody> getAllComments(@Header("Cookie") List<String> cookie, @Query("title") String title);

    @POST("/rest/map/getMediaPic")
    Call<ResponseBody> getPic(@Header("Cookie") List<String> cookie, @Body GetMediaPic mp);

    @POST("/rest/map/addTrackToUser")
    Call<ResponseBody> addTrackToUser(@Header("Cookie") List<String> cookie, @Query("trackId") String trackId);



}
