package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EX11{

    @Test
    public void cookieMethod(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        Map<String, String> cookies = response.getCookies();

        Assertions.assertAll(
                () -> assertTrue(cookies.containsKey("HomeWork"), "cookies doesn't contains key 'HomeWork'"),
                () -> assertTrue(cookies.containsValue("hw_value"), "cookies doesn't contains value 'hw_value'")
        );


    }
}
