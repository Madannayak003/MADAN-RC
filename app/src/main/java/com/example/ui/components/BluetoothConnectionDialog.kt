package com.example.ui.components

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.BluetoothState
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen

@Composable
fun BluetoothConnectionDialog(
  connectionState: BluetoothState,
  onConnectDevice: (BluetoothDevice) -> Unit,
  onDisconnect: () -> Unit,
  onDismiss: () -> Unit,
  getPairedDevices: () -> Set<BluetoothDevice>
) {
  val context = LocalContext.current
  var pairedDevices by remember { mutableStateOf<Set<BluetoothDevice>>(emptySet()) }
  var hasPermission by remember { mutableStateOf(false) }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { permissions ->
    hasPermission = permissions.values.all { it }
    if (hasPermission) {
      pairedDevices = getPairedDevices()
    }
  }

  LaunchedEffect(Unit) {
    val neededPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
    } else {
      arrayOf(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
    }

    hasPermission = neededPermissions.all {
      ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    if (hasPermission) {
      pairedDevices = getPairedDevices()
    } else {
      permissionLauncher.launch(neededPermissions)
    }
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF111A2A),
    title = {
      Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Icon(Icons.Default.Bluetooth, contentDescription = "Bluetooth", tint = ElectricCyan)
        Text("Bluetooth RC Connection", color = Color.White, fontWeight = FontWeight.Bold)
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .height(340.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Current status & Disconnect
        if (connectionState is BluetoothState.Connected) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = NeonGreen.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, NeonGreen)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("CONNECTED DEVICE:", color = NeonGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(connectionState.deviceName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(connectionState.deviceAddress, color = Color(0xFF94A3B8), fontSize = 11.sp)
              }
              Button(
                onClick = onDisconnect,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1744))
              ) {
                Text("Disconnect", color = Color.White, fontWeight = FontWeight.Bold)
              }
            }
          }
        }

        Text("Paired HC-05 / HC-06 Bluetooth Devices:", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)

        if (!hasPermission) {
          Text("Bluetooth permissions required to discover paired RC modules.", color = Color(0xFFFF1744), fontSize = 12.sp)
        } else if (pairedDevices.isEmpty()) {
          Text("No paired Bluetooth Classic devices found. Please pair your HC-05/HC-06 module in Android Settings first.", color = Color(0xFF94A3B8), fontSize = 12.sp)
        } else {
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(pairedDevices.toList()) { device ->
              @SuppressLint("MissingPermission")
              val name = device.name ?: "Unknown HC-05 Module"
              val address = device.address

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF18243A),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    onConnectDevice(device)
                    onDismiss()
                  }
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(address, color = Color(0xFF94A3B8), fontSize = 11.sp)
                  }
                  Icon(Icons.Default.BluetoothConnected, contentDescription = "Connect", tint = ElectricCyan)
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Close", color = ElectricCyan, fontWeight = FontWeight.Bold)
      }
    }
  )
}
