package com.example.bluetoothtask.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
 import com.example.bluetoothtask.models.BLTDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject

@SuppressLint("MissingPermission")
@HiltViewModel
class MainViewModel @Inject constructor(private val bluetoothAdapter: BluetoothAdapter) : ViewModel() {
    var bltDevices: List<BLTDevice> by mutableStateOf(emptyList())
        private set

    var isDiscovering  = MutableStateFlow(bluetoothAdapter.isDiscovering)

    fun getPairedDevices() {
        bluetoothAdapter.bondedDevices.forEach {
            val device = BLTDevice(
                it,
                false,
                "Unknown"
            )
            addNewDevice(device)
        }
    }

    fun addNewDevice(device: BLTDevice) {
        for (i in 0..bltDevices.lastIndex){
            if (bltDevices[i].isConnected){
                bltDevices[i].isConnected = false
            }
        }
        val oldDevice = bltDevices.find { it.device.address ==  device.device.address}
        if (oldDevice != null){
            bltDevices = bltDevices - oldDevice
            bltDevices = bltDevices + device
        }else{
            bltDevices = bltDevices + device
        }
    }

    private var bluetoothSocket: BluetoothSocket? = null

    fun connect(device: BluetoothDevice) {

        // UUID for SPP (Serial Port Profile). This should match the UUID used by the Bluetooth device.
        val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid)
            bluetoothSocket?.connect()

            // Connection successful, you can now use the BluetoothSocket for data transfer.

        } catch (e: Exception) {
            // Handle connection errors
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            bluetoothSocket?.close()
        } catch (e: Exception) {
            // Handle disconnection errors
            e.printStackTrace()
        }
    }


    fun startOrStopDiscovery() {
         if (bluetoothAdapter.isDiscovering){
             bluetoothAdapter.cancelDiscovery()
              isDiscovering.value =false
         }else{
             bluetoothAdapter.startDiscovery()
             isDiscovering.value = true
         }
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothAdapter.cancelDiscovery()
    }

}

