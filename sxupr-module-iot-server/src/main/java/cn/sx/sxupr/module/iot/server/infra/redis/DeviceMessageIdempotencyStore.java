package cn.sx.sxupr.module.iot.server.infra.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class DeviceMessageIdempotencyStore {

    private static final String KEY_PREFIX =
            "iot:dedup:";

    private static final Duration TTL =
            Duration.ofHours(24);

    private final StringRedisTemplate redisTemplate;

    public DeviceMessageIdempotencyStore(
            StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public boolean isProcessed(
            String productKey,
            String deviceName,
            String requestId) {

        String key =
                buildKey(
                        productKey,
                        deviceName,
                        requestId
                );

        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key)
        );
    }

    public void markProcessed(
            String productKey,
            String deviceName,
            String requestId) {

        String key =
                buildKey(
                        productKey,
                        deviceName,
                        requestId
                );

        redisTemplate.opsForValue()
                .set(
                        key,
                        "DONE",
                        TTL
                );
    }

    private String buildKey(
            String productKey,
            String deviceName,
            String requestId) {

        return KEY_PREFIX
                + "{"
                + productKey
                + ":"
                + deviceName
                + "}:"
                + requestId;
    }
}