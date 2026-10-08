package cn.sx.sxupr.module.iot.server.infra.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class AlarmCounterStore {

    private static final String PREFIX =
            "iot:alarm:counter:";

    private final StringRedisTemplate redisTemplate;

    public AlarmCounterStore(
            StringRedisTemplate redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    public long incrementHighWater(
            Long deviceId) {

        String key =
                buildHighWaterKey(deviceId);

        Long count =
                redisTemplate
                        .opsForValue()
                        .increment(key);

        redisTemplate.expire(
                key,
                Duration.ofMinutes(10)
        );

        return count == null
                ? 0
                : count;
    }

    public void resetHighWater(
            Long deviceId) {

        redisTemplate.delete(
                buildHighWaterKey(deviceId)
        );
    }

    private String buildHighWaterKey(
            Long deviceId) {

        return PREFIX
                + "{"
                + deviceId
                + "}:HIGH_WATER";
    }
}