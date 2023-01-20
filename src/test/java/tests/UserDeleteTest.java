package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Issue;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Epic("Сustomers interaction")
    @Feature("Deleting customers")
    @DisplayName("Create a user, log in from under him, delete, then try to get his data by ID and make sure that the user is really deleted")
    @Severity(SeverityLevel.BLOCKER)
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
    @Epic("Сustomers interaction")
    @Feature("Deleting customers")
    @DisplayName("try to delete user by ID 2")
    @Severity(SeverityLevel.NORMAL)
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
    @Epic("Сustomers interaction")
    @Feature("Deleting customers")
    @DisplayName("try to delete the user while being logged in by another user")
    @Severity(SeverityLevel.CRITICAL)
    @Issue("1")
    @TmsLink("test-1")
    @Description("Баг метода: не удаляется пользователь, указанного ID, а удаляется, под которым прошла авторизация")
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

        String urlUserSecond = "https://playground.learnqa.ru/api/user/" + userSecondId;

        Response responseDelete = apiCoreRequests.makeDeleteUserRequest(urlUserFirst, header, cookie);

        //Assert
        Response responseFirstUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(urlUserFirst);
        Response responseSecondUserData = apiCoreRequests.makeGetRequestWithoutTokenAndCookie(urlUserSecond);

        assertAll(
                () -> assertEquals(userDataSecond.get("username"), responseSecondUserData.getBody().path("username")),
                () -> assertEquals(userDataFirst.get("username"), responseFirstUserData.getBody().path("username"))
        );
    }
}
