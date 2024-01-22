package com.example.bluetoothtask.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.bluetoothtask.BluetoothDeviceDiscoveryReceiver
import com.example.bluetoothtask.models.BLTDevice
import com.example.bluetoothtask.utils.getBondState
import com.example.bluetoothtask.utils.getDeviceClass


@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: MainViewModel,
    onBluetoothStateChange: () -> Unit
) {
    val context = LocalContext.current

    val discoveryReceiver = BluetoothDeviceDiscoveryReceiver(viewModel)

    val isDiscovering by viewModel.isDiscovering.collectAsState(initial = false)


    SystemBroadcastReceiver(systemAction = BluetoothAdapter.ACTION_STATE_CHANGED ){ intent ->
        if(intent.action == BluetoothAdapter.ACTION_STATE_CHANGED){
            onBluetoothStateChange()
        }
    }

    LaunchedEffect(key1 = null, block = {
        viewModel.getPairedDevices()
    })

    DisposableEffect(key1 = null, effect = {
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter().apply {
            this.addAction(BluetoothDevice.ACTION_FOUND)
            this.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        context.registerReceiver(discoveryReceiver, filter)
        onDispose {
            // Unregister for broadcasts when a device is discovered.
            context.unregisterReceiver(discoveryReceiver)
        }
    })

    Scaffold { paddingValues ->  
        Column(modifier = Modifier
            .padding(all = 10.dp)
            .padding(paddingValues = paddingValues)) {
            Button(onClick = {
                viewModel.startOrStopDiscovery()
            }) {
                Text(text = if(isDiscovering) "Stop Discovery" else "Start Discovery")
            }
            LazyColumn(content = {
                items(viewModel.bltDevices) {
                    BLTDeviceItem(it, viewModel)
                }
            })
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BLTDeviceItem(bltDevice: BLTDevice, viewModel: MainViewModel) {
    Box(modifier = Modifier
        .padding(all = 10.dp)
        .background(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(size = 10.dp)
        )) {
        Column(modifier = Modifier.padding(all = 20.dp)) {
            Text(text = bltDevice.device.address)
            Spacer(modifier = Modifier.height(height = 10.dp))
            Text(text = bltDevice.device.name)
            Spacer(modifier = Modifier.height(height = 10.dp))
            Text(text = getDeviceClass(bltDevice.device.bluetoothClass.deviceClass))
            Spacer(modifier = Modifier.height(height = 10.dp))
            Text(text = if (getBondState(bltDevice.device.bondState)) "Paired" else "Not Paired")
            Spacer(modifier = Modifier.height(height = 10.dp))
            Text(text = "Single Strength : ${bltDevice.signalStrength}")
            Spacer(modifier = Modifier.height(height = 10.dp))
            if (bltDevice.device.bondState == BluetoothDevice.BOND_BONDED && bltDevice.isConnected){
                Button(onClick = {
                    viewModel.disconnect()
                }) {
                    Text(text = "Disconnect")
                }
            }
             if (bltDevice.device.bondState == BluetoothDevice.BOND_BONDED && !bltDevice.isConnected){
                Button(onClick = {
                    viewModel.connect(bltDevice.device)
                }) {
                    Text(text = "Connect")
                }
            }
            if (bltDevice.device.bondState == BluetoothDevice.BOND_NONE){
                Button(onClick = {
                    bltDevice.device.createBond()
                }) {
                    Text(text = "Pair")
                }
            }

            if (bltDevice.device.bondState == BluetoothDevice.BOND_BONDED){
                Button(onClick = {
                    bltDevice.device::class.java.getMethod("removeBond").invoke(bltDevice.device)
                }) {
                    Text(text = "Unpair")
                }
            }
        }

    }
 }