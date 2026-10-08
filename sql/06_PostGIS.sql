CREATE EXTENSION IF NOT EXISTS postgis;
CREATE TABLE iot_device_location (

                                     device_id BIGINT NOT NULL,

                                     product_id BIGINT NOT NULL,

                                     device_name VARCHAR(64) NOT NULL,

                                     longitude DOUBLE PRECISION NOT NULL,

                                     latitude DOUBLE PRECISION NOT NULL,

                                     geom geometry(Point, 4326) NOT NULL,

                                     geog geography(Point, 4326) NOT NULL,

                                     update_time TIMESTAMP(3)
                                         NOT NULL
                                         DEFAULT CURRENT_TIMESTAMP,

                                     PRIMARY KEY (device_id)
);

CREATE INDEX idx_device_location_geom
    ON iot_device_location
    USING GIST (geom);

CREATE INDEX idx_device_location_geog
    ON iot_device_location
    USING GIST (geog);

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
           1,
           1,
           'pump-001',
           112.5500,
           37.8700,

           ST_SetSRID(
                   ST_MakePoint(
                           112.5500,
                           37.8700
                   ),
                   4326
           ),

           ST_SetSRID(
                   ST_MakePoint(
                           112.5500,
                           37.8700
                   ),
                   4326
           )::geography
       );