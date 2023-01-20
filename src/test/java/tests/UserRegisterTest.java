package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Users with email '" + email + "' already exists");
    }

    @Test
    public void testCreateUserSuccess(){
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response response = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user")
                .andReturn();

        Assertions.assertResponseCodeEquals(response, 200);
        Assertions.assertJsonHasField(response, "id");
    }

    @Description("This test check registration w/o sending '@' symbol in email")
    @DisplayName("Test negative registration user")
    @Test
    public void testCreateUserWithoutSymbolInEmail(){
        String email = "vinkotovexample.com";
        String url = "https://playground.learnqa.ru/api/user";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests.makePostRequestRegisterUser(url, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
    }

    @Description("This test check registration w/o sending one of necessary field")
    @DisplayName("Test negative registration user")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testNegativeAuthUser(String condition){
        String url = "https://playground.learnqa.ru/api/user";

        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationData(userData);
        switch (condition){
            case ("email"):
                userData.keySet().removeIf(key -> key == "email");
                break;
            case ("password"):
                userData.keySet().removeIf(key -> key == "password");
                break;
            case ("username"):
                userData.keySet().removeIf(key -> key == "username");
                break;
            case ("firstName"):
                userData.keySet().removeIf(key -> key == "firstName");
                break;
            case ("lastName"):
                userData.keySet().removeIf(key -> key == "lastName");
                break;
        }

        Response response = apiCoreRequests.makePostRequestRegisterUser(url, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The following required params are missed: " + condition);
    }

    @Description("This test check registration sending one symbol in email")
    @DisplayName("Test negative registration user")
    @Test
    public void testCreateUserWithOneSymbolInEmail(){
        String email = "v";
        String url = "https://playground.learnqa.ru/api/user";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests.makePostRequestRegisterUser(url, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'email' field is too short");
    }

    @Description("This test check registration sending one symbol in email")
    @DisplayName("Test negative registration user")
    @Test
    public void testCreateUserWith251SymbolsInEmail(){
        String email = "А также элементы политического процесса могут быть призваны к ответу. А также тщательные исследования конкурентов представляют собой не что иное, как квинтэссенцию победы маркетинга над разумом и должны быть разоблачены. Мы вынуждены отталкиваться от ";
        String url = "https://playground.learnqa.ru/api/user";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response response = apiCoreRequests.makePostRequestRegisterUser(url, userData);

        Assertions.assertResponseCodeEquals(response, 400);
        Assertions.assertResponseTextEquals(response, "The value of 'email' field is too long");
    }
}
