package cn.sx.sxupr.module.iot.server.infra.redis;

import cn.sx.sxupr.module.iot.core.state.IotDeviceLatestState;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Repository
public class DeviceLatestStateRedisStore {

    private static final String KEY_PREFIX =
            "iot:device:";

    private final StringRedisTemplate redisTemplate;

    private final JsonMapper jsonMapper;

    public DeviceLatestStateRedisStore(
            StringRedisTemplate redisTemplate,
            JsonMapper jsonMapper) {

        this.redisTemplate = redisTemplate;
        this.jsonMapper = jsonMapper;
    }

    public void save(IotDeviceLatestState state) {

        String key = buildKey(state.deviceId());

        try {

            String json =
                    jsonMapper.writeValueAsString(state);

            redisTemplate.opsForValue()
                    .set(key, json);

        } catch (JacksonException e) {

            throw new IllegalStateException(
                    "设备最新状态序列化失败",
                    e
            );
        }
    }

    public IotDeviceLatestState get(Long deviceId) {

        String key = buildKey(deviceId);

        String json =
                redisTemplate.opsForValue()
                        .get(key);

        if (json == null) {
            return null;
        }

        try {

            return jsonMapper.readValue(
                    json,
                    IotDeviceLatestState.class
            );

        } catch (JacksonException e) {

            throw new IllegalStateException(
                    "设备最新状态反序列化失败",
                    e
            );
        }
    }

    private String buildKey(Long deviceId) {

        return KEY_PREFIX
                + "{"
                + deviceId
                + "}:latest";
    }
}