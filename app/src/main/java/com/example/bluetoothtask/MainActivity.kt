package com.example.bluetoothtask

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothtask.screens.HomeScreen
import com.example.bluetoothtask.screens.MainViewModel
import com.example.bluetoothtask.ui.theme.BluetoothTaskTheme
import com.example.bluetoothtask.utils.permissions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var bluetoothAdapter: BluetoothAdapter

    val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothTaskTheme {

                val multiplePermissionState = rememberMultiplePermissionsState(permissions =  permissions)
                

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!multiplePermissionState.allPermissionsGranted){
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Button(onClick = {
                                multiplePermissionState.launchMultiplePermissionRequest()
                            }) {
                                Text(text = "Allow Permissions")
                            }
                        }
                    }else {
                        AppNavigation(viewModel = viewModel) {
                            showBluetoothDialog()
                        }
                    }
                }
            }
        }
    }

    private var isBluetoothDialogAlreadyShown = false

    private fun showBluetoothDialog() {
        if (!bluetoothAdapter.isEnabled){
            if (!isBluetoothDialogAlreadyShown){
                val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startBluetoothIntentForResult.launch(enableBluetoothIntent)
                isBluetoothDialogAlreadyShown = true
            }
        }
    }

    private val startBluetoothIntentForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        isBluetoothDialogAlreadyShown = false
        if (it.resultCode != RESULT_OK){
            showBluetoothDialog()
        }
    }
}


@Composable
fun AppNavigation(viewModel: MainViewModel, onBluetoothStateChange: () -> Unit){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.HOME_SCREEN.route){
        composable(route = AppRoutes.HOME_SCREEN.route){
            HomeScreen(navController = navController, viewModel = viewModel){
                    onBluetoothStateChange()
            }
        }
    }
}



