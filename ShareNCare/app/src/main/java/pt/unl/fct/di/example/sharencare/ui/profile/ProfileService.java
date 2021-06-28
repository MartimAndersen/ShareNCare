package pt.unl.fct.di.example.sharencare.ui.profile;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.register.RegisterUser;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProfileService {

    @POST("/rest/loggedIn/changeAttributes")
    Call<ResponseBody> changeProfile(@Body ProfileUser user);
}
