package com.payconiq.assessment.gists;

import com.payconiq.assessment.TestBase;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

public class CreateGistTest extends TestBase {
    @Test
    public void createPublicGistWithOneFileIsOk(){
        auth()
            .body(getFileFromResources("createGistOne.json"))
            .post(GISTS).then().statusCode(HttpStatus.SC_CREATED)
            .body("owner.login",is("payconiqQA"));
    }

    @Test
    public void createSecretGistWithOneFileIsOk(){
        auth()
                .body(getFileFromResources("createGistTwo.json"))
                .post(GISTS).then().statusCode(HttpStatus.SC_CREATED)
                .body("owner.login",is("payconiqQA"));
    }

    @Test
    public void createGistWithMultipleFilesIsOk(){
        auth()
                .body(getFileFromResources("createGistMultipleFiles.json"))
                .post(GISTS).then().statusCode(HttpStatus.SC_CREATED)
                .body("owner.login",is("payconiqQA"));
    }

    @Test
    public void createGistInvalidPayloadIsNotOk(){
        auth()
                .body("invalidPayload")
                .post(GISTS).then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("message",is("Problems parsing JSON"),
                        "documentation_url",containsString("#create-a-gist"));
    }
    @Test
    public void createGistWithIncorrectUriIsNotOk(){
        auth()
                .body(getFileFromResources("createGistOne.json"))
                .post("/gsts").then().statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message",is("Not Found"),
                        "documentation_url",containsString("developer.github.com"));
    }

    @Test
    public void createGistNoAuthIsNotOk(){
        given()
                .body(getFileFromResources("createGistOne.json"))
                .post(GISTS).then().statusCode(HttpStatus.SC_UNAUTHORIZED)
                .body("message",is("Requires authentication"),
                        "documentation_url",containsString("#create-a-gist"));
    }
}
