package cn.sx.sxupr.module.iot.server.infra.tdengine;

import cn.sx.sxupr.module.iot.core.message.IotDevicePropertyMessage;
import cn.sx.sxupr.module.iot.server.dal.dataobject.device.DeviceDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.product.ProductDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.thingmodel.ThingModelPropertyDO;
import cn.sx.sxupr.module.iot.server.dal.dataobject.thingmodel.ThingModelVersionDO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import cn.sx.sxupr.module.iot.server.controller.vo.device.DevicePropertyHistoryPointVO;
import java.util.List;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class DevicePropertyHistoryStore {

    private final JdbcTemplate jdbcTemplate;

    public DevicePropertyHistoryStore(
            TdengineClient tdengineClient) {

        this.jdbcTemplate =
                tdengineClient.jdbcTemplate();
    }

    public void save(
            ProductDO product,
            DeviceDO device,
            ThingModelVersionDO version,
            List<ThingModelPropertyDO> definitions,
            IotDevicePropertyMessage message,
            Instant serverReceiveInstant) {

        String tableName =
                buildTableName(device.getId());

        ensureSubTable(
                tableName,
                device,
                product
        );

        Map<String, ThingModelPropertyDO> definitionMap =
                definitions.stream()
                        .collect(
                                Collectors.toMap(
                                        ThingModelPropertyDO::getIdentifier,
                                        Function.identity()
                                )
                        );

        for (Map.Entry<String, Object> entry
                : message.properties().entrySet()) {

            ThingModelPropertyDO definition =
                    definitionMap.get(
                            entry.getKey()
                    );

            if (definition == null) {
                throw new IllegalStateException(
                        "历史存储时找不到属性定义: "
                                + entry.getKey()
                );
            }

            insertProperty(
                    tableName,
                    version,
                    message,
                    serverReceiveInstant,
                    definition,
                    entry.getValue()
            );
        }
    }

    private void ensureSubTable(
            String tableName,
            DeviceDO device,
            ProductDO product) {

        String sql =
                "CREATE TABLE IF NOT EXISTS pump_iot_ts."
                        + tableName
                        + " USING pump_iot_ts.device_property_history "
                        + "TAGS ("
                        + device.getId()
                        + ", "
                        + product.getId()
                        + ")";

        jdbcTemplate.execute(sql);
    }

    private void insertProperty(
            String tableName,
            ThingModelVersionDO version,
            IotDevicePropertyMessage message,
            Instant serverReceiveInstant,
            ThingModelPropertyDO definition,
            Object value) {

        Object decimalValue = null;
        Object integerValue = null;
        Object booleanValue = null;
        Object stringValue = null;

        switch (definition.getDataType()) {

            case "DECIMAL" ->
                    decimalValue =
                            new BigDecimal(
                                    value.toString()
                            ).doubleValue();

            case "INTEGER" ->
                    integerValue =
                            new BigDecimal(
                                    value.toString()
                            ).longValueExact();

            case "BOOLEAN" ->
                    booleanValue = value;

            case "STRING" ->
                    stringValue = value.toString();

            default ->
                    throw new IllegalStateException(
                            "不支持的历史属性类型: "
                                    + definition.getDataType()
                    );
        }

        String sequenceKey =
                message.requestId()
                        + ":"
                        + definition.getIdentifier();

        String sql =
                "INSERT INTO pump_iot_ts."
                        + tableName
                        + " ("
                        + "ts, "
                        + "sequence_key, "
                        + "request_id, "
                        + "server_receive_ts, "
                        + "thing_model_version, "
                        + "identifier, "
                        + "data_type, "
                        + "decimal_value, "
                        + "integer_value, "
                        + "boolean_value, "
                        + "string_value"
                        + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                new Timestamp(
                        message.deviceTimestamp()
                ),
                sequenceKey,
                message.requestId(),
                Timestamp.from(
                        serverReceiveInstant
                ),
                version.getVersion(),
                definition.getIdentifier(),
                definition.getDataType(),
                decimalValue,
                integerValue,
                booleanValue,
                stringValue
        );
    }

    private String buildTableName(
            Long deviceId) {

        return "d_" + deviceId;
    }

    public List<DevicePropertyHistoryPointVO> query(
            Long deviceId,
            String identifier,
            int limit) {

        int safeLimit =
                Math.max(
                        1,
                        Math.min(limit, 1000)
                );

        String sql =
                """
                SELECT
                    ts,
                    server_receive_ts,
                    request_id,
                    identifier,
                    data_type,
                    decimal_value,
                    integer_value,
                    boolean_value,
                    string_value
                FROM pump_iot_ts.device_property_history
                WHERE device_id = ?
                  AND identifier = ?
                ORDER BY ts DESC
                LIMIT %d
                """.formatted(safeLimit);

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> {

                    String dataType =
                            rs.getString(
                                    "data_type"
                            );

                    Object value =
                            switch (dataType) {

                                case "DECIMAL" ->
                                        rs.getDouble(
                                                "decimal_value"
                                        );

                                case "INTEGER" ->
                                        rs.getLong(
                                                "integer_value"
                                        );

                                case "BOOLEAN" ->
                                        rs.getBoolean(
                                                "boolean_value"
                                        );

                                case "STRING" ->
                                        rs.getString(
                                                "string_value"
                                        );

                                default -> null;
                            };

                    return new DevicePropertyHistoryPointVO(
                            rs.getTimestamp("ts")
                                    .getTime(),

                            rs.getTimestamp(
                                            "server_receive_ts"
                                    )
                                    .getTime(),

                            rs.getString(
                                    "request_id"
                            ),

                            rs.getString(
                                    "identifier"
                            ),

                            dataType,

                            value
                    );
                },
                deviceId,
                identifier
        );
    }
}