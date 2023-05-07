package featuregates.internal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class InstrumentationTest {
    @ParameterizedTest
    @ValueSource(strings = {"a", "b", "c"})
    void someLibraryMethodReturnsTrue(String test) {
        assertNotEquals(test.length(), 0);
    }
}
