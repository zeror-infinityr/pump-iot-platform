<template>
  <el-container class="admin-layout">

    <el-aside
        width="220px"
        class="sidebar"
    >
      <div class="logo">
        Pump IoT
      </div>

      <el-menu
          router
          :default-active="activeMenu"
          class="menu"
      >
        <el-menu-item index="/devices">
          <el-icon>
            <Monitor />
          </el-icon>

          <span>
            设备管理
          </span>
        </el-menu-item>

        <el-menu-item index="/map">

          <el-icon>
            <Location />
          </el-icon>

          <span>
    设备地图
  </span>

        </el-menu-item>

      </el-menu>
    </el-aside>

    <el-container>

      <el-header class="header">
        <div class="header-title">
          泵站设备监测平台
        </div>

        <div class="header-right">
          本地开发环境
        </div>
      </el-header>

      <el-main class="main">
        <RouterView />
      </el-main>

    </el-container>

  </el-container>
</template>

<script setup lang="ts">

import { computed } from 'vue'
import { RouterView, useRoute } from 'vue-router'
import { Monitor,Location } from '@element-plus/icons-vue'

const route = useRoute()

const activeMenu = computed(() => {

  if (route.path.startsWith('/devices')) {
    return '/devices'
  }
  if (route.path.startsWith('/map')) {
    return '/map'
  }
  return route.path
}
)

</script>

<style scoped>

.admin-layout {
  width: 100%;
  min-height: 100vh;
}

.sidebar {
  background: #ffffff;
  border-right: 1px solid #e4e7ed;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding-left: 24px;
  font-size: 20px;
  font-weight: 600;
  border-bottom: 1px solid #e4e7ed;
}

.menu {
  border-right: none;
}

.header {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;

  background: #ffffff;
  border-bottom: 1px solid #e4e7ed;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
}

.header-right {
  color: #909399;
  font-size: 14px;
}

.main {
  padding: 24px;
  background: #f5f7fa;
}

</style>