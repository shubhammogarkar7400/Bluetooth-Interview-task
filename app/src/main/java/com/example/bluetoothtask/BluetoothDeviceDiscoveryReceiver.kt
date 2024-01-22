package com.example.bluetoothtask

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.bluetoothtask.models.BLTDevice
import com.example.bluetoothtask.screens.MainViewModel

class BluetoothDeviceDiscoveryReceiver(private val viewModel: MainViewModel) : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            BluetoothDevice.ACTION_FOUND, BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                }else{
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }?.let {
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)

                    val bltDevice = BLTDevice(
                        it,
                        false,
                        "$rssi dBm"
                    )
                   viewModel.addNewDevice(bltDevice)
                }
            }
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                }else{
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }?.let {
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                    val bltDevice = BLTDevice(
                        it,
                        true,
                        "$rssi dBm"
                    )
                    viewModel.addNewDevice(bltDevice)
                }
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                }else{
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }?.let {
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                    val bltDevice = BLTDevice(
                        it,
                        false,
                        "$rssi dBm"
                    )
                    viewModel.addNewDevice(bltDevice)
                }
            }
        }
    }
}