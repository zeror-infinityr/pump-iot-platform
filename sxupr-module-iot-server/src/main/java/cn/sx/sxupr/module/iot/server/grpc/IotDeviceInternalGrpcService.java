package cn.sx.sxupr.module.iot.server.grpc;

import cn.sx.sxupr.module.iot.api.grpc.GetDeviceAccessInfoRequest;
import cn.sx.sxupr.module.iot.api.grpc.GetDeviceAccessInfoResponse;
import cn.sx.sxupr.module.iot.api.grpc.IotDeviceInternalServiceGrpc;

import cn.sx.sxupr.module.iot.server.dal.dataobject.device.DeviceDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.product.ProductDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.thingmodel.ThingModelVersionDO;

import cn.sx.sxupr.module.iot.server.dal.mapper.device.DeviceMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.product.ProductMapper;
import cn.sx.sxupr.module.iot.server.dal.mapper.thingmodel.ThingModelVersionMapper;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.springframework.stereotype.Component;


@Component
public class IotDeviceInternalGrpcService
        extends IotDeviceInternalServiceGrpc
        .IotDeviceInternalServiceImplBase {

    private final ProductMapper productMapper;

    private final DeviceMapper deviceMapper;

    private final ThingModelVersionMapper
            thingModelVersionMapper;


    public IotDeviceInternalGrpcService(

            ProductMapper productMapper,

            DeviceMapper deviceMapper,

            ThingModelVersionMapper
                    thingModelVersionMapper) {

        this.productMapper =
                productMapper;

        this.deviceMapper =
                deviceMapper;

        this.thingModelVersionMapper =
                thingModelVersionMapper;
    }


    @Override
    public void getDeviceAccessInfo(

            GetDeviceAccessInfoRequest request,

            StreamObserver<
                    GetDeviceAccessInfoResponse
                    > responseObserver) {

        try {

            ProductDO product =
                    productMapper.selectOne(

                            Wrappers
                                    .<ProductDO>lambdaQuery()

                                    .eq(
                                            ProductDO::getProductKey,
                                            request.getProductKey()
                                    )

                                    .last("LIMIT 1")
                    );


            if (product == null) {

                responseObserver.onNext(

                        GetDeviceAccessInfoResponse
                                .newBuilder()

                                .setFound(false)

                                .build()
                );

                responseObserver.onCompleted();

                return;
            }


            DeviceDO device =
                    deviceMapper.selectOne(

                            Wrappers
                                    .<DeviceDO>lambdaQuery()

                                    .eq(
                                            DeviceDO::getProductId,
                                            product.getId()
                                    )

                                    .eq(
                                            DeviceDO::getDeviceName,
                                            request.getDeviceName()
                                    )

                                    .last("LIMIT 1")
                    );


            if (device == null) {

                responseObserver.onNext(

                        GetDeviceAccessInfoResponse
                                .newBuilder()

                                .setFound(false)

                                .build()
                );

                responseObserver.onCompleted();

                return;
            }


            ThingModelVersionDO version =
                    thingModelVersionMapper
                            .selectById(
                                    device
                                            .getThingModelVersionId()
                            );


            if (version == null) {

                throw new IllegalStateException(
                        "设备绑定的物模型版本不存在"
                );
            }


            GetDeviceAccessInfoResponse response =

                    GetDeviceAccessInfoResponse
                            .newBuilder()

                            .setFound(true)

                            .setDeviceId(
                                    device.getId()
                            )

                            .setProductId(
                                    product.getId()
                            )

                            .setThingModelVersionId(
                                    version.getId()
                            )

                            .setThingModelVersion(
                                    version.getVersion()
                            )

                            .build();


            responseObserver.onNext(
                    response
            );

            responseObserver.onCompleted();


        } catch (Exception e) {

            responseObserver.onError(

                    Status.INTERNAL

                            .withDescription(
                                    "查询设备接入信息失败"
                            )

                            .withCause(e)

                            .asRuntimeException()
            );
        }
    }
}