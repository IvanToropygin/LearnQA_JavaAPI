package tests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testDeleteJustCreatedTest(){
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String urlCreate = "https://playground.learnqa.ru/api/user";

        JsonPath responseCreateUser = apiCoreRequests.makePostRequestCreateUser(urlCreate, userData);
        String userId = responseCreateUser.getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuth = apiCoreRequests.makePostRequest(urlLogin, authData);

        String cookie = getCookie(responseGetAuth, "auth_sid");
        String header = getHeader(responseGetAuth, "x-csrf-token");

        //Delete
        String urlDelete = "https://playground.learnqa.ru/api/user/" + userId;

        Response responseDelete = apiCoreRequests.makeDeleteUserRequest(urlDelete, header, cookie);

        //Get update data
        String urlUser = "https://playground.learnqa.ru/api/user/" + userId;

        Response responseUserData = apiCoreRequests.makeGetRequest(urlUser, header, cookie);

        //Assert
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    public void testDeleteID2Test(){
        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuth = apiCoreRequests.makePostRequest(urlLogin, authData);

        String cookie = getCookie(responseGetAuth, "auth_sid");
        String header = getHeader(responseGetAuth, "x-csrf-token");

        //Delete
        String urlDelete2 = "https://playground.learnqa.ru/api/user/2";

        Response responseDelete = apiCoreRequests.makeDeleteUserRequest(urlDelete2, header, cookie);

        //Assert
        assertAll(
                () -> Assertions.assertResponseCodeEquals(responseDelete, 400),
                () -> Assertions.assertResponseTextEquals(responseDelete, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.")
        );
    }

    @Test
    public void testDeleteJustCreatedAuthAnotherAuthUserTest() {
        String urlCreate = "https://playground.learnqa.ru/api/user";
        //Generate user1
        Map<String, String> userDataFirst = DataGenerator.getRegistrationData();

        JsonPath responseCreateUserFirst = apiCoreRequests.makePostRequestCreateUser(urlCreate, userDataFirst);
        String userFirstId = responseCreateUserFirst.getString("id");

        //Generate user2
        Map<String, String> userDataSecond = DataGenerator.getRegistrationData();

        JsonPath responseCreateUserSecond = apiCoreRequests.makePostRequestCreateUser(urlCreate, userDataSecond);
        String userSecondId = responseCreateUserSecond.getString("id");

        //Login user2
        Map<String, String> authDataSecond = new HashMap<>();
        authDataSecond.put("email", userDataSecond.get("email"));
        authDataSecond.put("password", userDataSecond.get("password"));

        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuthSecond = apiCoreRequests.makePostRequest(urlLogin, authDataSecond);
        String cookie = getCookie(responseGetAuthSecond, "auth_sid");
        String header = getHeader(responseGetAuthSecond, "x-csrf-token");

        //Delete user1
        String urlUserFirst = "https://playground.learnqa.ru/api/user/" + userFirstId;

        Response responseDelete = apiCoreRequests.makeDeleteUserRequest(urlUserFirst, header, cookie);

        //Assert
        Response responseUserData = apiCoreRequests.makeGetRequest(urlUserFirst, header, cookie);
        responseUserData.prettyPrint();

        assertAll(
                () -> Assertions.assertResponseCodeEquals(responseDelete, 400),
                () -> Assertions.assertResponseTextEquals(responseDelete, "Unknown text")
        );
    }
}
