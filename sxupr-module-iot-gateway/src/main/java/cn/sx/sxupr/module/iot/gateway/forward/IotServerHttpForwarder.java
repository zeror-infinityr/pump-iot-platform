package cn.sx.sxupr.module.iot.gateway.forward;

import cn.sx.sxupr.module.iot.core.message.IotDevicePropertyMessage;
import cn.sx.sxupr.module.iot.gateway.config.IotGatewayProperties;
import cn.sx.sxupr.module.iot.gateway.dto.DevicePropertyForwardRequest;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class IotServerHttpForwarder {

    private final IotGatewayProperties properties;

    private final JsonMapper jsonMapper;

    private final HttpClient httpClient;

    public IotServerHttpForwarder(
            IotGatewayProperties properties,
            JsonMapper jsonMapper) {

        this.properties = properties;
        this.jsonMapper = jsonMapper;

        this.httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(
                                Duration.ofSeconds(3)
                        )
                        .build();
    }

    public void forward(
            IotDevicePropertyMessage message)
            throws Exception {

        DevicePropertyForwardRequest requestBody =
                new DevicePropertyForwardRequest(
                        message.requestId(),
                        message.productKey(),
                        message.deviceName(),
                        message.deviceTimestamp(),
                        message.properties()
                );

        String json =
                jsonMapper.writeValueAsString(
                        requestBody
                );

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        properties.serverBaseUrl()
                                                + "/api/iot/device/property/post"
                                )
                        )
                        .timeout(
                                Duration.ofSeconds(5)
                        )
                        .header(
                                "Content-Type",
                                "application/json"
                        )
                        .POST(
                                HttpRequest.BodyPublishers
                                        .ofString(json)
                        )
                        .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() < 200
                || response.statusCode() >= 300) {

            throw new IllegalStateException(
                    "iot-server 拒绝消息，HTTP "
                            + response.statusCode()
                            + ", body="
                            + response.body()
            );
        }

        System.out.println(
                "[GATEWAY] Server accepted requestId="
                        + message.requestId()
        );
    }
}