package costa.paltrinieri.felipe.fiscalData.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import costa.paltrinieri.felipe.core.exceptions.IntegrationException;
import costa.paltrinieri.felipe.fiscalData.config.FiscalDataProperties;
import costa.paltrinieri.felipe.fiscalData.dto.ExchangeRateResponse;
import costa.paltrinieri.felipe.infrastructure.metrics.FiscalDataMetrics;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Timer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class FiscalDataGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(FiscalDataGateway.class);

    private final RestClient restClient;
    private final FiscalDataProperties properties;
    private final ObjectMapper objectMapper;
    private final FiscalDataMetrics metrics;
    private List<ExchangeRateResponse.ExchangeRateData> fallbackData;

    public FiscalDataGateway(@Qualifier("fiscalDataRestClient") RestClient restClient,
                             FiscalDataProperties properties,
                             ObjectMapper objectMapper,
                             FiscalDataMetrics metrics) {
        this.restClient = restClient;
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.metrics = metrics;
    }

    @PostConstruct
    public void init() {
        try {
            final ClassPathResource resource = new ClassPathResource("fiscalData/RprtRateXchgCln_20010331_20250930.json");
            final ExchangeRateResponse response = objectMapper.readValue(resource.getInputStream(), ExchangeRateResponse.class);

            this.fallbackData = response != null && response.getData() != null ? response.getData() : List.of();

            LOGGER.info("Fallback data loaded successfully with {} records", fallbackData.size());
        } catch (IOException e) {
            LOGGER.error("Failed to load fallback data: {}", e.getMessage());

            this.fallbackData = List.of();
        }
    }

    @Cacheable(value = "exchangeRate", key = "#currency + '_' + #purchaseDate")
    @Retry(name = "fiscalDataRetry")
    @CircuitBreaker(name = "fiscal-data-service", fallbackMethod = "getExchangeRateFallback")
    public Optional<BigDecimal> getExchangeRate(final String currency, final LocalDate purchaseDate) {
        LOGGER.info("Fetching exchange rate for currency: {} on date: {}", currency, purchaseDate);

        final Timer.Sample sample = metrics.startTimer();
        final LocalDate maxMonthsAgo = purchaseDate.minusMonths(this.properties.getMaxMonthAgo());

        try {
            ExchangeRateResponse response = this.restClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path(properties.getRatesOfExchangePath())
                    .queryParam(
                        "filter", String.format(
                            "record_date:gte:%s,record_date:lte:%s,country_currency_desc:eq:%s",
                            maxMonthsAgo.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            purchaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                            currency
                        )
                    )
                    .queryParam("sort", "-record_date")
                    .queryParam("page[size]", "1")
                    .build())
                .retrieve()
                .body(ExchangeRateResponse.class);

            if (response == null || response.getData() == null || response.getData().isEmpty()) {
                LOGGER.error("Invalid API response structure");

                metrics.recordApiError();

                return getExchangeRateFallback(currency, purchaseDate, new IntegrationException("Invalid API response structure"));
            }

            ExchangeRateResponse.ExchangeRateData data = response.getData().getFirst();

            if (data.getExchangeRate() == null || data.getRecordDate() == null) {
                LOGGER.error("Invalid exchange rate data");

                metrics.recordApiError();

                return getExchangeRateFallback(currency, purchaseDate, new IntegrationException("Invalid exchange rate data"));
            }

            metrics.recordLatency(sample);

            return Optional.of(new BigDecimal(data.getExchangeRate()));
        } catch (Exception e) {
            metrics.recordApiError();

            LOGGER.error("Error fetching exchange rate from API: {}", e.getMessage());

            throw e;
        }
    }

    @Cacheable(value = "exchangeRateFallback", key = "#currency + '_' + #purchaseDate")
    public Optional<BigDecimal> getExchangeRateFallback(final String currency, final LocalDate purchaseDate, Throwable throwable) {
        LOGGER.warn("Using fallback for currency: {} on date: {} - Reason: {}", currency, purchaseDate, throwable.getMessage());

        metrics.recordFallbackUsage();

        if (fallbackData.isEmpty()) {
            LOGGER.error("Fallback data not available for currency: {} on date: {}", currency, purchaseDate);

            return Optional.empty();
        }

        final LocalDate maxMonthsAgo = purchaseDate.minusMonths(this.properties.getMaxMonthAgo());

        Optional<BigDecimal> result = fallbackData.stream()
            .filter(data -> data.getRecordDate() != null && data.getExchangeRate() != null && data.getCountryCurrencyDesc() != null)
            .filter(data -> data.getCountryCurrencyDesc().contains(currency))
            .map(data -> {
                try {
                    return new FallbackRecord(LocalDate.parse(data.getRecordDate()), new BigDecimal(data.getExchangeRate()));
                } catch (Exception e) {
                    return null;
                }
            })
            .filter(record -> record != null && !record.date.isBefore(maxMonthsAgo) && !record.date.isAfter(purchaseDate))
            .findFirst()
            .map(record -> record.rate);

        if (result.isEmpty()) {
            LOGGER.error("No fallback exchange rate found for currency: {} on date: {}", currency, purchaseDate);
        }

        return result;
    }

    private record FallbackRecord(LocalDate date, BigDecimal rate) {

    }

}
