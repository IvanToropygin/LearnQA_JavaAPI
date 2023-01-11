package tests;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class Ex13 {
    private static Stream<Arguments> userAgentHeaders() {
        return Stream.of(
                arguments("Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30",
                        "Mobile", "No", "Android"
                ),
                arguments("Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1",
                        "Mobile", "Chrome", "iOS"
                ),
                arguments("Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)",
                        "Googlebot", "Unknown", "Unknown"
                ),
                arguments("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0",
                        "Web", "Chrome", "No"
                ),
                arguments("Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1",
                        "Mobile", "No", "iPhone"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("userAgentHeaders")
    public void testUserAgent(String userAgent, String device, String browser, String platform) {
        Response response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .post("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();
        String actualResponseUserAgent = response.jsonPath().getString("user_agent");
        String actualResponsePlatform = response.jsonPath().getString("platform");
        String actualResponseBrowser = response.jsonPath().getString("browser");
        String actualResponseDevice = response.jsonPath().getString("device");

        Assertions.assertAll(
                () -> assertEquals(userAgent, actualResponseUserAgent, "Unexpected User Agent"),
                () -> assertEquals(device, actualResponseDevice, "Unexpected Device"),
                () -> assertEquals(browser, actualResponseBrowser, "Unexpected Browser"),
                () -> assertEquals(platform, actualResponsePlatform, "Unexpected Platform")
        );
    }
}
