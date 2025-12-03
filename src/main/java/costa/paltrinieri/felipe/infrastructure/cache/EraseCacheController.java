package costa.paltrinieri.felipe.infrastructure.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/cache")
public class EraseCacheController {

    private final CacheManager cacheManager;

    public EraseCacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCache() {
        cacheManager.getCacheNames()
            .forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/ttl")
    public ResponseEntity<Void> updateCacheTtl(@RequestParam Long minutes) {
        if (cacheManager instanceof CaffeineCacheManager caffeineCacheManager) {
            caffeineCacheManager.setCacheSpecification("expireAfterWrite=" + minutes + "m");

            clearCache();
        }

        return ResponseEntity.noContent().build();
    }

}
