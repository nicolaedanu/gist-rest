package com.payconiq.assessment.gists;

import com.payconiq.assessment.TestBase;
import org.apache.http.HttpStatus;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;

public class GetGistTests extends TestBase {
    private static final String DOC_GET_SINGLE_GIST = "#get-a-single-gist";

    @Test
    public void getAllGistsIsOk() {
        auth()
                .when().get(GISTS)
                .then().statusCode(HttpStatus.SC_OK)
                .body("$.size()", greaterThanOrEqualTo(0))
                .body(matchesJsonSchemaInClasspath("gistJson/gistsSchema.json"));
    }

    @Test
    public void getGistByIdIsOk() {
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_TWO).path("id");
        // get gist and validate response against schema
        auth()
                .when().pathParam("gistId", gistId).get(GISTS_ID)
                .then().statusCode(HttpStatus.SC_OK)
                .body("owner.login", is(OWNER),
                        "files['gistTwo.txt'].content", is(GIST_CONTENT),
                        "description", is("Created gistTwo via API"),
                        "public", is(false))
                .body(matchesJsonSchemaInClasspath("gistJson/gistSchema.json"));
    }

    @Test
    public void getGistMultipleFileByIdIsOk() {
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_MULTIPLE).path("id");
        // get gist and validate response against schema
        auth()
                .when().pathParam("gistId", gistId).get(GISTS_ID)
                .then().statusCode(HttpStatus.SC_OK)
                .body("owner.login", is(OWNER),
                        "files.size()", is(3),
                        "description", is("Created multiple gist files via API"),
                        "public", is(false))
                .body(matchesJsonSchemaInClasspath("gistJson/gistSchema.json"));
    }

    @Test
    public void getPublicGistsIsOk() {
        given()
                .when().get(GISTS)
                .then().statusCode(HttpStatus.SC_OK)
                .body("$.size()", greaterThanOrEqualTo(0));
//                .body(matchesJsonSchemaInClasspath("gistsSchema.json"));
    }

    @Test
    public void getGistByInvalidIdIsNotOk() {
        String gistId = "invalidId";
        // get gist and validate response against schema
        auth()
                .when().pathParam("gistId", gistId).get(GISTS_ID)
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, CoreMatchers.is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString(DOC_GET_SINGLE_GIST));

    }

    @Test
    public void getPublicGistIdUsingAuthIsOk() {
        // get first Public Gist
        String firstPublicId = given().get(GISTS).then().body("[0].owner.login", not(OWNER))
                .extract().response().path("[0].id");
        auth()
                .when().pathParam("gistId", firstPublicId).get(GISTS_ID)
                .then().statusCode(HttpStatus.SC_OK)
                .body(matchesJsonSchemaInClasspath("gistJson/gistSchema.json"));
    }

    @Test
    public void getGistWithIncorrectUriIsNotOk() {
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_TWO).path("id");
        // get gist and validate response against schema
        auth()
                .when().pathParam("gistId", gistId).get("/gst/{gistId}")
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, CoreMatchers.is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString("developer.github.com"));
    }

}
