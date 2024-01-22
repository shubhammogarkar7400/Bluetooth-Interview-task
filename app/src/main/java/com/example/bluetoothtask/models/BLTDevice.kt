package com.example.bluetoothtask.models

import android.bluetooth.BluetoothDevice

data class BLTDevice(
    val device: BluetoothDevice,
    var isConnected: Boolean,
    val signalStrength: String,
)