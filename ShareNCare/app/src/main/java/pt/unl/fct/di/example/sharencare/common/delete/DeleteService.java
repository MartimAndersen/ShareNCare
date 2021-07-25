package pt.unl.fct.di.example.sharencare.common.delete;

import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.user.register.RegisterUser;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DeleteService {

    @POST("/rest/delete/user")
    Call<ResponseBody> deleteUser(@Header("Cookie") List<String> cookie, @Query("username") String username);

    @POST("/rest/delete/institution")
    Call<ResponseBody> deleteInstitution(@Header("Cookie") List<String> cookie, @Query("nif") String nif);
}
