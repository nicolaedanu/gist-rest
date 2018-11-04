package com.payconiq.assessment;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

import static io.restassured.RestAssured.given;

public abstract class TestBase {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String TOKEN = System.getProperty("token");
    private static final String VND_GITHUB_V3 = "application/vnd.github.v3+json";
    private static final String BASE_URI = "https://api.github.com";

    protected static final String OWNER = "payconiqQA";
    protected static final String GIST_CONTENT = "Having a POJO would have been way much easier";
    protected static final String PATH_MESSAGE = "message";
    protected static final String PATH_DOC_URL = "documentation_url";
    protected static final String ERROR_NOT_FOUND = "Not Found";

    protected static final String FILE_GIST_ONE = "gistFiles/createGistOne.json";
    protected static final String FILE_GIST_ONE_UPDATE = "gistFiles/updateGistOne.json";
    protected static final String FILE_GIST_TWO = "gistFiles/createGistTwo.json";
    protected static final String FILE_GIST_TWO_UPDATE = "gistFiles/updateGistTwo.json";
    protected static final String FILE_GIST_MULTIPLE = "gistFiles/createGistMultipleFiles.json";
    protected static final String FILE_EMPTY = "gistFiles/emptyFile.json";


    protected static final String GISTS = "/gists";
    protected static final String GISTS_ID = "/gists/{gistId}";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeMethod
    public void beforeTestCase(Method m) {
        logger.info("Running test: " + m.getName());
    }

    protected RequestSpecification auth() {
        return given().auth().preemptive().oauth2(TOKEN).accept(VND_GITHUB_V3);
    }

    protected File getFileFromResources(String fileName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL url = classLoader.getResource(fileName);
            return new File(url.getFile());
        } catch (Exception e) {
            logger.error("File not found, or incorrect file name", e);
            throw e;
        }
    }

    protected Response restCreateGistWithBody(String fileName) {
        return auth().body(getFileFromResources(fileName))
                .post(GISTS).then().statusCode(HttpStatus.SC_CREATED)
                .extract().response();
    }

    protected Response restGetGistWithId(String gistId) {
        return auth().pathParam("gistId", gistId)
                .get(GISTS_ID).then().extract().response();
    }
}
