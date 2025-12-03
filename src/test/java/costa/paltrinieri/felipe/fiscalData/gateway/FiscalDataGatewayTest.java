package costa.paltrinieri.felipe.fiscalData.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import costa.paltrinieri.felipe.core.exceptions.IntegrationException;
import costa.paltrinieri.felipe.fiscalData.config.FiscalDataProperties;
import costa.paltrinieri.felipe.infrastructure.metrics.FiscalDataMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class FiscalDataGatewayTest {

    private FiscalDataGateway gateway;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setUp() {
        FiscalDataProperties properties = new FiscalDataProperties();
        properties.setMaxMonthAgo(6);

        meterRegistry = new SimpleMeterRegistry();
        FiscalDataMetrics metrics = new FiscalDataMetrics(meterRegistry);
        RestClient restClient = RestClient.builder().build();
        ObjectMapper objectMapper = new ObjectMapper();

        gateway = new FiscalDataGateway(restClient, properties, objectMapper, metrics);
        gateway.init();
    }

    @Test
    void shouldUseFallback() {
        String currency = "Euro Zone-Euro";
        LocalDate purchaseDate = LocalDate.of(2024, 1, 15);

        Optional<BigDecimal> result = gateway.getExchangeRateFallback(currency, purchaseDate, new IntegrationException("API error"));

        assertThat(result).isPresent();
        assertThat(result.get()).isGreaterThan(BigDecimal.ZERO);
        assertThat(meterRegistry.counter("fiscal.data.fallback.usage", "service", "treasury-api").count()).isEqualTo(1.0);
    }

}
