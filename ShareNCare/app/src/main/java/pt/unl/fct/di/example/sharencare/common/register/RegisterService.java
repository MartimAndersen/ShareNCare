package pt.unl.fct.di.example.sharencare.common.register;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.institution.register.RegisterInstitution;
import pt.unl.fct.di.example.sharencare.user.register.RegisterUser;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegisterService {

    @POST("/rest/register/user")
    Call<ResponseBody> registerUser(@Body RegisterUser user);

    @POST("/rest/register/institution")
    Call<ResponseBody> registerInstitution(@Body RegisterInstitution user);
}
