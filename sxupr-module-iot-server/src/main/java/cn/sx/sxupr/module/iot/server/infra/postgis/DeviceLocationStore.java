package cn.sx.sxupr.module.iot.server.infra.postgis;

import cn.sx.sxupr.module.iot.server.controller.vo.gis.NearbyDeviceVO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DeviceLocationStore {

    private final JdbcTemplate jdbcTemplate;

    public DeviceLocationStore(
            PostgisClient postgisClient) {

        this.jdbcTemplate =
                postgisClient.jdbcTemplate();
    }

    public void upsert(
            Long deviceId,
            Long productId,
            String deviceName,
            double longitude,
            double latitude) {

        String sql =
                """
                INSERT INTO iot_device_location (
                    device_id,
                    product_id,
                    device_name,
                    longitude,
                    latitude,
                    geom,
                    geog
                )
                VALUES (
                    ?, ?, ?, ?, ?,
                    ST_SetSRID(
                        ST_MakePoint(?, ?),
                        4326
                    ),
                    ST_SetSRID(
                        ST_MakePoint(?, ?),
                        4326
                    )::geography
                )
                ON CONFLICT (device_id)
                DO UPDATE SET
                    product_id = EXCLUDED.product_id,
                    device_name = EXCLUDED.device_name,
                    longitude = EXCLUDED.longitude,
                    latitude = EXCLUDED.latitude,
                    geom = EXCLUDED.geom,
                    geog = EXCLUDED.geog,
                    update_time = CURRENT_TIMESTAMP
                """;

        jdbcTemplate.update(
                sql,
                deviceId,
                productId,
                deviceName,
                longitude,
                latitude,

                longitude,
                latitude,

                longitude,
                latitude
        );
    }

    public List<NearbyDeviceVO> queryNearby(
            double longitude,
            double latitude,
            double radiusMeters) {

        String sql =
                """
                SELECT
                    device_id,
                    product_id,
                    device_name,
                    longitude,
                    latitude,

                    ST_Distance(
                        geog,
                        ST_SetSRID(
                            ST_MakePoint(?, ?),
                            4326
                        )::geography
                    ) AS distance_meters

                FROM iot_device_location

                WHERE ST_DWithin(
                    geog,
                    ST_SetSRID(
                        ST_MakePoint(?, ?),
                        4326
                    )::geography,
                    ?
                )

                ORDER BY distance_meters
                """;

        return jdbcTemplate.query(
                sql,

                (rs, rowNum) ->
                        new NearbyDeviceVO(
                                rs.getLong(
                                        "device_id"
                                ),

                                rs.getLong(
                                        "product_id"
                                ),

                                rs.getString(
                                        "device_name"
                                ),

                                rs.getDouble(
                                        "longitude"
                                ),

                                rs.getDouble(
                                        "latitude"
                                ),

                                rs.getDouble(
                                        "distance_meters"
                                )
                        ),

                longitude,
                latitude,

                longitude,
                latitude,

                radiusMeters
        );
    }
}