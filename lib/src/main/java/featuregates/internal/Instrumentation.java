package featuregates.internal;

import java.util.concurrent.TimeUnit;

import featuregates.InstrumentType;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;

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

    static void recordExecution(String featureGateKey, FeatureGateState featureGateState, Runnable runnable,
            InstrumentType instrumentType) {

        boolean featureGateException = false;
        long startNanoTime = System.nanoTime();

        try {
            if (runnable == null) {
                return;
            }

            runnable.run();
        } catch (RuntimeException exception) {
            featureGateException = true;
            // TODO: Record exception in span.
            throw exception;
        } finally {
            // TODO: Set span status to `featureGateException`.

            // TODO: Record measurement with tags.
            long elapsedNanoseconds = System.nanoTime() - startNanoTime;
            recordMeasurement(instrumentType, elapsedNanoseconds);
        }
    }

    private static void recordMeasurement(InstrumentType instrumentType, long elapsedNanoseconds) {
        switch (instrumentType) {

            case COUNTER:
                EXECUTION_COUNTER.add(1);
                break;

            case HISTOGRAM:
                // TODO: Check unit of measurements here.
                EXECUTION_DURATION_HISTOGRAM.record(TimeUnit.NANOSECONDS.toMillis(elapsedNanoseconds));
                break;

            default:
                break;
        }
    }
}
