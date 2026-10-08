import { createRouter, createWebHistory } from 'vue-router'

import AdminLayout from '../layouts/AdminLayout.vue'
import DeviceListView from '../views/DeviceListView.vue'
import DeviceDetailView from '../views/DeviceDetailView.vue'
import DeviceMapView from '../views/DeviceMapView.vue'

const router = createRouter({
    history: createWebHistory(),

    routes: [
        {
            path: '/',
            component: AdminLayout,

            children: [
                {
                    path: '',
                    redirect: '/devices'
                },
                {
                    path: 'devices',
                    name: 'device-list',
                    component: DeviceListView
                },
                {
                    path: 'devices/:id',
                    name: 'device-detail',
                    component: DeviceDetailView
                },
                {
                    path: 'map',
                    name: 'device-map',
                    component: DeviceMapView
                }
            ]
        }
    ]
})

export default router