package pt.unl.fct.di.example.teste3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class Repository {
    private static Repository instance;

    private LoginService loginService;
    private RegisterService registerService;


    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public Repository() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apdc-dot-capable-sphinx-312419.appspot.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        loginService = retrofit.create(LoginService.class);
        registerService = retrofit.create(RegisterService.class);
    }

    public LoginService getLoginService() {
        return loginService;
    }

    public RegisterService getRegisterService(){
        return registerService;
    }
}
