package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.events;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import pt.unl.fct.di.example.sharencare.institution.main_menu.ui.directions.EventData;

public interface EventsService {

    @POST("/rest/event/registerEvent")
    Call<ResponseBody> registerEvent(@Body EventData data);

    @GET("/rest/event/getAllEvents")
    Call<ResponseBody> getAllEvents(@Query("tokenId") String tokenId);

    @GET("/rest/event/listUserEvents")
    Call<ResponseBody> getUserEvents(@Query("tokenId") String tokenId);

    @POST("/rest/event/addEvent")
    Call<ResponseBody> addEventToUser(@Body AddEventData user);

}
