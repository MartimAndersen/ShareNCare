package pt.unl.fct.di.example.sharencare.common.events;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.AddEventData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import pt.unl.fct.di.example.sharencare.institution.main_menu.ui.new_event.EventData;

public interface EventsService {

    @POST("/rest/event/registerEventAndroid")
    Call<ResponseBody> registerEvent(@Query("tokenId") String tokenId, @Body EventData data);

    @GET("/rest/event/getAllEvents")
    Call<ResponseBody> getAllEvents(@Query("tokenId") String tokenId);

    @GET("/rest/event/listUserEvents")
    Call<ResponseBody> getUserEvents(@Query("tokenId") String tokenId);

    @POST("/rest/event/addEvent")
    Call<ResponseBody> addEventToUser(@Body AddEventData user);

    @POST("/rest/event/addEventInstitution")
    Call<ResponseBody> addEventToInstitution(@Body AddEventData user);

    @GET("/rest/event/getEvent")
    Call<ResponseBody> getEvent(@Query("eventId") String eventId);


}

