package costa.paltrinieri.felipe.fiscalData.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class FiscalDataRestClientConfig {

    private final FiscalDataProperties properties;

    public FiscalDataRestClientConfig(FiscalDataProperties properties) {
        this.properties = properties;
    }

    @Bean("fiscalDataRestClient")
    public RestClient restClient(RestClient.Builder builder) {
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMillis(properties.getReadTimeoutMs()));

        return builder
            .baseUrl(properties.getBaseUrl())
            .requestFactory(requestFactory)
            .build();
    }

    @Bean("fiscalDataRetry")
    public Retry retry() {
        RetryConfig config = RetryConfig.custom()
            .maxAttempts(properties.getMaxRetries())
            .intervalFunction(io.github.resilience4j.core.IntervalFunction.ofExponentialBackoff(500, 2))
            .build();

        return Retry.of("fiscalDataRetry", config);
    }

}
