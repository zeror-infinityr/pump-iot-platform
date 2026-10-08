package cn.sx.sxupr.module.iot.server.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;


@Component
public class GrpcServerLifecycle {

    private final IotDeviceInternalGrpcService
            deviceService;

    private final int port;

    private Server server;


    public GrpcServerLifecycle(

            IotDeviceInternalGrpcService deviceService,

            @Value("${iot.grpc.server.port:19090}")
            int port) {

        this.deviceService =
                deviceService;

        this.port =
                port;
    }


    @PostConstruct
    public void start()
            throws Exception {

        server =
                ServerBuilder
                        .forPort(port)

                        .addService(
                                deviceService
                        )

                        .build()

                        .start();


        System.out.println(
                "[GRPC-SERVER] started on port "
                        + port
        );
    }


    @PreDestroy
    public void stop()
            throws InterruptedException {

        if (server == null) {
            return;
        }

        server.shutdown();

        if (!server.awaitTermination(
                5,
                TimeUnit.SECONDS)) {

            server.shutdownNow();
        }
    }
}