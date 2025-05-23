package services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;
import java.util.Map;


public interface GorestService {

    @POST("/public/v2/users/{user}/posts")
    Call<Map<String, Object>> postIssueUrl(
            @retrofit2.http.Header("Authorization") String authToken,
            @retrofit2.http.Path("user") String user,
            @retrofit2.http.Query("title") String title,
            @retrofit2.http.Query("body") String body);


    @GET("/public/v2/users/{user}/posts")
    Call<List<Map<String, Object>>> getPostsByUserId(
            @retrofit2.http.Header("Authorization") String authToken,
            @retrofit2.http.Path("user") String user);
}
