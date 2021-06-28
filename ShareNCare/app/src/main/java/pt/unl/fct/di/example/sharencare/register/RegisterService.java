package pt.unl.fct.di.example.sharencare.register;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterService {

    @POST("/rest/register/user")
    Call<ResponseBody> registerUser(@Body RegisterUser user);

}
