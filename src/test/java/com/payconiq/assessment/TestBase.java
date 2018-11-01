package com.payconiq.assessment;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.io.File;
import java.lang.reflect.Method;

import static io.restassured.RestAssured.given;

@Listeners({com.payconiq.assessment.reports.CustomisedReports.class})
public class TestBase {
    private static final String TOKEN = "{useYourGithubAccessToken}";
    private static final String VND_GITHUB_V3 = "application/vnd.github.v3+json";
    private static final String BASE_URI = "https://api.github.com";

    public static final String OWNER = "payconiqQA";
    public static final String GIST_CONTENT = "Having a POJO would have been way much easier";
    public static final String PATH_MESSAGE = "message";
    public static final String PATH_DOC_URL = "documentation_url";
    public static final String ERROR_NOT_FOUND = "Not Found";

    public static final String FILE_GIST_ONE = "gistFiles/createGistOne.json";
    public static final String FILE_GIST_ONE_UPDATE = "gistFiles/updateGistOne.json";
    public static final String FILE_GIST_TWO = "gistFiles/createGistTwo.json";
    public static final String FILE_GIST_TWO_UPDATE = "gistFiles/updateGistTwo.json";
    public static final String FILE_GIST_MULTIPLE = "gistFiles/createGistMultipleFiles.json";


    public static final String GISTS = "/gists";
    public static final String GISTS_ID = "/gists/{gistId}";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeMethod
    public void beforeTestCase(Method m) {
        // we could have used a Logger library. But in this scenario we are interested to see only the test name
        // no other info like package or classname. We also dont care about the log level.
        System.out.println("Running test: " + m.getName());
    }

    public RequestSpecification auth() {
        return given().auth().preemptive().oauth2(TOKEN).accept(VND_GITHUB_V3);
    }

    public File getFileFromResources(String fileName) {
        return new File(getClass().getClassLoader().getResource(fileName).getFile());
    }

    public Response restCreateGistWithBody(String fileName) {
        return auth().body(getFileFromResources(fileName))
                .post(GISTS).then().statusCode(HttpStatus.SC_CREATED)
                .extract().response();
    }

    public Response restGetGistWithId(String gistId) {
        return auth().pathParam("gistId", gistId)
                .get(GISTS_ID).then().extract().response();
    }
}
