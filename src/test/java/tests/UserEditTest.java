package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testEditJustCreatedTest(){
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateUser = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .jsonPath();
        String userId = responseCreateUser.getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //Edit
        String newName = "Changed_name";
        Map<String, String> body = new HashMap<>();
        body.put("firstName", newName);

        String cookie = getCookie(responseGetAuth, "auth_sid");
        String header = getHeader(responseGetAuth, "x-csrf-token");

        Response responseEdit = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .body(body)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //Get update data
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    public void testEditJustCreatedAuthAnotherAuthUserTest() {
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

        //Edit user1
        String newName = "Changed_name";
        Map<String, String> body = new HashMap<>();
        body.put("firstName", newName);

        String urlUser = "https://playground.learnqa.ru/api/user/" + userFirstId;
        Response responseEdit = apiCoreRequests.makePutRequestEditUser(urlUser, body);

        //Login user1
        Map<String, String> authDataFirst = new HashMap<>();
        authDataFirst.put("email", userDataFirst.get("email"));
        authDataFirst.put("password", userDataFirst.get("password"));

        Response responseGetAuthFirst = apiCoreRequests.makePostRequest(urlLogin, authDataFirst);

        String cookieFirst = getCookie(responseGetAuthFirst, "auth_sid");
        String headerFirst = getHeader(responseGetAuthFirst, "x-csrf-token");

        //Get update data
        Response responseUserData = apiCoreRequests.makeGetRequest(urlUser, headerFirst, cookieFirst);

        Assertions.assertJsonByName(responseUserData, "firstName", "learnqa");
    }

    @Test
    public void testEditJustCreatedWithoutAuthTest(){
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        String urlCreate = "https://playground.learnqa.ru/api/user";

        JsonPath responseCreateUser = apiCoreRequests.makePostRequestCreateUser(urlCreate, userData);
        String userId = responseCreateUser.getString("id");

        //Edit
        String newName = "Changed_name";
        Map<String, String> body = new HashMap<>();
        body.put("firstName", newName);

        String urlUser = "https://playground.learnqa.ru/api/user/" + userId;
        Response responseEdit = apiCoreRequests.makePutRequestEditUser(urlUser, body);

        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuth = apiCoreRequests.makePostRequest(urlLogin, authData);

        String cookie = getCookie(responseGetAuth, "auth_sid");
        String header = getHeader(responseGetAuth, "x-csrf-token");

        //Get update data
        Response responseUserData = apiCoreRequests.makeGetRequest(urlUser, header, cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", "learnqa");
    }

    @Test
    public void testEditWrongEmailJustCreatedTest(){
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

        //Edit
        String newEmail = "Changed_name.ru";
        Map<String, String> body = new HashMap<>();
        body.put("email", newEmail);

        String urlUser = "https://playground.learnqa.ru/api/user/" + userId;
        Response responseEdit = apiCoreRequests.makePutRequestEditUser(urlUser, body);

        //Get update data
        Response responseUserData = apiCoreRequests.makeGetRequest(urlUser, header, cookie);

        Assertions.assertJsonByName(responseUserData, "email", userData.get("email"));
    }

    @Test
    public void testEditNameOneSymbolJustCreatedTest(){
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

        //Edit
        String newName = "q";
        Map<String, String> body = new HashMap<>();
        body.put("firstName", newName);

        String urlUser = "https://playground.learnqa.ru/api/user/" + userId;
        Response responseEdit = apiCoreRequests.makePutRequestEditUser(urlUser, body);

        //Get update data
        Response responseUserData = apiCoreRequests.makeGetRequest(urlUser, header, cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }
}
