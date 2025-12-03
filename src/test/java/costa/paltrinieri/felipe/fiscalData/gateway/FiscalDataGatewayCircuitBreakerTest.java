package costa.paltrinieri.felipe.fiscalData.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "resilience4j.circuitbreaker.instances.fiscal-data-service.failure-rate-threshold=50",
    "resilience4j.circuitbreaker.instances.fiscal-data-service.sliding-window-size=5",
    "resilience4j.circuitbreaker.instances.fiscal-data-service.minimum-number-of-calls=3",
    "spring.cache.type=none"
})
class FiscalDataGatewayCircuitBreakerTest {

    @Autowired
    private FiscalDataGateway gateway;

    @MockBean
    @Qualifier("fiscalDataRestClient")
    private RestClient fiscalDataRestClient;

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void shouldUseFallbackWhenCircuitBreakerOpens() {
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = org.mockito.Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = org.mockito.Mockito.mock(RestClient.ResponseSpec.class);

        when(fiscalDataRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(Class.class))).thenThrow(new RuntimeException("API unavailable"));

        String currency = "Euro Zone-Euro";
        LocalDate purchaseDate = LocalDate.of(2024, 1, 15);

        for (int i = 0; i < 5; i++) {
            Optional<BigDecimal> result = gateway.getExchangeRate(currency, purchaseDate);
            assertThat(result).isPresent();
            assertThat(result.get()).isGreaterThan(BigDecimal.ZERO);
        }
    }

}
