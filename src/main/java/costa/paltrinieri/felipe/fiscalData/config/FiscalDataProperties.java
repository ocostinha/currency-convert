package costa.paltrinieri.felipe.fiscalData.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("fiscal.data")
public class FiscalDataProperties {

    @NotBlank
    private String baseUrl;

    @NotBlank
    private String ratesOfExchangePath;

    @NotNull
    private Integer maxMonthAgo;

    @NotNull
    private Integer connectTimeoutMs = 5000;

    @NotNull
    private Integer readTimeoutMs = 10000;

    @NotNull
    private Integer maxRetries = 3;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getRatesOfExchangePath() {
        return ratesOfExchangePath;
    }

    public void setRatesOfExchangePath(String ratesOfExchangePath) {
        this.ratesOfExchangePath = ratesOfExchangePath;
    }

    public Integer getMaxMonthAgo() {
        return maxMonthAgo;
    }

    public void setMaxMonthAgo(Integer maxMonthAgo) {
        this.maxMonthAgo = maxMonthAgo;
    }

    public Integer getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(Integer connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public Integer getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(Integer readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

}
