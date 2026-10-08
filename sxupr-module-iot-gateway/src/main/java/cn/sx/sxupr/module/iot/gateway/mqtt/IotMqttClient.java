package cn.sx.sxupr.module.iot.gateway.mqtt;

import cn.sx.sxupr.module.iot.core.message.IotDeviceCommandReplyMessage;
import cn.sx.sxupr.module.iot.core.message.IotDevicePropertyMessage;
import cn.sx.sxupr.module.iot.core.topic.IotDeviceIdentity;
import cn.sx.sxupr.module.iot.core.topic.IotTopics;
import cn.sx.sxupr.module.iot.gateway.config.IotGatewayProperties;
import cn.sx.sxupr.module.iot.gateway.grpc.IotDeviceGrpcClient;
import cn.sx.sxupr.module.iot.gateway.kafka.DeviceCommandReplyKafkaProducer;
import cn.sx.sxupr.module.iot.gateway.kafka.DeviceMessageKafkaProducer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.mqttv5.client.IMqttMessageListener;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.MqttSubscription;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import cn.sx.sxupr.module.iot.api.grpc.GetDeviceAccessInfoResponse;
import cn.sx.sxupr.module.iot.gateway.grpc.IotDeviceGrpcClient;

import java.nio.charset.StandardCharsets;

@Component
public class IotMqttClient {

    private final IotGatewayProperties properties;
    private final JsonMapper jsonMapper;
    private final DeviceMessageKafkaProducer propertyProducer;
    private final DeviceCommandReplyKafkaProducer replyProducer;
    private final IotDeviceGrpcClient deviceGrpcClient;

    private MqttClient client;

    public IotMqttClient(
            IotGatewayProperties properties,
            JsonMapper jsonMapper,
            DeviceMessageKafkaProducer propertyProducer,
            DeviceCommandReplyKafkaProducer replyProducer,
            IotDeviceGrpcClient deviceGrpcClient) {

        this.properties = properties;
        this.jsonMapper = jsonMapper;
        this.propertyProducer = propertyProducer;
        this.replyProducer = replyProducer;
        this.deviceGrpcClient = deviceGrpcClient;
    }

    @PostConstruct
    public void start()
            throws MqttException {

        client = new MqttClient(
                properties.brokerUri(),
                "pump-iot-gateway"
        );

        MqttConnectionOptions options =
                new MqttConnectionOptions();

        options.setCleanStart(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);

        client.connect(options);

        subscribe(
                IotTopics.propertyPostFilter(),
                this::handleProperty
        );

        subscribe(
                IotTopics.commandReplyFilter(),
                this::handleCommandReply
        );

        System.out.println(
                "[GATEWAY] MQTT subscriptions ready"
        );
    }

    private void subscribe(
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

//    private void handleProperty(
//            String topic,
//            MqttMessage mqttMessage) {
//
//        try {
//
//            String payload =
//                    new String(
//                            mqttMessage.getPayload(),
//                            StandardCharsets.UTF_8
//                    );
//
//            IotDeviceIdentity identity =
//                    IotTopics.parsePropertyPost(
//                            topic
//                    );
//
//            IotDevicePropertyMessage message =
//                    jsonMapper.readValue(
//                            payload,
//                            IotDevicePropertyMessage.class
//                    );
//
//            if (!identity.productKey()
//                    .equals(message.productKey())
//                    ||
//                    !identity.deviceName()
//                            .equals(message.deviceName())) {
//
//                throw new IllegalArgumentException(
//                        "属性消息 Topic 与 Payload 身份不一致"
//                );
//            }
//
//            propertyProducer.send(message);
//
//        } catch (Exception e) {
//
//            System.err.println(
//                    "[GATEWAY] property rejected: "
//                            + e.getMessage()
//            );
//        }
//    }

    private void handleProperty(
            String topic,
            MqttMessage mqttMessage) {

        try {

            String payload =
                    new String(
                            mqttMessage.getPayload(),
                            StandardCharsets.UTF_8
                    );


            IotDeviceIdentity identity =
                    IotTopics.parsePropertyPost(
                            topic
                    );


            IotDevicePropertyMessage message =
                    jsonMapper.readValue(
                            payload,
                            IotDevicePropertyMessage.class
                    );


            if (!identity.productKey()
                    .equals(message.productKey())
                    ||
                    !identity.deviceName()
                            .equals(message.deviceName())) {

                throw new IllegalArgumentException(
                        "属性消息 Topic 与 Payload 身份不一致"
                );
            }


            GetDeviceAccessInfoResponse accessInfo =

                    deviceGrpcClient
                            .getDeviceAccessInfo(

                                    message.productKey(),

                                    message.deviceName()
                            );


            if (!accessInfo.getFound()) {

                throw new IllegalArgumentException(
                        "设备未注册: "
                                + message.productKey()
                                + "/"
                                + message.deviceName()
                );
            }


            System.out.println(
                    "[GATEWAY-GRPC] device accepted"
                            + ", deviceId="
                            + accessInfo.getDeviceId()
                            + ", modelVersion="
                            + accessInfo
                            .getThingModelVersion()
            );


            propertyProducer.send(
                    message
            );


        } catch (Exception e) {

            System.err.println(
                    "[GATEWAY] property rejected: "
                            + e.getMessage()
            );
        }
    }

    private void handleCommandReply(
            String topic,
            MqttMessage mqttMessage) {

        try {

            String payload =
                    new String(
                            mqttMessage.getPayload(),
                            StandardCharsets.UTF_8
                    );

            IotDeviceIdentity identity =
                    IotTopics.parseCommandReply(
                            topic
                    );

            IotDeviceCommandReplyMessage reply =
                    jsonMapper.readValue(
                            payload,
                            IotDeviceCommandReplyMessage.class
                    );

            if (!identity.productKey()
                    .equals(reply.productKey())
                    ||
                    !identity.deviceName()
                            .equals(reply.deviceName())) {

                throw new IllegalArgumentException(
                        "命令回执 Topic 与 Payload 身份不一致"
                );
            }

            replyProducer.send(reply);

        } catch (Exception e) {

            System.err.println(
                    "[GATEWAY] command reply rejected: "
                            + e.getMessage()
            );
        }
    }

    public void publish(
            String topic,
            String payload,
            int qos)
            throws MqttException {

        MqttMessage message =
                new MqttMessage(
                        payload.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        message.setQos(qos);
        message.setRetained(false);

        client.publish(
                topic,
                message
        );
    }

    @PreDestroy
    public void stop()
            throws MqttException {

        if (client != null
                && client.isConnected()) {

            client.disconnect();
        }

        if (client != null) {
            client.close();
        }
    }
}