package pt.unl.fct.di.example.sharencare.common.profile;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.institution.main_menu.ui.profile.ProfileInstitution;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.profile.ProfileUser;
import pt.unl.fct.di.example.sharencare.user.register.ProfileDataTags;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProfileService {

    @POST("/rest/loggedIn/changeAttributes")
    Call<ResponseBody> changeProfile(@Body ProfileUser user);

    @POST("/rest/loggedIn/changeAttributesTags")
    Call<ResponseBody> changeProfileTags(@Body ProfileDataTags tags);

    @POST("/rest/loggedInInstitution/changeAttributes")
    Call<ResponseBody> changeProfileInstitution(@Body ProfileInstitution institution);

    @GET("/rest/loggedIn/getPic")
    Call<ResponseBody> getProfilePic(@Query("tokenId") String tokenId);

}
