package com.payconiq.assessment.gists;

import com.payconiq.assessment.TestBase;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

public class DeleteGistTest extends TestBase {
    private static final String DOC_DELETE_GIST = "#delete-a-gist";

    @Test
    public void deleteSecretGistIsOk() {
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_TWO).path("id");
        restGetGistWithId(gistId).then().statusCode(HttpStatus.SC_OK);
        // gist is deleted
        auth()
                .when().pathParam("gistId", gistId).delete(GISTS_ID)
                .then().statusCode(HttpStatus.SC_NO_CONTENT);
        // gist is not found
        restGetGistWithId(gistId).then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void deletePublicGistMultipleFilesIsOk() {
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_MULTIPLE).path("id");
        restGetGistWithId(gistId).then().statusCode(HttpStatus.SC_OK);
        // gist is deleted
        auth()
                .when().pathParam("gistId", gistId).delete(GISTS_ID)
                .then().statusCode(HttpStatus.SC_NO_CONTENT);
        // gist is not found
        restGetGistWithId(gistId).then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void deleteInvalidGistIsNotOk() {
        String gistId = "invalidId";
        auth()
                .when().pathParam("gistId", gistId).delete(GISTS_ID)
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString(DOC_DELETE_GIST));
    }

    @Test
    public void deleteGistWithoutAuthIsNotOk() {
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_MULTIPLE).path("id");
        restGetGistWithId(gistId).then().statusCode(HttpStatus.SC_OK);
        // gist is not deleted
        given()
                .when().pathParam("gistId", gistId).delete(GISTS_ID)
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString(DOC_DELETE_GIST));
    }

    @Test
    public void deleteOtherPublicAccountGistIsNotOk() {
        // get first Public Gist
        String firstPublicId = given().get(GISTS).then().body("[0].owner.login", not(OWNER))
                .extract().response().path("[0].id");
        // try to delete Public Gist
        auth()
                .when().pathParam("gistId", firstPublicId).delete(GISTS_ID)
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString(DOC_DELETE_GIST));
    }

    @Test
    public void deleteGistWithIncorectUriIsNotOk() {
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_TWO).path("id");
        restGetGistWithId(gistId).then().statusCode(HttpStatus.SC_OK);
        // gist deletion fails
        auth()
                .when().pathParam("gistId", gistId).delete("/gst/{gistId}")
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString("developer.github.com"));
        // gist is still present
        restGetGistWithId(gistId).then().statusCode(HttpStatus.SC_OK);
    }
}
