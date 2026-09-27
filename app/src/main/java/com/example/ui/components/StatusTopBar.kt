package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BluetoothState
import com.example.data.DrivingMode
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.WarningOrange

@Composable
fun StatusTopBar(
  connectionState: BluetoothState,
  isDemoMode: Boolean,
  drivingMode: DrivingMode,
  speedPercentage: Int,
  onOpenBluetoothDialog: () -> Unit,
  onEmergencyStop: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .padding(6.dp),
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFF111A2A),
    tonalElevation = 4.dp,
    border = BorderStroke(1.dp, Color(0xFF18243A))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Branding & Connection Status
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(
              when {
                isDemoMode -> WarningOrange
                connectionState is BluetoothState.Connected -> NeonGreen
                connectionState is BluetoothState.Connecting -> WarningOrange
                connectionState is BluetoothState.Error -> EmergencyRed
                else -> Color.Gray
              }
            )
        )

        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = "MADAN RC",
              color = ElectricCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            if (isDemoMode) {
              Surface(
                shape = RoundedCornerShape(4.dp),
                color = WarningOrange.copy(alpha = 0.2f)
              ) {
                Text(
                  text = "DEMO MODE",
                  color = WarningOrange,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
              }
            }
          }
          Text(
            text = when {
              isDemoMode -> "SIMULATION ONLY"
              connectionState is BluetoothState.Connected -> "CONNECTED: ${connectionState.deviceName}"
              connectionState is BluetoothState.Connecting -> "CONNECTING..."
              connectionState is BluetoothState.Error -> "CONNECTION ERROR"
              else -> "DISCONNECTED"
            },
            color = when {
              isDemoMode -> WarningOrange
              connectionState is BluetoothState.Connected -> NeonGreen
              connectionState is BluetoothState.Error -> EmergencyRed
              else -> Color(0xFF94A3B8)
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
          )
        }

        OutlinedButton(
          onClick = onOpenBluetoothDialog,
          modifier = Modifier.height(34.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan)
        ) {
          Icon(Icons.Default.Bluetooth, contentDescription = "Bluetooth", modifier = Modifier.size(15.dp))
          Spacer(Modifier.width(4.dp))
          Text(
            if (connectionState is BluetoothState.Connected || isDemoMode) "CONFIG" else "SELECT DEVICE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Mode & Speed & Emergency Stop
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Mode badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (drivingMode == DrivingMode.AUTO) WarningOrange.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.2f),
          border = BorderStroke(1.dp, if (drivingMode == DrivingMode.AUTO) WarningOrange else ElectricBlue)
        ) {
          Text(
            text = if (drivingMode == DrivingMode.AUTO) "AUTO MODE" else "MANUAL",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            color = if (drivingMode == DrivingMode.AUTO) WarningOrange else ElectricCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }

        // Speed badge
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF18243A)
        ) {
          Text(
            text = "SPEED: $speedPercentage%",
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }

        // Emergency STOP Button (CRITICAL)
        Button(
          onClick = onEmergencyStop,
          colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .height(38.dp)
            .testTag("emergency_stop_button")
        ) {
          Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(Modifier.width(2.dp))
          Text("STOP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}
