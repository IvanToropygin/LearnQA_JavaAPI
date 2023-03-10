package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request without token and cookie")
    public Response makeGetRequestWithoutTokenAndCookie(String url){
        return given()
                .filter(new AllureRestAssured())
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request with token and auth cookie")
    public Response makePostRequest(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request register user")
    public Response makePostRequestRegisterUser(String url, Map<String, String> authData){
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request create user")
    public JsonPath makePostRequestCreateUser(String url, Map<String, String> userData){
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .jsonPath();
    }

    @Step("Make a PUT-request edit user")
    public Response makePutRequestEditUser(String url, Map<String, String> userData){
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a DELETE-request to delete user")
    public Response makeDeleteUserRequest(String url, String token, String cookie){
        return given()
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .delete(url)
                .andReturn();
    }
}
