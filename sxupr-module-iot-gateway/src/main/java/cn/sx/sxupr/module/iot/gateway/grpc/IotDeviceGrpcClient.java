package cn.sx.sxupr.module.iot.gateway.grpc;

import cn.sx.sxupr.module.iot.api.grpc.GetDeviceAccessInfoRequest;
import cn.sx.sxupr.module.iot.api.grpc.GetDeviceAccessInfoResponse;
import cn.sx.sxupr.module.iot.api.grpc.IotDeviceInternalServiceGrpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class IotDeviceGrpcClient {

    private final ManagedChannel channel;

    private final IotDeviceInternalServiceGrpc
            .IotDeviceInternalServiceBlockingStub
            blockingStub;

    private final IotGrpcClientProperties properties;


    public IotDeviceGrpcClient(
            IotGrpcClientProperties properties) {

        this.properties =
                properties;

        this.channel =
                ManagedChannelBuilder

                        .forAddress(
                                properties.host(),
                                properties.port()
                        )

                        .usePlaintext()

                        .build();


        this.blockingStub =
                IotDeviceInternalServiceGrpc

                        .newBlockingStub(
                                channel
                        );
    }


    public GetDeviceAccessInfoResponse
    getDeviceAccessInfo(

            String productKey,

            String deviceName) {

        GetDeviceAccessInfoRequest request =

                GetDeviceAccessInfoRequest
                        .newBuilder()

                        .setProductKey(
                                productKey
                        )

                        .setDeviceName(
                                deviceName
                        )

                        .build();


        return blockingStub

                .withDeadlineAfter(
                        properties.deadlineMs(),
                        TimeUnit.MILLISECONDS
                )

                .getDeviceAccessInfo(
                        request
                );
    }


    @PreDestroy
    public void close()
            throws InterruptedException {

        channel.shutdown();

        if (!channel.awaitTermination(
                5,
                TimeUnit.SECONDS)) {

            channel.shutdownNow();
        }
    }
}