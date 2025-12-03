package costa.paltrinieri.felipe.infrastructure.circuitBreak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.function.Predicate;

public class CircuitBreakerPredicate implements Predicate<Throwable> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CircuitBreakerPredicate.class);

    @Override
    public boolean test(Throwable throwable) {
        if (throwable instanceof HttpStatusCodeException httpException) {
            int statusCode = httpException.getStatusCode().value();

            LOGGER.debug("Circuit breaker evaluating error with status code: {}", statusCode);

            return statusCode >= 500;
        }

        LOGGER.debug("Circuit breaker evaluating non-HTTP error: {}", throwable.getMessage());

        return true;
    }

}