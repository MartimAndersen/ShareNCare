package pt.unl.fct.di.example.sharencare.common.register;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pt.unl.fct.di.example.sharencare.common.login.LoginService;
import pt.unl.fct.di.example.sharencare.common.events.EventsService;
import pt.unl.fct.di.example.sharencare.common.profile.ProfileService;
import pt.unl.fct.di.example.sharencare.user.map.MapService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Repository {
    private static Repository instance;

    private LoginService loginService;
    private RegisterService registerService;
    private MapService mapService;
    private ProfileService profileService;
    private EventsService eventsService;

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public Repository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apdc-dot-capable-sphinx-312419.appspot.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        loginService = retrofit.create(LoginService.class);
        registerService = retrofit.create(RegisterService.class);
        mapService = retrofit.create(MapService.class);
        profileService = retrofit.create(ProfileService.class);
        eventsService = retrofit.create(EventsService.class);

    }

    public LoginService getLoginService() {
        return loginService;
    }

    public RegisterService getRegisterService(){
        return registerService;
    }

    public MapService getMapService(){return mapService;}

    public ProfileService getProfileService(){return profileService;}

    public EventsService getEventsService(){return eventsService;}
}
