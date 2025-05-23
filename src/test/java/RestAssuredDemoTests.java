import io.restassured.response.Response;
import modal.github.Issue;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static java.rmi.server.LogStream.log;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RestAssuredDemoTests {

    private final String BASE_URL = "https://api.github.com";
    private final String GOREST_URL = "https://gorest.co.in/public/v2/";
    private final String issueTitle = String.format("issue %s", RandomStringUtils.randomAlphabetic(5));
    private final String issueDescription = "Description of new issue";


    /**
     * 01. Проверяем, что приходит 200 код в ответ на простой GET
     **/

    @Test
    @Tag("smoke")
    void verifyHealthcheckTest() {
        log("START: Verify GET zen");
        given()
                .baseUri(BASE_URL)
                .when()
                .log().all()
                .get("/zen")
                .then()
                .statusCode(200)
                .log().ifStatusCodeIsEqualTo(200);
        log("END: Verify GET zen");
    }

    /*
        02. Проверяем, что приходит непустое тело ответа на простой GET
    */
    @Test
    void verifyDefunktBodyTest() {
        Response response = given()
                .baseUri(BASE_URL)
                .when()
                .log().all()
                .get("/defunkt");
        response.prettyPrint();
        response.then()
                .log().all()
                .body(Matchers.not(Matchers.empty()));
    }

    /*
        03. Проверяем, что тело ответа содержит поле, равное значению
    */
    @Test
    void verifyIssuesContainTest() {
        Response response = given()
                .baseUri(BASE_URL)
                .when()
                .log().all()
                .get("/users/defunkt");
        response.prettyPrint();
        response.then()
                .log().all()
                .body("login", equalTo("defunkt"))
                .statusCode(200);

    }

    /*
        04. Проверяем, что тело ответа содержит поле после авторизации
    */
    @Test
    void verifyIssuesAuthorized() {

        Response response = given()
                .baseUri(BASE_URL)
                .header("Authorization",
                        "BEARER_GITHUB")
                .when()
                .log().all()
                .get("/users/Stragnik/repos");
        response.prettyPrint();
        response.then()
                .log().all()
                .body("name", Matchers.hasItem("Homework"));
    }

    /*
        05. Проверяем, что тело ответа содержит ошибку и 401 код
    */
    @Test
    void verifyIssuesNoUserAgent() {

        Response response = given()
                .baseUri(BASE_URL)
                .header("Accept", "application/xml")
                .when()
                .log().all()
                .get("/repos/ilyademchenco/rest/issues");
        response.prettyPrint();
        response.then()
                .log().all()
                .statusCode(415)
                .body("message", containsString("Must accept 'application/json'"));

    }

    /*
        06. Проверяем, что ишью публиковаться (тело запроса в строке)
    */

    @Test
    void verifyPostIssues() {
        Response response = given()
                .baseUri(BASE_URL)
                .header("Accept", "application/json")
                .header("Authorization", "BEARER_GITHUB")
                .body("{\n" +
                        "    \"title\":\"ibs-training 11\",\n" +
                        "    \"body\": \"Description of issue\"\n" +
                        "}")
                .when()
                .post("/repos/Stragnik/Homework/issues");
        response.prettyPrint();
        response.then()
                .log().all()
                .statusCode(201)
                .body("title", containsString("ibs-training 11"));
    }

/*
    07. Проверяем, что тело ответа содержит данные и 201 код
*/

    @Test
    void verifyPostIssuesUrlParam() {
        log("START: Verify POST issues");
        Response response = given()
                .baseUri(GOREST_URL)
                .header("Accept", "application/json")
                .header("Authorization", "BEARER_GOREST")
                .param("title", "test-title")
                .param("body", issueDescription)
                .when()
                .post("users/7439480/posts");
        response.prettyPrint();
        response.then()
                .log().all()
                .statusCode(201)
                .body("title", equalTo("test-title"));
        log("END: Verify POST issues");
    }

    /*
    08. Проверяем, что ишью публикуется (тело запроса в POJO)
     */

    @Test
    void verifyPostPojo() {
        Issue requestIssue = new Issue();
        requestIssue
                .setTitle(issueTitle)
                .setBody(issueDescription);

        log("START: Verify POST issues");
        Response response = given()
                .baseUri(BASE_URL)
                .header("Accept", "application/json")
                .header("Authorization", "BEARER_GITHUB")
                .body(requestIssue)
                .when()
                .post("/repos/Stragnik/Homework/issues");
        response.prettyPrint();

        Issue responseIssue = response.body().as(Issue.class);

        assertAll(
                () -> assertEquals(201, response.statusCode()),
                () -> assertEquals(issueTitle, responseIssue.getTitle(), "Issue title"),
                () -> assertEquals(issueDescription, responseIssue.getBody(), "Issue description"));
        log("END: Verify POST issues");
    }

    @ParameterizedTest(name = "Create issue with title: {0}")
    @ValueSource(strings = {"Bug", "Feature", "Docs"})
    void verifyPostMapParametrized1(String type) {
        String title = type + ": Test issue";
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("title", issueTitle);
        requestBody.put("body", issueDescription);

        log("START: Verify POST issues with title: " + issueTitle);
        Response response = given()
                .baseUri(BASE_URL)
                .header("Accept", "application/json")
                .header("Authorization", "BEARER_GITHUB")
                .body(requestBody)
                .when()
                .post("/repos/Stragnik/Homework/issues");
        response.prettyPrint();

        Map<String, Object> responseIssue = response.body().as(LinkedHashMap.class);

        assertAll(
                () -> assertEquals(201, response.statusCode()),
                () -> assertEquals(issueTitle, responseIssue.get("title"), "Issue title"),
                () -> assertEquals(issueDescription, responseIssue.get("body"), "Issue description")
        );
        log("END: Verify POST issues with title: " + issueTitle);
    }
}

