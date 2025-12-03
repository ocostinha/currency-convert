package costa.paltrinieri.felipe.infrastructure.actuator;

import costa.paltrinieri.felipe.fiscalData.config.FiscalDataProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FiscalDataHealthIndicator implements HealthIndicator {

    private final RestClient restClient;
    private final FiscalDataProperties properties;

    public FiscalDataHealthIndicator(@Qualifier("fiscalDataRestClient") RestClient restClient, FiscalDataProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Override
    public Health health() {
        try {
            restClient.get()
                .uri(properties.getRatesOfExchangePath() + "?page[size]=1")
                .retrieve()
                .toBodilessEntity();
            return Health.up().withDetail("service", "Fiscal Data API").build();
        } catch (Exception e) {
            return Health.down().withDetail("service", "Fiscal Data API").withDetail("error", e.getMessage()).build();
        }
    }

}
