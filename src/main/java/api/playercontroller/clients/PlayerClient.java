package api.playercontroller.clients;

import api.playercontroller.annotations.Bug;
import api.playercontroller.models.requestDto.PlayerDeleteRequestDto;
import api.playercontroller.models.requestDto.PlayerGetByPlayerIdRequestDto;
import api.playercontroller.models.requestDto.PlayerUpdateRequestDto;
import io.restassured.response.Response;

import java.util.Map;

public class PlayerClient extends BaseAPIClient {


    //TODO Documentation swagger endpoint names do not correspond to their real behavior(action).
    // For example: documentation endpoint POST (/player/get/ getPlayerByPlayerId)
    // don't allow real post request instead it required real GET request
    @Bug("bug_7")
    public Response get(PlayerGetByPlayerIdRequestDto requestDto) {
        return prepareRequest()
                .when()
                .body(requestDto)
                .post(String.format("%s/player/get", baseUrl))// помилка в свагері не post а get має бути
                .then()
                .log()
                .all()
                .extract()
                .response();

    }

    //TODO Documentation swagger endpoint names do not correspond to their real behavior(action).
    // For example: documentation endpoint GET (/player/create/{editor} createPlayer)
    // don't allow real GET request instead it required real POST request
    @Bug("bug_8")
    public Response post(Map<String, String> queryParams, String pathParamValue) {
        return prepareRequest()
                .when()
                .queryParams(queryParams)
                .pathParam("editor", pathParamValue)
                .get(String.format("%s/player/create/{editor}", baseUrl))
                .then()
                .log()
                .all()
                .extract()
                .response();
    }

    public Response patch(String firstPathValue, String secondPathValue, PlayerUpdateRequestDto requestDto) {
        return prepareRequest()
                .when()
                .pathParam("editor", firstPathValue)
                .pathParam("id", secondPathValue)
                .body(requestDto)
                .patch(String.format("%s/player/update/{editor}/{id}", baseUrl))
                .then()
                .log()
                .all()
                .extract()
                .response();
    }

    public Response delete(String pathValue, PlayerDeleteRequestDto requestDto) {
        return prepareRequest()
                .when()
                .pathParam("editor", pathValue)
                .body(requestDto)
                .delete(String.format("%s/player/delete/{editor}", baseUrl))
                .then()
                .log()
                .all()
                .extract()
                .response();
    }

    public Response getAll() {
        return prepareRequest()
                .when()
                .get(String.format("%s/player/get/all", baseUrl))
                .then()
                .log()
                .all()
                .extract()
                .response();
    }
}
