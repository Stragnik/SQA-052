import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import services.GitHubService;
import services.GorestService;
import utils.TestLogger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RetrofitDemoTests implements TestLogger {

    private Retrofit retrofitGit;
    private Retrofit retrofitGorest;
    private final GitHubService gitHubService;
    private final GorestService gorestService;
    private static final String GIT_HUB_URL = "https://api.github.com/";
    private static final String GOREST_URL = "https://gorest.co.in/";

    private final String issueTitle = String.format("issue %s", RandomStringUtils.randomAlphabetic(5));
    private final String issueDescription = "Description of new issue";


    public RetrofitDemoTests() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        this.retrofitGit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(GIT_HUB_URL)
                .build();

        this.retrofitGorest = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(GOREST_URL)
                .build();

        this.gitHubService = retrofitGit.create(GitHubService.class);
        this.gorestService = retrofitGorest.create(GorestService.class);
    }


    @Test
    void verificationHealthCheck() throws IOException {
        log("START: Verify GET zen");

        Response<String> response = gitHubService.getZen().execute();
        assertEquals(200, response.code(), "Статус код не равен 200 а равен: " + response.code());

        log("End: Verify GET zen");
    }


    @Test
    void verifyDefunktBodyTest() throws IOException {
        log("START: Verify GET defunkt");
        Response<List<Map<String, Object>>> response =
                gorestService.getPostsByUserId("BEARER_GOREST", "7439483").execute();

        assertNotNull(response.body(), "Response body is null");
        log("Response body: " + response.body());
        assertFalse(response.body().isEmpty(), "Response body is empty");

        log("End: Verify GET defunkt");
    }

    @Test
    @Disabled
    void verifyPostIssuesUrlParam() throws IOException {
        log("START: Verify POST issues");
        Response<Map<String, Object>> response =
                gorestService.postIssueUrl(
                                "BEARER_GOREST",
                                "7439483",
                                "test-title",
                                "test-body")
                        .execute();
        assertAll(
                () -> assertEquals(201, response.code(), "Статус код не равен 201 а равен: " + response.code()),
                () -> assertNotNull(response.body(), "Response body is null")
        );

    }

    //title=test-title&body=test-body

}