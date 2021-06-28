package pt.unl.fct.di.example.sharencare.ui.events;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.TokenData;
import pt.unl.fct.di.example.sharencare.ui.profile.ProfileUser;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventsService {

    @GET("/rest/event/getAllEvents/{tokenId}")
    Call<ResponseBody> getAllEvents(@Path("tokenId") String tokenId);

    @GET("/rest/event/listUserEvents")
    Call<ResponseBody> getUserEvents(@Body ProfileUser user);

    @POST("/rest/event/addEvent")
    Call<ResponseBody> addEventToUser(@Body AddEventData user);

}
