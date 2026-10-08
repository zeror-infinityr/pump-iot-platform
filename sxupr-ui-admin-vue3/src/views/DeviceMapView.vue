<template>

  <div class="map-page">

    <div class="page-header">

      <div>
        <h1>
          设备地图
        </h1>

        <p>
          GeoServer + PostGIS + OpenLayers
        </p>
      </div>

    </div>

    <el-card shadow="never">

      <div
          ref="mapElement"
          class="map"
      />

    </el-card>

  </div>

</template>

<script setup lang="ts">

import {
  onBeforeUnmount,
  onMounted,
  ref
} from 'vue'

import {
  useRouter
} from 'vue-router'

import Map from 'ol/Map.js'
import View from 'ol/View.js'

import TileLayer from 'ol/layer/Tile.js'
import VectorLayer from 'ol/layer/Vector.js'

import OSM from 'ol/source/OSM.js'
import VectorSource from 'ol/source/Vector.js'

import GeoJSON from 'ol/format/GeoJSON.js'

import {
  Circle as CircleStyle,
  Fill,
  Stroke,
  Style
} from 'ol/style.js'

import {
  fromLonLat
} from 'ol/proj.js'

import 'ol/ol.css'

const router =
    useRouter()

const mapElement =
    ref<HTMLDivElement | null>(
        null
    )

let map:
    Map | null = null

onMounted(() => {

  const deviceSource =
      new VectorSource({

        url:
            '/geoserver/pump_iot/ows'
            + '?service=WFS'
            + '&version=2.0.0'
            + '&request=GetFeature'
            + '&typeNames=pump_iot:iot_device_location'
            + '&outputFormat=application/json',

        format:
            new GeoJSON({

              dataProjection:
                  'EPSG:4326',

              featureProjection:
                  'EPSG:3857'
            })
      })

  const deviceLayer =
      new VectorLayer({

        source:
        deviceSource,

        style:
            new Style({

              image:
                  new CircleStyle({

                    radius: 8,

                    fill:
                        new Fill(),

                    stroke:
                        new Stroke({
                          width: 2
                        })
                  })
            })
      })

  map =
      new Map({

        target:
            mapElement.value!,

        layers: [

          new TileLayer({
            source:
                new OSM()
          }),

          deviceLayer
        ],

        view:
            new View({

              center:
                  fromLonLat([
                    112.55,
                    37.87
                  ]),

              zoom: 12
            })
      })

  map.on(
      'singleclick',
      event => {

        const feature =
            map?.forEachFeatureAtPixel(
                event.pixel,
                feature => feature
            )

        if (!feature) {
          return
        }

        const deviceId =
            feature.get(
                'device_id'
            )

        if (!deviceId) {
          return
        }

        router.push({
          name:
              'device-detail',

          params: {
            id:
            deviceId
          }
        })
      }
  )
})

onBeforeUnmount(() => {

  if (map) {

    map.setTarget(
        undefined
    )

    map = null
  }
})

</script>

<style scoped>

.map-page {
  width: 100%;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  margin: 0 0 6px;
  font-size: 24px;
}

.page-header p {
  margin: 0;
  color: #909399;
}

.map {
  width: 100%;
  height: 650px;
}

</style>