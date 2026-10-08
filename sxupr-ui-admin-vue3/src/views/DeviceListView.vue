<template>

  <div class="device-list-page">

    <div class="page-header">
      <div>
        <h1>
          设备管理
        </h1>

        <p>
          管理平台中已经注册的 IoT 设备
        </p>
      </div>

      <el-button
          type="primary"
          @click="loadDevices"
      >
        刷新
      </el-button>
    </div>

    <el-card shadow="never">

      <el-table
          v-loading="loading"
          :data="devices"
          stripe
          style="width: 100%"
      >

        <el-table-column
            prop="id"
            label="设备 ID"
            width="90"
        />

        <el-table-column
            prop="deviceName"
            label="设备名称"
            min-width="150"
        />

        <el-table-column
            prop="productKey"
            label="Product Key"
            min-width="210"
        />

        <el-table-column
            prop="deviceKey"
            label="Device Key"
            min-width="240"
        />

        <el-table-column
            label="物模型版本"
            width="120"
        >
          <template #default="{ row }">
            V{{ row.thingModelVersion }}
          </template>
        </el-table-column>

        <el-table-column
            label="设备状态"
            width="110"
        >
          <template #default="{ row }">

            <el-tag
                :type="
                row.onlineStatus === 'ONLINE'
                  ? 'success'
                  : 'info'
              "
            >
              {{ row.onlineStatus }}
            </el-tag>

          </template>
        </el-table-column>

        <el-table-column
            label="最后上报时间"
            min-width="180"
        >
          <template #default="{ row }">
            {{ formatTime(row.lastReportTime) }}
          </template>
        </el-table-column>

        <el-table-column
            label="操作"
            width="110"
            fixed="right"
        >
          <template #default="{ row }">

            <el-button
                link
                type="primary"
                @click="openDevice(row.id)"
            >
              查看详情
            </el-button>

          </template>
        </el-table-column>

      </el-table>

      <el-empty
          v-if="!loading && devices.length === 0"
          description="暂无设备"
      />

    </el-card>

  </div>

</template>

<script setup lang="ts">

import {
  onMounted,
  ref
} from 'vue'

import {
  useRouter
} from 'vue-router'

import {
  getDevices
} from '../api/device'

import type {
  Device
} from '../types/iot'

const router = useRouter()

const loading =
    ref(false)

const devices =
    ref<Device[]>([])

async function loadDevices() {

  loading.value = true

  try {

    devices.value =
        await getDevices()

  } catch (error) {

    console.error(
        '加载设备列表失败',
        error
    )

  } finally {

    loading.value = false
  }
}

function openDevice(
    deviceId: number
) {

  router.push({
    name: 'device-detail',

    params: {
      id: deviceId
    }
  })
}

function formatTime(
    value: string | null
) {

  if (!value) {
    return '-'
  }

  return value
}

onMounted(() => {

  loadDevices()
})

</script>

<style scoped>

.device-list-page {
  width: 100%;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;

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

</style>