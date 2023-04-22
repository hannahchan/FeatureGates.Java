package featuregates.internal;

import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;

import featuregates.InstrumentType;

class Instrumentation {

    private static final LongCounter EXECUTION_COUNTER = Library.METER
            .counterBuilder("feature.gate.executions")
            .setUnit(null)
            .setDescription("measures the number of times a feature gate has been executed")
            .build();

    private static final DoubleHistogram EXECUTION_DURATION_HISTOGRAM = Library.METER
            .histogramBuilder("feature.gate.duration")
            .setUnit("ms")
            .setDescription("measures the duration of feature gate executions")
            .build();

    static void recordExecution(String featureGateKey, FeatureGateState featureGateState, Runnable runnable, InstrumentType instrumentType) {
    }
}
