package com.payconiq.assessment.gists;

import com.payconiq.assessment.TestBase;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public class UpdateGistTest extends TestBase {
    private static final String DOC_EDIT_GIST = "#edit-a-gist";

    @Test
    public void updatePublicGistsIsOk(){
        // gist is created
        Response gist = restCreateGistWithBody(FILE_GIST_ONE)
                .then().body("description",is("Created gistOne via API"),
                        "files['gistOne.txt'].content",is(GIST_CONTENT))
                .extract().response();
        String gistId = gist.path("id");
        // update gist - different file name and no content
        auth()
                .when().body(getFileFromResources(FILE_GIST_ONE_UPDATE)).pathParam("gistId", gistId).patch(GISTS_ID)
                .then().statusCode(HttpStatus.SC_OK)
                .body("owner.login", Matchers.is(OWNER),
                        "files['updateGistOne.txt'].content", nullValue(),
                        "description", Matchers.is("Created gistOne via Rest Assured"));
    }

    @Test
    public void updateSecretGistsIsOk(){
        // gist is created
        Response gist = restCreateGistWithBody(FILE_GIST_TWO)
                .then().body("description",is("Created gistTwo via API"),
                        "files['gistTwo.txt'].content",is(GIST_CONTENT))
                .extract().response();
        String gistId = gist.path("id");

        auth()
                .when().body(getFileFromResources(FILE_GIST_TWO_UPDATE)).pathParam("gistId", gistId).patch(GISTS_ID)
                .then().statusCode(HttpStatus.SC_OK).log().all()
                .body("owner.login", Matchers.is(OWNER),
                        "files['updatedgistTwo.txt'].content", Matchers.is(GIST_CONTENT),
                        "description", Matchers.is("Created gistTwo via Rest Assured"));
    }

    @Test
    public void updateOtherPublicAccountGistsIsNotOk(){
        // get first Public Gist
        String firstPublicId = given().get(GISTS).then().body("[0].owner.login", not(OWNER))
                .extract().response().path("[0].id");
        // update Other account gist
        auth()
                .when().body(getFileFromResources(FILE_GIST_TWO_UPDATE)).pathParam("gistId", firstPublicId).patch(GISTS_ID)
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, CoreMatchers.is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString(DOC_EDIT_GIST));
    }

    @Test
    public void updateGistWithoutAuthIsNotOk(){
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_TWO).path("id");
        // remove gist without auth
        given()
                .when().body(getFileFromResources(FILE_GIST_TWO_UPDATE)).pathParam("gistId", gistId).patch(GISTS_ID)
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, CoreMatchers.is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString(DOC_EDIT_GIST));
    }
    @Test
    public void updateGistInvalidPayloadIsNotOk2(){
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_TWO).path("id");
        // update gist using invalid payload
        given()
                .when().body("{empty}").pathParam("gistId", gistId).patch(GISTS_ID)
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, CoreMatchers.is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString(DOC_EDIT_GIST));
    }

    @Test
    public void updateGistWithIncorrectUriIsNotOk(){
        // gist is created
        String gistId = restCreateGistWithBody(FILE_GIST_TWO).path("id");
        // remove gist without auth
        given()
                .when().body(getFileFromResources(FILE_GIST_TWO_UPDATE)).pathParam("gistId", gistId)
                .patch("/gst/{gistId}")
                .then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body(PATH_MESSAGE, CoreMatchers.is(ERROR_NOT_FOUND),
                        PATH_DOC_URL, containsString("developer.github.com"));
    }

}
