export interface Device {
    id: number
    productId: number
    productKey: string
    deviceKey: string
    deviceName: string
    thingModelVersion: number
    onlineStatus: string
    lastReportTime: string | null
    createTime: string
    updateTime: string
}

export interface DeviceCommand {

    id: number

    deviceId: number

    requestId: string

    commandType: string

    commandParams: string

    status: string

    replyMessage: string | null

    createTime: string

    sentTime: string | null

    replyTime: string | null
}

export interface Alarm {

    id: number

    deviceId: number

    alarmType: string

    alarmLevel: string

    status: string

    triggerValue: number | null

    thresholdValue: number | null

    firstTriggerTime: string

    lastTriggerTime: string

    recoverTime: string | null
}

export type PropertyValue =
    | number
    | string
    | boolean
    | null

export interface DeviceLatestState {
    deviceId: number
    productKey: string
    deviceName: string
    thingModelVersion: number
    requestId: string
    deviceTimestamp: number
    serverReceiveTimestamp: number
    properties: Record<string, PropertyValue>
}

export interface DevicePropertyHistoryPoint {
    deviceTimestamp: number
    serverReceiveTimestamp: number
    requestId: string
    identifier: string
    dataType: string
    value: PropertyValue
}

export interface NearbyDevice {

    deviceId: number

    productId: number

    deviceName: string

    longitude: number

    latitude: number

    distanceMeters: number
}