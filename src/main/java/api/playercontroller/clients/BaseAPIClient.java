package api.playercontroller.clients;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static api.playercontroller.utils.EnvProperties.*;
import static io.restassured.RestAssured.given;

public class BaseAPIClient {
    protected String baseUrl;
    protected ContentType contentType;

    public BaseAPIClient() {
        baseUrl = getBaseURL();
        contentType = ContentType.JSON;
    }

    protected RequestSpecification prepareRequest() {
        return given()
                .baseUri(baseUrl)
                .accept(contentType)
                .contentType(contentType)
                .log()
                .all();
    }
}
