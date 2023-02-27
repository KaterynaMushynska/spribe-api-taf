package api.playercontroller;

import api.playercontroller.annotations.Bug;
import api.playercontroller.clients.PlayerClient;
import api.playercontroller.models.requestDto.PlayerDeleteRequestDto;
import api.playercontroller.models.requestDto.PlayerGetByPlayerIdRequestDto;
import api.playercontroller.models.requestDto.PlayerUpdateRequestDto;
import api.playercontroller.models.responseDto.PlayerCreateResponseDto;
import api.playercontroller.models.responseDto.PlayerGetAllResponseDto;
import api.playercontroller.models.responseDto.PlayerGetByPlayerIdResponseDto;
import api.playercontroller.models.responseDto.PlayerItem;
import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;


public class PlayerControllerTest {
    private PlayerClient client;
    private SoftAssert softAssert;

    @BeforeMethod
    public void init() {
        client = new PlayerClient();
        softAssert = new SoftAssert();
    }

    @DataProvider(name = "inputValidAge")
    public Object[][] inputValidAge() {
        return new Object[][]{{"17"}, {"30"}, {"60"}};
    }

    @Test(dataProvider = "inputValidAge")
    public void verifyThatSupervisorCanCreateAUserWithValidAge(String inputAge) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("age", inputAge);
        queryParams.put("gender", "female");
        queryParams.put("login", "userTestPlay");
        queryParams.put("password", "testPlayer1");
        queryParams.put("role", "admin");
        queryParams.put("screenName", "testPlayer1");

        Response response = client.post(queryParams, "supervisor");
        softAssert.assertEquals(response.getStatusCode(), 200, "Response status code should be 200");

        PlayerCreateResponseDto playerResponse = response.as(PlayerCreateResponseDto.class);
        int playerId = playerResponse.getId();

        PlayerGetByPlayerIdRequestDto requestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(playerId)
                .build();
        Response getResponse = client.get(requestDto);
        softAssert.assertEquals(getResponse.getStatusCode(), 200, "Response status code should be 200");
        PlayerGetByPlayerIdResponseDto responseDto = getResponse.as(PlayerGetByPlayerIdResponseDto.class);
        softAssert.assertEquals(responseDto.getId(), playerId, "Response id should be equal to expected");
        softAssert.assertEquals(responseDto.getAge(), Integer.parseInt(inputAge), "Response age should be equal to expected");
        softAssert.assertAll();
    }

    @DataProvider(name = "inputInvalidAge")
    public Object[][] inputAge() {
        return new Object[][]{{"16"}, {"61"}, {"70"}};
    }

    @Test(dataProvider = "inputInvalidAge")
    public void verifyThatSupervisorCanNotCreateAUserWithInvalidAge(String inputAge) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("age", inputAge);
        queryParams.put("gender", "female");
        queryParams.put("login", "testSuper");
        queryParams.put("password", "testSuper1");
        queryParams.put("role", "user");
        queryParams.put("screenName", "testSuper");

        Response response = client.post(queryParams, "supervisor");
        assertEquals(response.getStatusCode(), 400, "The user should not be created with invalid age");

    }

    @DataProvider(name = "inputValidPassword")
    public Object[][] inputValidPassword() {
        return new Object[][]{{"testAd1"}, {"testAdmin1"}, {"testPlayerAd123"}};
    }

    @Test(dataProvider = "inputValidPassword")
    public void verifyThatAdminCanCreateAUserWithValidPassword(String inputPassword) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("age", "20");
        queryParams.put("gender", "male");
        queryParams.put("login", "admin");
        queryParams.put("password", inputPassword);
        queryParams.put("role", "admin");
        queryParams.put("screenName", "testAdmin");
        String pathParam = "admin";

        Response response = client.post(queryParams, pathParam);
        softAssert.assertEquals(response.getStatusCode(), 200, "The user was successfully created");

        PlayerCreateResponseDto playerResponse = response.as(PlayerCreateResponseDto.class);
        int playerId = playerResponse.getId();

        PlayerGetByPlayerIdRequestDto requestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(playerId)
                .build();
        Response getResponse = client.get(requestDto);

        PlayerGetByPlayerIdResponseDto responseDto = getResponse.as(PlayerGetByPlayerIdResponseDto.class);

        softAssert.assertEquals(getResponse.getStatusCode(), 200, "Response status code should be 200");
        softAssert.assertEquals(responseDto.getId(), playerId, "Response id should be equal to expected");
        softAssert.assertEquals(responseDto.getPassword(), inputPassword, "Response password should be equal to expected");

        softAssert.assertAll();
    }

    @DataProvider(name = "inputInvalidPassword")
    public Object[][] inputInvalidPassword() {
        return new Object[][]{{"test"}, {"124565"}, {"testAdm))*l14"}, {"testPlayerAdmin12356"}};
    }

    // Bug:
    // The user was created with an invalid password.
    // Expected status code 400
    // Actual response status code was 200
    //TODO enabled when bug will be fixed
    @Bug("bug_1")
    @Test(dataProvider = "inputInvalidPassword", enabled = false)
    public void verifyThatAdminCanNotCreateAUserWithInvalidPassword(String inputPassword) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("age", "20");
        queryParams.put("gender", "male");
        queryParams.put("login", "admin");
        queryParams.put("password", inputPassword);
        queryParams.put("role", "admin");
        queryParams.put("screenName", "testAdmin");
        String pathParam = "admin";

        Response response = client.post(queryParams, pathParam);
        assertEquals(response.getStatusCode(), 400, "Response status code should be 400");

    }

    // Bug:
    // The screenName field is not unique for each user.
    // We can create users with the same screenName.
    // TODO enabled when bug will be fixed
    @Bug("bug_2")
    @Test(enabled = false)
    public void verifyThatScreenNameFieldIsUniqueForEachUser() {
        Response response = client.getAll();
        PlayerGetAllResponseDto responseGetAllDto = response.as(PlayerGetAllResponseDto.class);
        List<String> screenNames = responseGetAllDto.getPlayers()
                .stream()
                .map(PlayerItem::getScreenName)
                .collect(Collectors.toList());
        String screenName = "Player";

        softAssert.assertFalse(screenNames.stream().anyMatch(s -> s.equals(screenName)), "The list of screenNames should not contain " + screenName);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("age", "25");
        queryParams.put("gender", "female");
        queryParams.put("login", "adminLogin");
        queryParams.put("password", "Player124");
        queryParams.put("role", "admin");
        queryParams.put("screenName", screenName);
        String pathParam = "admin";

        Response userResponse = client.post(queryParams, pathParam);

        PlayerCreateResponseDto playerCreateResponseDto = userResponse.as(PlayerCreateResponseDto.class);
        softAssert.assertEquals(userResponse.getStatusCode(), 200, "The user should be successfully created");

        PlayerGetByPlayerIdRequestDto playerGetByPlayerIdRequestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(playerCreateResponseDto.getId())
                .build();
        Response getResponse = client.get(playerGetByPlayerIdRequestDto);
        PlayerGetByPlayerIdResponseDto responseDto = getResponse.as(PlayerGetByPlayerIdResponseDto.class);
        softAssert.assertEquals(getResponse.getStatusCode(), 200, "The user should be successfully created");
        softAssert.assertEquals(responseDto.getScreenName(), screenName, "Response screenName should be equal to expected");

        Map<String, String> queryParamsForSecondUser = new HashMap<>();
        queryParams.put("age", "27");
        queryParams.put("gender", "male");
        queryParams.put("login", "admPlLog");
        queryParams.put("password", "AdPassword12");
        queryParams.put("role", "admin");
        queryParams.put("screenName", screenName);
        String pathParamForSecondUser = "admin";

        Response newUserResponse = client.post(queryParamsForSecondUser, pathParamForSecondUser);
        softAssert.assertEquals(newUserResponse.getStatusCode(), 400, "The user with this screenName should be already created");

        Response getAllResponse = client.getAll();
        PlayerGetAllResponseDto playerGetAllResponseDto = getAllResponse.as(PlayerGetAllResponseDto.class);
        List<String> listScreenNames = playerGetAllResponseDto.getPlayers()
                .stream()
                .map(PlayerItem::getScreenName)
                .collect(Collectors.toList());

        softAssert.assertEquals(listScreenNames.stream()
                .filter(name -> name.equals(screenName))
                .collect(Collectors.toList())
                .size(), 1, "The screenName field should be unique");

        PlayerDeleteRequestDto deleteRequestDto = PlayerDeleteRequestDto.builder()
                .playerId(playerCreateResponseDto.getId())
                .build();
        client.delete(pathParam, deleteRequestDto);

        softAssert.assertAll();
    }

    @DataProvider(name = "inputInvalidRole")
    public Object[][] inputInvalidRole() {
        return new Object[][]{{"supervisor"}, {"sup"}, {"ad"}};
    }

    @Test(dataProvider = "inputInvalidRole")
    public void verifyThatUserCanNotBeCreatedWithInvalidRole(String role) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("age", "35");
        queryParams.put("gender", "male");
        queryParams.put("login", "testRole");
        queryParams.put("password", "testRole12");
        queryParams.put("role", role);
        queryParams.put("screenName", "testRole1");

        Response response = client.post(queryParams, "supervisor");
        assertEquals(response.getStatusCode(), 400, "The user should not be created with invalid role");
    }

    @DataProvider(name = "inputValidRole")
    public Object[][] inputValidRole() {
        return new Object[][]{{"admin"}, {"user"}};
    }

    @Test(dataProvider = "inputValidRole")
    public void verifyThatUserCanBeCreatedWithValidRole(String role) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("age", "36");
        queryParams.put("gender", "female");
        queryParams.put("login", "testValRole");
        queryParams.put("password", "testValRole12");
        queryParams.put("role", role);
        queryParams.put("screenName", "testValRole");

        Response response = client.post(queryParams, "supervisor");
        PlayerCreateResponseDto playerCreateResponseDto = response.as(PlayerCreateResponseDto.class);

        softAssert.assertEquals(response.getStatusCode(), 200, "The user should be created with valid role");

        PlayerGetByPlayerIdRequestDto playerGetByPlayerIdRequestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(playerCreateResponseDto.getId())
                .build();
        Response getUserResponse = client.get(playerGetByPlayerIdRequestDto);
        PlayerGetByPlayerIdResponseDto responseDto = getUserResponse.as(PlayerGetByPlayerIdResponseDto.class);
        softAssert.assertEquals(getUserResponse.getStatusCode(), 200, "The user should be successfully created");
        softAssert.assertEquals(responseDto.getRole(), role, "The role value should be equal to expected");

        softAssert.assertAll();
    }

    @Test
    public void verifyThatUserWithSupervisorRoleCanNotBeDeleted() {
        PlayerGetByPlayerIdRequestDto requestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(1)
                .build();

        Response response = client.get(requestDto);
        PlayerGetByPlayerIdResponseDto playerResponse = response.as(PlayerGetByPlayerIdResponseDto.class);

        softAssert.assertEquals(response.getStatusCode(), 200, "Response status code should be 200");
        softAssert.assertEquals(playerResponse.getRole(), "supervisor", "Response role should be equal to expected");

        PlayerDeleteRequestDto deleteRequestDto = PlayerDeleteRequestDto.builder()
                .playerId(1)
                .build();

        Response deleteResponse = client.delete("supervisor", deleteRequestDto);
        softAssert.assertEquals(deleteResponse.getStatusCode(), 403, "Response status code should be 403");

        softAssert.assertAll();
    }

    @Test
    public void verifyThatUserWithAdminRoleCanBeDeleted() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("age", "19");
        queryParams.put("gender", "female");
        queryParams.put("login", "adminLUse");
        queryParams.put("password", "adminLUsePl2");
        queryParams.put("role", "admin");
        queryParams.put("screenName", "adminNameUser");
        String pathParam = "admin";

        Response response = client.post(queryParams, pathParam);
        PlayerCreateResponseDto playerCreateResponseDto = response.as(PlayerCreateResponseDto.class);
        softAssert.assertEquals(response.getStatusCode(), 200, "The user should be successfully created");
        int id = playerCreateResponseDto.getId();

        PlayerGetByPlayerIdRequestDto requestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(id)
                .build();
        Response getUserResponse = client.get(requestDto);
        PlayerGetByPlayerIdResponseDto playerResponse = getUserResponse.as(PlayerGetByPlayerIdResponseDto.class);

        softAssert.assertEquals(getUserResponse.getStatusCode(), 200, "The user should be successfully created");
        softAssert.assertEquals(playerResponse.getRole(), "admin", "The role of created user should be equal to expected");

        PlayerDeleteRequestDto playerDeleteRequestDto = PlayerDeleteRequestDto.builder()
                .playerId(id)
                .build();
        Response deleteResponse = client.delete(pathParam, playerDeleteRequestDto);
        softAssert.assertEquals(deleteResponse.getStatusCode(), 204, "Response status code should be 204");

        Response getAllResponse = client.getAll();
        PlayerGetAllResponseDto playerGetAllResponseDto = getAllResponse.as(PlayerGetAllResponseDto.class);
        List<Integer> listId = playerGetAllResponseDto.getPlayers()
                .stream()
                .map(PlayerItem::getId)
                .collect(Collectors.toList());
        softAssert.assertFalse(listId.stream()
                        .anyMatch(playerId -> playerId.equals(id)),
                "The id of deleted user should not be in the list of existing users");

        softAssert.assertAll();
    }

    @Test
    public void verifyThatAdminCanNotEditTheUserWithSupervisorRole() {
        PlayerGetByPlayerIdRequestDto requestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(1)
                .build();

        Response response = client.get(requestDto);
        PlayerGetByPlayerIdResponseDto playerResponseDto = response.as(PlayerGetByPlayerIdResponseDto.class);
        softAssert.assertEquals(response.getStatusCode(), 200, "Response status code should be 200");
        softAssert.assertEquals(playerResponseDto.getId(), 1, "Response id should be equal to expected");
        softAssert.assertEquals(playerResponseDto.getRole(), "supervisor", "Response role should be equal to expected");

        PlayerUpdateRequestDto playerUpdateRequestDto = PlayerUpdateRequestDto.builder()
                .age(32)
                .gender("male")
                .build();

        Response updateResponse = client.patch("admin", String.valueOf(playerResponseDto.getId()), playerUpdateRequestDto);
        softAssert.assertEquals(updateResponse.getStatusCode(), 403, "Response status code should be 403");

        softAssert.assertAll();
    }

    // Bug:
    // The user was updated with invalid age field.
    // Expected response status code 400 but was 200
    // TODO enabled when bug will be fixed
    @Bug("bug_3")
    @Test(dataProvider = "inputInvalidAge", enabled = false)
    public void verifyThatTheUserCanNotBeUpdatedWithInvalidAge(String invalidAge) {
        PlayerGetByPlayerIdRequestDto requestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(1)
                .build();

        Response response = client.get(requestDto);
        PlayerGetByPlayerIdResponseDto responseDto = response.as(PlayerGetByPlayerIdResponseDto.class);

        softAssert.assertEquals(response.getStatusCode(), 200, "Response status code should be 200");

        PlayerUpdateRequestDto updateRequestDto = PlayerUpdateRequestDto.builder()
                .age(Integer.parseInt(invalidAge))
                .build();

        Response updateResponse = client.patch(responseDto.getRole(), String.valueOf(responseDto.getId()), updateRequestDto);
        softAssert.assertEquals(updateResponse.getStatusCode(), 400, "Response status code should be 400");

        Response responseAfterUpdating = client.get(requestDto);
        PlayerGetByPlayerIdResponseDto playerDtoAfterUpdating = responseAfterUpdating.as(PlayerGetByPlayerIdResponseDto.class);

        softAssert.assertEquals(responseAfterUpdating.getStatusCode(), 200, "Response status code should be 200");
        softAssert.assertEquals(playerDtoAfterUpdating.getId(), responseDto.getId(), "The id should be equal to expected");
        softAssert.assertEquals(playerDtoAfterUpdating.getAge(), responseDto.getAge(), "The age field should be the same as before updating");

        softAssert.assertAll();
    }

    // Bug:
    // The user was updated with invalid password field.
    // The expected response status code 400 but was 200.
    //TODO enabled when bug will be fixed
    @Bug("bug_4")
    @Test(dataProvider = "inputInvalidPassword", enabled = false)
    public void verifyThatUserFieldPasswordCanNotBeUpdatedWithInvalidValue(String invalidPassword) {
        PlayerGetByPlayerIdRequestDto requestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(1)
                .build();

        Response response = client.get(requestDto);
        PlayerGetByPlayerIdResponseDto responseDto = response.as(PlayerGetByPlayerIdResponseDto.class);

        softAssert.assertEquals(response.getStatusCode(), 200, "Response status code should be 200");

        PlayerUpdateRequestDto updateRequestDto = PlayerUpdateRequestDto.builder()
                .password(invalidPassword)
                .build();

        Response updateResponse = client.patch(responseDto.getRole(), String.valueOf(responseDto.getId()), updateRequestDto);
        softAssert.assertEquals(updateResponse.getStatusCode(), 400, "The user should not be updated with invalid password");

        Response responseAfterUpdating = client.get(requestDto);
        PlayerGetByPlayerIdResponseDto playerDtoAfterUpdating = responseAfterUpdating.as(PlayerGetByPlayerIdResponseDto.class);

        softAssert.assertEquals(responseAfterUpdating.getStatusCode(), 200, "Response status code should be 200");
        softAssert.assertEquals(playerDtoAfterUpdating.getId(), responseDto.getId(), "The id should be equal to expected");
        softAssert.assertEquals(playerDtoAfterUpdating.getPassword(), responseDto.getPassword(), "The password field should be equal to expected");

        softAssert.assertAll();
    }

    // Bug:
    // The user was updated with invalid gender field.
    // User`s ‘gender’ can only be: ‘male’ or ‘female’
    // Expected response status code 400 but was 200.
    //TODO enabled when bug will be fixed
    @Bug("bug_5")
    @Test(enabled = false)
    public void verifyThatUserFieldGenderCanNotBeUpdatedWithInvalidValue() {
        PlayerGetByPlayerIdRequestDto requestDto = PlayerGetByPlayerIdRequestDto.builder()
                .playerId(1)
                .build();

        Response response = client.get(requestDto);
        PlayerGetByPlayerIdResponseDto responseDto = response.as(PlayerGetByPlayerIdResponseDto.class);

        softAssert.assertEquals(response.getStatusCode(), 200, "Response status code should be 200");

        PlayerUpdateRequestDto updateRequestDto = PlayerUpdateRequestDto.builder()
                .gender("s")
                .build();

        Response updateResponse = client.patch(responseDto.getRole(), String.valueOf(responseDto.getId()), updateRequestDto);
        softAssert.assertEquals(updateResponse.getStatusCode(), 400, "The user should not be updated with invalid gender");

        Response responseAfterUpdating = client.get(requestDto);
        PlayerGetByPlayerIdResponseDto playerDtoAfterUpdating = responseAfterUpdating.as(PlayerGetByPlayerIdResponseDto.class);

        softAssert.assertEquals(responseAfterUpdating.getStatusCode(), 200, "Response status code should be 200");
        softAssert.assertEquals(playerDtoAfterUpdating.getId(), responseDto.getId(), "The id should be equal to expected");
        softAssert.assertEquals(playerDtoAfterUpdating.getGender(), responseDto.getGender(), "The gender field should not be updated");

        softAssert.assertAll();
    }
}
