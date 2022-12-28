import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class HelloWorldTest {

    @Test
    public void testHelloWorld(){
        Response response = RestAssured
                .get("http://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testHelloFromIvan(){
        System.out.println("Hello from Ivan");
    }

    @Test
    public void testGetRequest(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        String resp = response.getBody().asString();
        System.out.println(resp);
    }
}
