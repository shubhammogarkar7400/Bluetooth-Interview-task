package com.example.bluetoothtask.utils

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice

fun getDeviceClass(deviceClass: Int): String {
    return when(deviceClass){
        BluetoothClass.Device.PHONE_SMART -> "Smart Phone"
        BluetoothClass.Device.COMPUTER_LAPTOP -> "Computer Laptop"
        BluetoothClass.Device.COMPUTER_DESKTOP -> "Computer Desktop"
        BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES -> "Headphones"
        BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET -> "Headset"
        BluetoothClass.Device.WEARABLE_WRIST_WATCH -> "Wearable Wrist Watch"
        BluetoothClass.Device.AUDIO_VIDEO_VIDEO_MONITOR -> "Monitor"
        else -> "Unknown"
    }
}

fun getBondState(bondState : Int) : Boolean {
    return when(bondState){
        BluetoothDevice.BOND_NONE -> false
        BluetoothDevice.BOND_BONDED -> true
        else -> false
    }
}