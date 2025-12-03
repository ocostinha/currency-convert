package costa.paltrinieri.felipe.infrastructure.throttling;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();
    private final RateLimitProperties properties;

    public RateLimitingFilter(final RateLimitProperties properties) {
        this.properties = properties;
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain)
    throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String clientId = getClientId(httpRequest);

        Bucket bucket = cache.computeIfAbsent(clientId, k -> createBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"error\":\"Too many requests\"}");
        }
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
            .capacity(properties.getCapacity())
            .refillIntervally(properties.getRefillTokens(), Duration.ofMinutes(properties.getRefillDurationMinutes()))
            .build();

        return Bucket.builder().addLimit(limit).build();
    }

    private String getClientId(final HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();

        return clientIp != null ? clientIp : "unknown";
    }

}
