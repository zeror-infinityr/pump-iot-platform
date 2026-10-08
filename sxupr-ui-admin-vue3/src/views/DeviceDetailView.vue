<template>
  <div class="device-page">

    <div class="page-header">

      <div class="header-left">

        <el-button
            text
            @click="backToList"
        >
          ← 返回设备列表
        </el-button>

        <div>
          <h1>
            泵站设备监测
          </h1>

          <p v-if="device">
            {{ device.deviceName }}
            ·
            {{ device.productKey }}
          </p>
        </div>

      </div>

      <el-tag
          v-if="device"
          :type="
      device.onlineStatus === 'ONLINE'
        ? 'success'
        : 'danger'
    "
          size="large"
      >
        {{ device.onlineStatus }}
      </el-tag>

    </div>

    <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        show-icon
        :closable="false"
        class="error-alert"
    />

    <el-row
        :gutter="20"
        v-loading="loading"
    >

      <el-col
          :xs="24"
          :sm="12"
          :lg="8"
      >
        <el-card shadow="hover">
          <div class="metric-title">
            电机温度
          </div>

          <div class="metric-value">
            {{ displayValue('motorTemperature') }}

            <span class="metric-unit">
              ℃
            </span>
          </div>
        </el-card>
      </el-col>

      <el-col
          :xs="24"
          :sm="12"
          :lg="8"
      >
        <el-card shadow="hover">
          <div class="metric-title">
            集水井水位
          </div>

          <div class="metric-value">
            {{ displayValue('waterLevel') }}

            <span class="metric-unit">
              m
            </span>
          </div>
        </el-card>
      </el-col>

      <el-col
          :xs="24"
          :sm="12"
          :lg="8"
      >
        <el-card shadow="hover">
          <div class="metric-title">
            水泵运行状态
          </div>

          <div class="metric-value">
            <el-tag
                v-if="latest"
                :type="
                latest.properties.pumpRunning
                  ? 'success'
                  : 'info'
              "
                size="large"
            >
              {{
                latest.properties.pumpRunning
                    ? '运行'
                    : '停止'
              }}
            </el-tag>

            <span v-else>
              -
            </span>
          </div>
        </el-card>
      </el-col>

    </el-row>

    <el-card
        class="command-card"
        shadow="never"
    >
      <template #header>
        设备控制
      </template>

      <div class="command-form">

    <span>
      采样间隔
    </span>

        <el-input-number
            v-model="samplingIntervalSeconds"
            :min="1"
            :max="3600"
        />

        <span>
      秒
    </span>

        <el-button
            type="primary"
            :loading="commandSubmitting"
            @click="submitSamplingInterval"
        >
          下发命令
        </el-button>

      </div>
    </el-card>

    <el-card
        class="command-card"
        shadow="never"
    >
      <template #header>
        命令记录
      </template>

      <el-table
          :data="commands"
          stripe
      >

        <el-table-column
            prop="commandType"
            label="命令"
            min-width="190"
        />

        <el-table-column
            label="状态"
            width="160"
        >
          <template #default="{ row }">

            <el-tag
                :type="
            row.status === 'DEVICE_SUCCESS'
              ? 'success'
              : row.status === 'DEVICE_FAILURE'
                || row.status === 'TIMEOUT'
                ? 'danger'
                : 'warning'
          "
            >
              {{ row.status }}
            </el-tag>

          </template>
        </el-table-column>

        <el-table-column
            prop="replyMessage"
            label="设备回执"
            min-width="220"
        />

        <el-table-column
            prop="createTime"
            label="提交时间"
            min-width="180"
        />

        <el-table-column
            prop="replyTime"
            label="回执时间"
            min-width="180"
        />

      </el-table>
    </el-card>

    <el-card
        class="info-card"
        shadow="never"
    >
      <template #header>
        设备信息
      </template>

      <el-descriptions
          v-if="device"
          :column="3"
          border
      >
        <el-descriptions-item label="设备 ID">
          {{ device.id }}
        </el-descriptions-item>

        <el-descriptions-item label="设备名称">
          {{ device.deviceName }}
        </el-descriptions-item>

        <el-descriptions-item label="物模型版本">
          V{{ device.thingModelVersion }}
        </el-descriptions-item>

        <el-descriptions-item label="Product Key">
          {{ device.productKey }}
        </el-descriptions-item>

        <el-descriptions-item label="Device Key">
          {{ device.deviceKey }}
        </el-descriptions-item>

        <el-descriptions-item label="最后上报">
          {{ formatDateTime(device.lastReportTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card
        class="alarm-card"
        shadow="never"
    >
      <template #header>
        <div class="chart-header">

      <span>
        设备告警
      </span>

          <el-tag
              v-if="
          alarms.some(
            alarm =>
              alarm.status === 'ACTIVE'
          )
        "
              type="danger"
          >
            存在活动告警
          </el-tag>

        </div>
      </template>

      <el-table
          v-if="alarms.length > 0"
          :data="alarms"
          stripe
      >

        <el-table-column
            prop="alarmType"
            label="告警类型"
        />

        <el-table-column
            label="状态"
            width="120"
        >
          <template #default="{ row }">

            <el-tag
                :type="
            row.status === 'ACTIVE'
              ? 'danger'
              : 'success'
          "
            >
              {{ row.status }}
            </el-tag>

          </template>
        </el-table-column>

        <el-table-column
            prop="triggerValue"
            label="当前/最近值"
        />

        <el-table-column
            prop="thresholdValue"
            label="阈值"
        />

        <el-table-column
            prop="firstTriggerTime"
            label="首次触发"
            min-width="180"
        />

        <el-table-column
            prop="recoverTime"
            label="恢复时间"
            min-width="180"
        />

      </el-table>

      <el-empty
          v-else
          description="当前暂无告警记录"
      />

    </el-card>

    <el-card
        class="chart-card"
        shadow="never"
    >
      <template #header>
        <div class="chart-header">
          <span>
            水位历史
          </span>

          <span class="chart-subtitle">
            最近 {{ historyLimit }} 个采样点
          </span>
        </div>
      </template>

      <div
          ref="waterLevelChartRef"
          class="chart"
      />
    </el-card>

    <el-card
        v-if="latest"
        class="message-card"
        shadow="never"
    >
      <template #header>
        最近一条设备消息
      </template>

      <el-descriptions
          :column="2"
          border
      >
        <el-descriptions-item label="Request ID">
          {{ latest.requestId }}
        </el-descriptions-item>

        <el-descriptions-item label="设备时间">
          {{
            formatTimestamp(
                latest.deviceTimestamp
            )
          }}
        </el-descriptions-item>

        <el-descriptions-item label="服务器接收时间">
          {{
            formatTimestamp(
                latest.serverReceiveTimestamp
            )
          }}
        </el-descriptions-item>

        <el-descriptions-item label="物模型版本">
          V{{ latest.thingModelVersion }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

  </div>
</template>

<script setup lang="ts">

import {
  nextTick,
  onBeforeUnmount,
  onMounted,
  ref
} from 'vue'

import { useRoute,useRouter } from 'vue-router'

import * as echarts from 'echarts'

import type {
  EChartsType
} from 'echarts'

import {
  ElMessage
} from 'element-plus'

import {
  getDevice,
  getDeviceHistory,
  getLatestProperties,
  getDeviceAlarms,
  getDeviceCommands,
  setSamplingInterval
} from '../api/device'

import type {
  Alarm,
  Device,
  DeviceLatestState,
  DevicePropertyHistoryPoint,
  DeviceCommand
} from '../types/iot'

const route = useRoute()
const router = useRouter()

const samplingIntervalSeconds =
    ref(5)

const commandSubmitting =
    ref(false)

const commands =
    ref<DeviceCommand[]>([])

const deviceId =
    Number(route.params.id)

const loading =
    ref(false)

const errorMessage =
    ref('')

const device =
    ref<Device | null>(null)

const latest =
    ref<DeviceLatestState | null>(null)

const waterLevelHistory =
    ref<DevicePropertyHistoryPoint[]>([])

const historyLimit = 100

const waterLevelChartRef =
    ref<HTMLDivElement | null>(null)

const alarms =
    ref<Alarm[]>([])

let waterLevelChart:
    EChartsType | null = null

let refreshTimer:
    number | null = null

async function loadCommands() {

  commands.value =
      await getDeviceCommands(
          deviceId
      )
}

async function loadAlarms() {

  alarms.value =
      await getDeviceAlarms(
          deviceId
      )
}

async function loadDevice() {

  device.value =
      await getDevice(deviceId)
}

async function loadLatest() {

  latest.value =
      await getLatestProperties(deviceId)
}

async function loadWaterLevelHistory() {

  waterLevelHistory.value =
      await getDeviceHistory(
          deviceId,
          'waterLevel',
          historyLimit
      )

  renderWaterLevelChart()
}

async function initialLoad() {

  loading.value = true

  errorMessage.value = ''

  try {

    await Promise.all([
      loadDevice(),
      loadLatest(),
      loadWaterLevelHistory(),
      loadAlarms(),
      loadCommands()
    ])

  } catch (error) {

    console.error(error)

    errorMessage.value =
        '设备数据加载失败，请检查后端服务和数据链路'

  } finally {

    loading.value = false
  }
}

async function refreshRealtimeData() {

  try {

    await Promise.all([
      loadDevice(),
      loadLatest(),
      loadWaterLevelHistory(),
      loadAlarms(),
      loadCommands()
    ])

  } catch (error) {

    console.error(
        '刷新设备数据失败',
        error
    )
  }
}

async function submitSamplingInterval() {

  commandSubmitting.value = true

  try {

    await setSamplingInterval(
        deviceId,
        samplingIntervalSeconds.value
    )

    ElMessage.success(
        '采样间隔命令已提交'
    )

    await loadCommands()

  } catch (error) {

    console.error(error)

    ElMessage.error(
        '命令发送失败'
    )

  } finally {

    commandSubmitting.value = false
  }
}

function displayValue(
    identifier: string
) {

  const value =
      latest.value
          ?.properties[identifier]

  if (value === undefined
      || value === null) {

    return '-'
  }

  return value
}

function renderWaterLevelChart() {

  if (!waterLevelChartRef.value) {
    return
  }

  if (!waterLevelChart) {

    waterLevelChart =
        echarts.init(
            waterLevelChartRef.value
        )
  }

  /*
   * 后端历史接口是：
   *
   * ORDER BY ts DESC
   *
   * 也就是说返回顺序：
   *
   * 最新 → 最旧
   *
   * 但是图表通常希望：
   *
   * 最旧 → 最新
   *
   * 所以前端这里 reverse。
   */
  const points =
      [...waterLevelHistory.value]
          .reverse()

  waterLevelChart.setOption({

    tooltip: {
      trigger: 'axis'
    },

    grid: {
      left: 45,
      right: 30,
      top: 30,
      bottom: 50
    },

    xAxis: {
      type: 'category',

      data: points.map(
          point =>
              formatChartTime(
                  point.deviceTimestamp
              )
      )
    },

    yAxis: {
      type: 'value',

      name: '水位 / m',

      min: 0,

      max: 10
    },

    series: [
      {
        name: '水位',

        type: 'line',

        smooth: true,

        showSymbol: false,

        data: points.map(
            point =>
                Number(point.value)
        )
      }
    ]
  })
}

function formatChartTime(
    timestamp: number
) {

  return new Date(timestamp)
      .toLocaleTimeString()
}

function formatTimestamp(
    timestamp: number
) {

  return new Date(timestamp)
      .toLocaleString()
}

function formatDateTime(
    value: string | null
) {

  if (!value) {
    return '-'
  }

  return value
}

function handleResize() {

  waterLevelChart?.resize()
}

function backToList() {

  router.push({
    name: 'device-list'
  })
}

onMounted(async () => {

  await nextTick()

  await initialLoad()

  window.addEventListener(
      'resize',
      handleResize
  )

  refreshTimer =
      window.setInterval(
          refreshRealtimeData,
          5000
      )
})

onBeforeUnmount(() => {

  if (refreshTimer !== null) {

    window.clearInterval(
        refreshTimer
    )
  }

  window.removeEventListener(
      'resize',
      handleResize
  )

  waterLevelChart?.dispose()

  waterLevelChart = null
})

</script>

<style scoped>

.device-page {
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 6px;
  font-size: 26px;
}

.page-header p {
  margin: 0;
  color: #909399;
}

.error-alert {
  margin-bottom: 20px;
}

.metric-title {
  margin-bottom: 12px;
  color: #909399;
  font-size: 14px;
}

.metric-value {
  min-height: 42px;
  font-size: 30px;
  font-weight: 600;
}

.metric-unit {
  margin-left: 4px;
  color: #909399;
  font-size: 14px;
  font-weight: normal;
}

.info-card,
.chart-card,
.message-card {
  margin-top: 20px;
}

.alarm-card {
  margin-top: 20px;
}

.chart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chart-subtitle {
  color: #909399;
  font-size: 13px;
}

.chart {
  width: 100%;
  height: 380px;
}

.command-card {
  margin-top: 20px;
}

.command-form {
  display: flex;
  align-items: center;
  gap: 12px;
}

</style>