package costa.paltrinieri.felipe.infrastructure.throttling;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("rate.limit")
public class RateLimitProperties {

    @NotNull
    @Positive
    private Integer capacity;

    @NotNull
    @Positive
    private Integer refillTokens;

    @NotNull
    @Positive
    private Integer refillDurationMinutes;

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getRefillTokens() {
        return refillTokens;
    }

    public void setRefillTokens(Integer refillTokens) {
        this.refillTokens = refillTokens;
    }

    public Integer getRefillDurationMinutes() {
        return refillDurationMinutes;
    }

    public void setRefillDurationMinutes(Integer refillDurationMinutes) {
        this.refillDurationMinutes = refillDurationMinutes;
    }

}
