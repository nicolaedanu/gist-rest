package com.payconiq.assessment.gists;

import com.payconiq.assessment.TestBase;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

public class GetGistTests extends TestBase {
    @Test
    public void getAllGistsIsOk(){
        auth().get(GISTS).then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void getGistByIdIsOk(){

    }

    @Test
    public void getGIstMultipleFileByIdIsOk(){

    }

    @Test
    public void getPublicGistsIsOk(){

    }

    @Test
    public void getGistByInvalidIdIsNotOk(){

    }

    @Test
    public void getPublicGistIdUsingAuthIsNotOk(){

    }

    @Test
    public void getGistWithIncorrectUriIsNotOk(){

    }

}
