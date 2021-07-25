package pt.unl.fct.di.example.sharencare.common.profile;

import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.institution.main_menu.ui.profile.ProfileInstitution;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.profile.ProfileUser;
import pt.unl.fct.di.example.sharencare.user.register.ProfileDataTags;
import pt.unl.fct.di.example.sharencare.user.settings.ChangeEmailData;
import pt.unl.fct.di.example.sharencare.user.settings.ChangePasswordData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ProfileService {

    @POST("/rest/loggedIn/changeAttributes")
    Call<ResponseBody> changeProfile(@Header("Cookie") List<String> cookie, @Body ProfileUser user);

    @POST("/rest/loggedIn/changeAttributesTags")
    Call<ResponseBody> changeProfileTags(@Body ProfileDataTags tags);

    @POST("/rest/loggedInInstitution/changeAttributes")
    Call<ResponseBody> changeProfileInstitution(@Header("Cookie") List<String> cookie, @Body ProfileInstitution institution);

    @GET("/rest/loggedIn/getPic")
    Call<ResponseBody> getProfilePic(@Header("Cookie") List<String> cookie);

    @POST("/rest/loggedIn/changeEmail")
    Call<ResponseBody> changeEmail(@Header("Cookie") List<String> cookie, @Body ChangeEmailData data);

    @POST("/rest/loggedInInstitution/changeEmailInstitution")
    Call<ResponseBody> changeEmailInstitution(@Header("Cookie") List<String> cookie, @Body ChangeEmailData data);

    @POST("/rest/loggedIn/changePassword")
    Call<ResponseBody> changePassword(@Header("Cookie") List<String> cookie, @Body ChangePasswordData data);

    @POST("/rest/loggedInInstitution/changePasswordCompany")
    Call<ResponseBody> changePasswordInstitution(@Header("Cookie") List<String> cookie, @Body ChangePasswordData data);

    @POST("/rest/loggedIn/logout")
    Call<ResponseBody> userLogout(@Header("Cookie") List<String> cookie);

    @GET("/rest/loggedIn/getUser")
    Call<ResponseBody> getUser(@Header("Cookie") List<String> cookie, @Query("username") String username);

}
