import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

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

    @Test
    public void EX6_Redirect() {
        String URL = "https://playground.learnqa.ru/api/long_redirect";
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(URL)
                .andReturn();

        System.out.println(response.getHeader("Location"));
    }

    @Test
    public void EX7_LongRedirect() {
        String URL = "https://playground.learnqa.ru/api/long_redirect";
        int count_redirects = 0;
        while (true) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(true)
                    .when()
                    .get(URL)
                    .andReturn();
            count_redirects++;
            if (response.getStatusCode() == 200) {
                break;
            }
        }
        System.out.println("Количество редиректов: " + count_redirects);
    }

    @Test
    public void EX8_Tokens() throws InterruptedException {
        String URL = "https://playground.learnqa.ru/ajax/api/longtime_job";

        JsonPath responseBeforeToken = RestAssured
                .get(URL)
                .jsonPath();

        String token = responseBeforeToken.get("token");
        System.out.println("token: " + token);

        int seconds = responseBeforeToken.get("seconds");
        System.out.println("seconds: " + seconds);

        Map<String, String> param = new HashMap<>();
        param.put("token", token);

        Response responseAfterToken = RestAssured
                .given()
                .queryParams(param)
                .when()
                .get(URL)
                .andReturn();

        System.out.println("Response after get with token:");
        responseAfterToken.prettyPrint();

        Thread.sleep((seconds) * 1000);

        Response responseAfterTokenAndExpectation = RestAssured
                .given()
                .queryParams(param)
                .when()
                .get(URL)
                .andReturn();

        System.out.println("Response after get with token and expectation:");
        responseAfterTokenAndExpectation.prettyPrint();
    }

    @Test
    public void EX9_passwordGuessing() throws IOException {
        FileReader fr = new FileReader("passwords.txt");
        Scanner sc = new Scanner(fr);
        String stringFromSc = "";
        while (sc.hasNextLine()) {
            stringFromSc += (sc.nextLine());
        }
        fr.close();

        String[] passwords = stringFromSc.split("\t");

        Set<String> passwordsWithoutDuplicates = new HashSet<>();
        for (String str : passwords) {
            passwordsWithoutDuplicates.add(str);
        }

        for (String str : passwordsWithoutDuplicates) {
            Map<String, String> body = new HashMap<>();
            body.put("login", "super_admin");
            body.put("password", str);

            String URL_login = "https://playground.learnqa.ru/ajax/api/get_secret_password_homework";
            Response responseFromGet = RestAssured
                    .given()
                    .body(body)
                    .when()
                    .post(URL_login)
                    .andReturn();

            String respCookies = responseFromGet.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            if (respCookies != null) {
                cookies.put("auth_cookie", respCookies);
            }

            String URL_checkCookies = "https://playground.learnqa.ru/ajax/api/check_auth_cookie";
            Response responseCheckCookies = RestAssured
                    .given()
                    .cookies(cookies)
                    .when()
                    .post(URL_checkCookies)
                    .andReturn();

            String answer = responseCheckCookies.asString();
            Boolean result = answer.equals("You are NOT authorized");
            if (!result){
                System.out.println("Password is: " + str);
                break;
            }
        }
    }
}
