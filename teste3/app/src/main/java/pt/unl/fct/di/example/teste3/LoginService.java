package pt.unl.fct.di.example.teste3;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {

    @POST("/rest/login/user")
    Call<LoginUser> loginUser(@Body LoginUser user);

}
