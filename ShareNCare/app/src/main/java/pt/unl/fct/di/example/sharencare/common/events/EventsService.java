package pt.unl.fct.di.example.sharencare.common.events;

import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.institution.main_menu.ui.events.RegisterEvent;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.events.AddEventData;
import pt.unl.fct.di.example.sharencare.user.settings.AbandonEventData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EventsService {

    @POST("/rest/event/registerEvent")
    Call<ResponseBody> registerEvent(@Header("Cookie") List<String> cookie, @Body RegisterEvent data);

    @POST("/rest/event/joinEvent")
    Call<ResponseBody> joinEventUser(@Header("Cookie") List<String> cookie, @Body AddEventData user);

    @POST("/rest/event/removeUserFromEvent")
    Call<ResponseBody> abandonEvent(@Header("Cookie") List<String> cookie, @Body AbandonEventData data);

    @POST("/rest/event/addEventInstitution")
    Call<ResponseBody> addEventToInstitution(@Header("Cookie") List<String> cookie, @Body AddEventData user);

    @GET("/rest/event/getAllEvents")
    Call<ResponseBody> getAllEvents(@Header("Cookie") List<String> cookie);

    @GET("/rest/event/listUserEvents")
    Call<ResponseBody> getUserEvents(@Header("Cookie") List<String> cookie);

    @GET("/rest/event/getEvent")
    Call<ResponseBody> getEvent(@Query("eventId") String eventId);

    @GET("/rest/event/filterEvents")
    Call<ResponseBody> filterEvents(@Query("filterString") String filterString);

    @GET("/rest/event/listEventPreferences")
    Call<ResponseBody> getEventPreferences(@Header("Cookie") List<String> cookie);

    @POST("/rest/event/getEventsByLocation")
    Call<ResponseBody> getEventsByCoordinates(@Body GetEventsByCoordinates data);


}

