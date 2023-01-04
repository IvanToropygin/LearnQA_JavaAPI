import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.Configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {

        Map<String, String> params = new HashMap<>();
        params.put("name", "Ivan");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("http://playground.learnqa.ru/api/hello")
                .jsonPath();

        String name = response.get("answer");
        if (name == null) {
            System.out.println("The key 'answer' is absent");
        } else {
            System.out.println(name);
        }
    }

    @Test
    public void testRestAssured() {

        Map<String, String> body = new HashMap<>();
        body.put("param1", "value1");
        body.put("param2", "value2");

        Response response = RestAssured
                .given()
                .body(body)
                .when()
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        response.print();
    }

    @Test
    public void testRestHeaders() {

        Map<String, String> headers = new HashMap<>();
        headers.put("myHeader1", "value1");
        headers.put("myHeader2", "value2");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();

        response.prettyPrint();
    }

    @Test
    public void testStatusCode200() {

        Response response = RestAssured
                .get("http://playground.learnqa.ru/api/check_type")
                .andReturn();

        System.out.println(response.getStatusCode());
    }

    @Test
    public void testStatusCode500() {

        Response response = RestAssured
                .get("http://playground.learnqa.ru/api/get_500")
                .andReturn();

        System.out.println(response.getStatusCode());
    }

    @Test
    public void testHelloFromIvan() {

        System.out.println("Hello from Ivan");
    }

    @Test
    public void testGetRequest() {

        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        String resp = response.getBody().asString();
        System.out.println(resp);
    }

    @Test
    public void testRestCookies() {

        Map<String, String> body = new HashMap<>();
        body.put("login", "secret_login");
        body.put("password", "secret_pass");

        Response responseFromGet = RestAssured
                .given()
                .body(body)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String respCookies = responseFromGet.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if (respCookies != null) {
            cookies.put("auth_cookie", respCookies);
        }

        Response respCheck = RestAssured
                .given()
                .body(body)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        respCheck.print();
    }

    @Test
    public void EX5_ParseJSON() {
        String URL = "https://playground.learnqa.ru/api/get_json_homework";
        JsonPath response = RestAssured
                .given()
                .get(URL)
                .jsonPath();

        List<String> messages = response.getList("messages");

        System.out.println(messages.stream().toArray()[1]);
    }
}
