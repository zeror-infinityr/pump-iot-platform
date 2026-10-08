import axios from 'axios'

// import type {
//     Device,
//     DeviceLatestState,
//     DevicePropertyHistoryPoint
// } from '../types/iot'

import type {
    Alarm,
    Device,
    DeviceLatestState,
    DevicePropertyHistoryPoint,
    DeviceCommand, NearbyDevice
} from '../types/iot'

export async function setSamplingInterval(
    deviceId: number,
    intervalSeconds: number
): Promise<DeviceCommand> {

    const response =
        await http.post<DeviceCommand>(
            `/devices/${deviceId}/commands/sampling-interval`,
            {
                intervalSeconds
            }
        )

    return response.data
}

export async function getDeviceCommands(
    deviceId: number
): Promise<DeviceCommand[]> {

    const response =
        await http.get<DeviceCommand[]>(
            `/devices/${deviceId}/commands`
        )

    return response.data
}

const http = axios.create({
    baseURL: '/api/iot',
    timeout: 5000
})

export async function getDeviceAlarms(
    deviceId: number
): Promise<Alarm[]> {

    const response =
        await http.get<Alarm[]>(
            `/devices/${deviceId}/alarms`
        )

    return response.data
}

export async function getDevice(
    deviceId: number
): Promise<Device> {

    const response =
        await http.get<Device>(
            `/devices/${deviceId}`
        )

    return response.data
}

export async function getLatestProperties(
    deviceId: number
): Promise<DeviceLatestState> {

    const response =
        await http.get<DeviceLatestState>(
            `/devices/${deviceId}/latest-properties`
        )

    return response.data
}

export async function getDeviceHistory(
    deviceId: number,
    identifier: string,
    limit = 100
): Promise<DevicePropertyHistoryPoint[]> {

    const response =
        await http.get<DevicePropertyHistoryPoint[]>(
            `/devices/${deviceId}/history`,
            {
                params: {
                    identifier,
                    limit
                }
            }
        )

    return response.data
}

export async function getDevices():
    Promise<Device[]> {

    const response =
        await http.get<Device[]>(
            '/devices'
        )

    return response.data
}

export async function getNearbyDevices(

    longitude: number,

    latitude: number,

    radiusMeters: number

): Promise<NearbyDevice[]> {

    const response =
        await http.get<NearbyDevice[]>(
            '/gis/devices/nearby',
            {
                params: {
                    longitude,
                    latitude,
                    radiusMeters
                }
            }
        )

    return response.data
}