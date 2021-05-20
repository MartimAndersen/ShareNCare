package pt.unl.fct.di.example.sharencare.login;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {

    @POST("/rest/login/user")
    Call<ResponseBody> loginUser(@Body LoginUser user);

}
