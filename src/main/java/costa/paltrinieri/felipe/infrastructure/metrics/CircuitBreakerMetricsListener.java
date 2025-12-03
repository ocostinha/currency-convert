package costa.paltrinieri.felipe.infrastructure.metrics;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CircuitBreakerMetricsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerMetricsListener.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final FiscalDataMetrics metrics;

    public CircuitBreakerMetricsListener(CircuitBreakerRegistry circuitBreakerRegistry, FiscalDataMetrics metrics) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.metrics = metrics;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerEventListener() {
        circuitBreakerRegistry.circuitBreaker("fiscal-data-service")
            .getEventPublisher()
            .onStateTransition(event -> {
                if (event.getStateTransition().getToState() == CircuitBreaker.State.OPEN) {
                    LOGGER.warn("Circuit breaker opened for fiscal-data-service");
                    metrics.recordCircuitBreakerOpen();
                }
            });
    }

}
