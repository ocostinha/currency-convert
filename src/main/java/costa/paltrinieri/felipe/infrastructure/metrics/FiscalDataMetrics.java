package costa.paltrinieri.felipe.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class FiscalDataMetrics {

    private final Timer apiLatencyTimer;
    private final Counter circuitBreakerOpenCounter;
    private final Counter apiErrorCounter;
    private final Counter fallbackUsageCounter;

    public FiscalDataMetrics(MeterRegistry registry) {
        this.apiLatencyTimer = Timer.builder("fiscal.data.api.latency")
            .description("Latency of Fiscal Data API calls")
            .tag("service", "treasury-api")
            .register(registry);

        this.circuitBreakerOpenCounter = Counter.builder("fiscal.data.circuit.breaker.open")
            .description("Circuit breaker open events")
            .tag("service", "treasury-api")
            .register(registry);

        this.apiErrorCounter = Counter.builder("fiscal.data.api.errors")
            .description("Fiscal Data API errors")
            .tag("service", "treasury-api")
            .register(registry);

        this.fallbackUsageCounter = Counter.builder("fiscal.data.fallback.usage")
            .description("Fallback usage count")
            .tag("service", "treasury-api")
            .register(registry);
    }

    public Timer.Sample startTimer() {
        return Timer.start();
    }

    public void recordLatency(Timer.Sample sample) {
        sample.stop(apiLatencyTimer);
    }

    public void recordCircuitBreakerOpen() {
        circuitBreakerOpenCounter.increment();
    }

    public void recordApiError() {
        apiErrorCounter.increment();
    }

    public void recordFallbackUsage() {
        fallbackUsageCounter.increment();
    }

}
