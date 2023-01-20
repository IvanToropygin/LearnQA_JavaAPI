package tests.homeworks;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class EX12 {

    @Test
    public void testHeaderMethod(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        Headers headers = response.getHeaders();

        Assertions.assertAll(
                () -> assertTrue(headers.hasHeaderWithName("x-secret-homework-header"),
                        "headers doesn't contains secret header"),
                () -> assertTrue(headers.getValue("x-secret-homework-header").equals("Some secret value"),
                        "cookie with name 'Some secret value' doesn't contains value 'Some secret value'")
        );
    }
}
