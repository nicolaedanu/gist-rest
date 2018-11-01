package com.payconiq.assessment;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import static io.restassured.RestAssured.given;

public class TestBase {
    private final static Logger logger = Logger.getLogger(TestBase.class.getName());
    private static final String TOKEN = "{useYourGithubAccessToken}";
    private static final String VND_GITHUB_V3 = "application/vnd.github.v3+json";
    private static final String BASE_URI = "https://api.github.com";

    public static final String GISTS = "/gists";
    public static final String GISTS_ID = "/gists/{gistId}";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeMethod
    public void beforeTestCase(Method m) {
        logger.info("Running test: " + m.getName());
    }

    public RequestSpecification auth() {
        return given().auth().preemptive().oauth2(TOKEN).accept(VND_GITHUB_V3);
    }

    public File getFileFromResources(String fileName){
        return new File(getClass().getClassLoader().getResource(fileName).getFile());
    }

    public Response restCreateGistWithBody(String fileName) {
        return  auth().body(getFileFromResources(fileName))
                .post(GISTS).then().statusCode(HttpStatus.SC_CREATED)
                .extract().response();
    }
}
