package services;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GitHubService {

    @GET("/zen")
    Call<String> getZen();

    @GET("/repos/Strgnik")
    Call<String> getStragnik();
}
