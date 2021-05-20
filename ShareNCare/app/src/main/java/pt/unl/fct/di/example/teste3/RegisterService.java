package pt.unl.fct.di.example.teste3;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterService {

    @POST("/rest/register/user")
    Call<ResponseBody> registerUser(@Body RegisterUser user);

 /*   @FormUrlEncoded
    @POST("comments")
    Call<RegisterUser> registerUser(@Field("username") String username, @Field("password") String password, @Field("confirmation") String confirmation, @Field("email") String email, @Field("firstName") String firstName, @Field("lastName") String lastName, @Field("location") String location, @Field("mobile") String mobile, @Field("postal") String postal, @Field("secondAddress") String secondAddress, @Field("landLine") String landLine, @Field("profileType") String profileType);

    @FormUrlEncoded
    @POST("comments")
    Call<RegisterUser> registerUser(@FieldMap Map<String, String> fields);*/
}
