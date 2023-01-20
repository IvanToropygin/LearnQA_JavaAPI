package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    public static void assertJsonByName(Response response, String name, int expectedValue){
        response.then().assertThat().body("$", hasKey(name));

        int value = response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "Json value not equal to expected value");
    }

    public static void assertJsonByName(Response response, String name, String expectedValue){
        response.then().assertThat().body("$", hasKey(name));

        String value = response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "Json value not equal to expected value");
    }

    public static void assertResponseTextEquals(Response response, String expectedAnswer){
        assertEquals(expectedAnswer, response.asString(), "Unexpected response answer");
    }

    public static void assertResponseCodeEquals(Response response, int expectedStatusCode){
        assertEquals(expectedStatusCode, response.statusCode(), "Unexpected status code");
    }

    public static void assertJsonHasField(Response response, String expectedKey){
        response.then().assertThat().body("$", hasKey(expectedKey));
    }
    public static void assertJsonHasFields(Response response, String[] expectedKeys){
        for(String key : expectedKeys){
            Assertions.assertJsonHasField(response, key);
        }
    }

    public static void assertJsonHasNotField(Response response, String unexpectedKey){
        response.then().assertThat().body("$", not(hasKey(unexpectedKey)));
    }

    public static void assertJsonHasNotFields(Response response, String[] unexpectedKeys){
        for(String key : unexpectedKeys){
            Assertions.assertJsonHasField(response, key);
        }
    }
}
