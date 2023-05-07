package featuregates.internal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import featuregates.InstrumentType;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

class InstrumentationTest {
    @ParameterizedTest
    @MethodSource("featureGateStateToInstrumentationType")
    void when_RecordingExecution_Expect_Recorded(String test) {
        assertNotEquals(test.length(), 0);

        // Arrange

        // Act

        // Assert
    }

    private static Stream<Arguments> featureGateStateToInstrumentationType() {
        return Stream.of(
                Arguments.of(FeatureGateState.CLOSED, InstrumentType.COUNTER),
                Arguments.of(FeatureGateState.CLOSED, InstrumentType.HISTOGRAM),
                Arguments.of(FeatureGateState.OPENED, InstrumentType.COUNTER),
                Arguments.of(FeatureGateState.OPENED, InstrumentType.HISTOGRAM));
    }

    private static Stream<Arguments> featureGateStateToNoneInstrumentationType() {
        return Stream.of(
                Arguments.of(FeatureGateState.CLOSED, InstrumentType.NONE),
                Arguments.of(FeatureGateState.OPENED, InstrumentType.NONE));
    }
}
