package pt.unl.fct.di.example.sharencare.common.login;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.institution.login.LoginInstitution;
import pt.unl.fct.di.example.sharencare.user.login.LoginUser;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginService {

    @POST("/rest/login/user")
    Call<ResponseBody> loginUser(@Body LoginUser user);

    @POST("/rest/login/institution")
    Call<ResponseBody> loginInstitution(@Body LoginInstitution institution);

}
