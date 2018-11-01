package com.payconiq.assessment.gists;

import com.payconiq.assessment.TestBase;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CreateGistTest extends TestBase {
    private static final String GIST_CONTENT = "Having a POJO would have been way much easier";
    private static final String DOC_CREATE_GIST = "#create-a-gist";

    @Test
    public void createPublicGistWithOneFileIsOk(){
        auth()
            .body(getFileFromResources("createGistOne.json"))
            .post(GISTS).then().statusCode(HttpStatus.SC_CREATED)
            .body("owner.login",is(OWNER),
                    "description",is("Created gistOne via API"),
                    "public",is(true),
                    "files.size()",is(1),
                    "files['gistOne.txt'].content",is(GIST_CONTENT));
    }

    @Test
    public void createSecretGistWithOneFileIsOk(){
        auth()
                .body(getFileFromResources("createGistTwo.json"))
                .post(GISTS).then().statusCode(HttpStatus.SC_CREATED)
                .body("owner.login",is(OWNER),
                        "description",is("Created gistTwo via API"),
                        "public",is(false),
                        "files.size()",is(1),
                        "files['gistTwo.txt'].content",is(GIST_CONTENT));
    }

    @Test
    public void createGistWithMultipleFilesIsOk(){
        auth()
                .body(getFileFromResources("createGistMultipleFiles.json"))
                .post(GISTS).then().statusCode(HttpStatus.SC_CREATED)
                .body("owner.login",is(OWNER),
                        "description",is("Created multiple gist files via API"),
                        "public",is(true),
                        "files.size()",both(greaterThanOrEqualTo(0)).and(lessThan(Integer.MAX_VALUE)),
                        "files['README.md'].content",is(GIST_CONTENT),
                        "files['README.md'].language",is("Markdown"),
                        "files['README.txt'].language",is("Text"),
                        "files['README.yaml'].language",is("YAML"));
    }

    @Test
    public void createGistInvalidPayloadIsNotOk(){
        auth()
                .body("invalidPayload")
                .post(GISTS).then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .header("X-GitHub-Media-Type",containsString("github.v3"))
                .body("message",is("Problems parsing JSON"),
                        "documentation_url",containsString(DOC_CREATE_GIST));
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
                        "documentation_url",containsString(DOC_CREATE_GIST));
    }
}
