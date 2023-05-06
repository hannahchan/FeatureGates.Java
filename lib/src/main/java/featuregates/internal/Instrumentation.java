package featuregates.internal;

import featuregates.InstrumentType;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;

class Instrumentation {

    // NOTE: This is deliberately a 'double' type
    private static final double NANOSECONDS_IN_A_MILLISECOND = 1_000_000L;

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
        Span span = startSpan(featureGateKey, featureGateState);
        long startNanoTime = System.nanoTime();

        try (Scope scope = span.makeCurrent()) {
            if (runnable == null) {
                return;
            }

            runnable.run();

        } catch (RuntimeException exception) {
            featureGateException = true;
            span.recordException(exception);
            throw exception;

        } finally {
            recordSpanStatus(span, featureGateException);

            long elapsedNanoseconds = System.nanoTime() - startNanoTime;
            Attributes attributes = createAttributes(featureGateKey, featureGateState, featureGateException);
            recordMeasurement(instrumentType, elapsedNanoseconds, attributes);

            span.end();
        }
    }

    private static Span startSpan(String featureGateKey, FeatureGateState featureGateState) {
        return Library.TRACER.spanBuilder("feature.gate.execution")
                .setSpanKind(SpanKind.INTERNAL)
                .setAttribute(SemanticConventions.ATTRIBUTE_FEATURE_GATE_KEY, featureGateKey)
                .setAttribute(SemanticConventions.ATTRIBUTE_FEATURE_GATE_STATE,
                        featureGateState == FeatureGateState.OPENED ? "opened" : "closed")
                .startSpan();
    }

    private static void recordSpanStatus(Span span, boolean featureGateException) {
        if (featureGateException) {
            span.setStatus(StatusCode.ERROR, "An uncaught exception occurred during feature gate execution.");
            return;
        }

        span.setStatus(StatusCode.OK);
    }

    private static Attributes createAttributes(String featureGateKey, FeatureGateState featureGateState,
            boolean featureGateException) {
        return Attributes.builder()
                .put(SemanticConventions.ATTRIBUTE_FEATURE_GATE_KEY, featureGateKey)
                .put(SemanticConventions.ATTRIBUTE_FEATURE_GATE_STATE,
                        featureGateState == FeatureGateState.OPENED ? "opened" : "closed")
                .put(SemanticConventions.ATTRIBUTE_FEATURE_GATE_EXCEPTION, featureGateException ? "true" : "false")
                .build();
    }

    private static void recordMeasurement(InstrumentType instrumentType, long elapsedNanoseconds,
            Attributes attributes) {
        switch (instrumentType) {
            case COUNTER:
                EXECUTION_COUNTER.add(1, attributes);
                break;
            case HISTOGRAM:
                EXECUTION_DURATION_HISTOGRAM.record(elapsedNanoseconds / NANOSECONDS_IN_A_MILLISECOND, attributes);
                break;
            default:
                break;
        }
    }
}
