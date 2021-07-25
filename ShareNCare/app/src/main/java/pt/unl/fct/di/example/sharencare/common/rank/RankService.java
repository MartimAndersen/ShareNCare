package pt.unl.fct.di.example.sharencare.common.rank;

import java.util.List;

import okhttp3.ResponseBody;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.find_tracks.LikeDislikeData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface RankService {
    @POST("/rest/ranking/commentLikeDislike")
    Call<ResponseBody> likeDislike(@Header("Cookie") List<String> cookie, @Body LikeDislikeData data);

    @GET("/rest/ranking/rankUsers")
    Call<ResponseBody> getTop10(@Header("Cookie") List<String> cookie);

}
