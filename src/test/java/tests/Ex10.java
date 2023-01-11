package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex10 {

    @ParameterizedTest
    @ValueSource(strings = {"", "little string", "String more then 15 symbols"})
    @Test
    public void stringLengthLess15(String strings){
        assertTrue(strings.length() <= 15, "String doesn't less 15 symbols");
    }
}
