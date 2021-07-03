package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.profile;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.user.register.ProfileDataTags;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ProfileService {

    @POST("/rest/loggedIn/changeAttributes")
    Call<ResponseBody> changeProfile(@Body ProfileUser user);



}
