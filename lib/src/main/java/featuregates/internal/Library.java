package featuregates.internal;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;

class Library {

    static final String NAME = "featuregates";

    static final String VERSION = "1.0.0";

    static final Meter METER = GlobalOpenTelemetry.getMeter(NAME);

    static final Tracer TRACER = GlobalOpenTelemetry.getTracer(NAME, VERSION);
}
