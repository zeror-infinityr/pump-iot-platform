package cn.sx.sxupr.simulator.mqtt;

import cn.sx.sxupr.simulator.config.SimulatorProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Component;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.common.MqttSubscription;

import java.nio.charset.StandardCharsets;

@Component
public class SimulatorMqttClient {

    private final SimulatorProperties properties;

    private MqttClient client;

    public SimulatorMqttClient(
            SimulatorProperties properties) {

        this.properties = properties;
    }

    @PostConstruct
    public void connect() throws MqttException {

        String clientId =
                "simulator-" + properties.deviceName();

        client = new MqttClient(
                properties.brokerUri(),
                clientId
        );

        MqttConnectionOptions options =
                new MqttConnectionOptions();

        options.setCleanStart(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);

        client.connect(options);

        System.out.println(
                "[MQTT] 已连接 Broker: "
                        + properties.brokerUri()
        );
    }

    public void publish(
            String topic,
            String payload) throws MqttException {

        if (!client.isConnected()) {
            throw new IllegalStateException(
                    "MQTT 客户端当前未连接"
            );
        }

        MqttMessage message =
                new MqttMessage(
                        payload.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        message.setQos(1);
        message.setRetained(false);

        client.publish(topic, message);
    }

    public void subscribe(
            String topic,
            IMqttMessageListener listener)
            throws MqttException {

        MqttSubscription subscription =
                new MqttSubscription(
                        topic,
                        1
                );

        client.subscribe(
                new MqttSubscription[]{
                        subscription
                },
                new IMqttMessageListener[]{
                        listener
                }
        );
    }

    @PreDestroy
    public void disconnect() throws MqttException {

        if (client != null && client.isConnected()) {
            client.disconnect();
        }

        if (client != null) {
            client.close();
        }
    }
}