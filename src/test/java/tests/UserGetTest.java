package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        Assertions.assertJsonHasField(response, "username");
        Assertions.assertJsonHasNotField(response, "firstName");
        Assertions.assertJsonHasNotField(response, "lastName");
        Assertions.assertJsonHasNotField(response, "email");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        String cookie = getCookie(responseGetAuth, "auth_sid");
        String header = getHeader(responseGetAuth, "x-csrf-token");

        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/2")
                .andReturn();

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseCheckAuth, expectedFields);
    }

    @Test
    public void testGetUserDetailsAuthAsAnotherUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        String urlLogin = "https://playground.learnqa.ru/api/user/login";
        Response responseGetAuth = apiCoreRequests.makePostRequestRegisterUser(urlLogin, authData);

        String cookie = getCookie(responseGetAuth, "auth_sid");
        String header = getHeader(responseGetAuth, "x-csrf-token");

        String urlUser = "https://playground.learnqa.ru/api/user/1";
        Response responseCheckAuth = apiCoreRequests.makeGetRequest(urlUser, cookie, header);

        System.out.println(responseCheckAuth.asString());

        Assertions.assertJsonHasField(responseCheckAuth, "username");
        Assertions.assertJsonHasNotField(responseCheckAuth, "firstName");
        Assertions.assertJsonHasNotField(responseCheckAuth, "lastName");
        Assertions.assertJsonHasNotField(responseCheckAuth, "email");
    }
}
