package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.BluetoothState
import com.example.ui.components.BluetoothConnectionDialog
import com.example.ui.components.OptionMenuDialog
import com.example.ui.components.TransmitterBottomBar
import com.example.ui.components.TransmitterCockpitScreen
import com.example.ui.components.TransmitterTopBar
import com.example.ui.screens.CommandLogScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MadanRcTheme
import com.example.viewmodel.CarViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    hideSystemUI()

    setContent {
      MadanRcTheme {
        val viewModel: CarViewModel = viewModel { CarViewModel(applicationContext) }

        val connectionState by viewModel.connectionState.collectAsStateWithLifecycle()
        val drivingMode by viewModel.drivingMode.collectAsStateWithLifecycle()
        val speedStep by viewModel.speedStep.collectAsStateWithLifecycle()
        val frontLightOn by viewModel.frontLightOn.collectAsStateWithLifecycle()
        val rearLightOn by viewModel.rearLightOn.collectAsStateWithLifecycle()
        val hornActive by viewModel.hornActive.collectAsStateWithLifecycle()
        val commandLogs by viewModel.commandLogs.collectAsStateWithLifecycle()
        val settings by viewModel.settings.collectAsStateWithLifecycle()

        var showOptionMenuDialog by remember { mutableStateOf(false) }
        var showBluetoothDialog by remember { mutableStateOf(false) }
        var showCommandLogDialog by remember { mutableStateOf(false) }
        var showSettingsDialog by remember { mutableStateOf(false) }

        val isConnected = connectionState is BluetoothState.Connected

        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          Column(
            modifier = Modifier
              .fillMaxSize()
              .systemBarsPadding()
          ) {
            // 1. Top Header Toolbar
            TransmitterTopBar(
              connectionState = connectionState,
              drivingMode = drivingMode,
              speedPercentage = speedStep * 10,
              onSpeedChange = { viewModel.setSpeedStep(it) },
              onOpenBluetoothDialog = { showBluetoothDialog = true },
              onEmergencyStop = { viewModel.sendStop() }
            )

            // 2. Main Full-Screen Landscape Cockpit with fixed feature buttons 1-8 flanking center D-pad
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
            ) {
              TransmitterCockpitScreen(
                connectionState = connectionState,
                drivingMode = drivingMode,
                hornActive = hornActive,
                onDirectionPress = { cmd, desc -> viewModel.sendDirection(cmd, desc) },
                onDirectionRelease = { viewModel.sendStop() },
                onHornPress = { viewModel.setHornActive(it) },
                onEmergencyStop = { viewModel.sendStop() },
                onTriggerFeature = { viewModel.sendFeatureCommand(it) }
              )
            }

            // 3. Bottom Auxiliary Panel (FRONT, REAR, AUTO, LOG, SETTINGS)
            TransmitterBottomBar(
              drivingMode = drivingMode,
              frontLightOn = frontLightOn,
              rearLightOn = rearLightOn,
              canDrive = isConnected,
              onToggleFrontLight = { viewModel.toggleFrontLight(it) },
              onToggleRearLight = { viewModel.toggleRearLight(it) },
              onToggleAuto = { viewModel.toggleAutoMode(it) },
              onOpenCommandLog = { showCommandLogDialog = true },
              onOpenSettings = { showOptionMenuDialog = true }
            )
          }
        }

        // Option Menu Dialog (Single entry point from bottom Settings button)
        if (showOptionMenuDialog) {
          OptionMenuDialog(
            onDismiss = { showOptionMenuDialog = false },
            onConnectToCar = { showBluetoothDialog = true },
            onOpenSettings = { showSettingsDialog = true },
            onCloseApp = { finish() }
          )
        }

        // Secondary Overlays & Dialogs
        if (showBluetoothDialog) {
          BluetoothConnectionDialog(
            connectionState = connectionState,
            onConnectDevice = { viewModel.connectDevice(it) },
            onDisconnect = { viewModel.disconnectDevice() },
            onDismiss = { showBluetoothDialog = false },
            getPairedDevices = { viewModel.getPairedDevices() }
          )
        }

        if (showCommandLogDialog) {
          AlertDialog(
            onDismissRequest = { showCommandLogDialog = false },
            containerColor = Color(0xFF141B28),
            title = { Text("Command Transmission Log", color = ElectricCyan) },
            text = {
              Box(modifier = Modifier.fillMaxWidth().height(380.dp)) {
                CommandLogScreen(
                  logs = commandLogs,
                  onClearLogs = { viewModel.clearLogs() }
                )
              }
            },
            confirmButton = {
              TextButton(onClick = { showCommandLogDialog = false }) {
                Text("Close", color = ElectricCyan)
              }
            }
          )
        }

        if (showSettingsDialog) {
          AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = Color(0xFF141B28),
            title = { Text("Application Settings", color = ElectricCyan) },
            text = {
              Box(modifier = Modifier.fillMaxWidth().height(380.dp)) {
                SettingsScreen(
                  settings = settings,
                  onUpdateSettings = { viewModel.updateSettings(it) }
                )
              }
            },
            confirmButton = {
              TextButton(onClick = { showSettingsDialog = false }) {
                Text("Close", color = ElectricCyan)
              }
            }
          )
        }
      }
    }
  }

  private fun hideSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.hide(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) {
      hideSystemUI()
    }
  }
}
