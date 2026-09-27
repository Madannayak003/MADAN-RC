package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BluetoothState
import com.example.data.DrivingMode
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.WarningOrange

@Composable
fun TransmitterTopBar(
  connectionState: BluetoothState,
  drivingMode: DrivingMode,
  speedPercentage: Int,
  onSpeedChange: (Int) -> Unit,
  onOpenBluetoothDialog: () -> Unit,
  onEmergencyStop: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(50.dp),
    color = Color(0xFF141B28),
    tonalElevation = 6.dp,
    border = BorderStroke(1.dp, Color(0xFF252C38))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Branding & Connection Status (Clickable to open Bluetooth connection dialog)
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .clickable { onOpenBluetoothDialog() }
          .padding(4.dp)
      ) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(
              when (connectionState) {
                is BluetoothState.Connected -> NeonGreen
                is BluetoothState.Connecting -> WarningOrange
                is BluetoothState.Error -> EmergencyRed
                else -> Color.Gray
              }
            )
        )

        Column {
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
              text = "MADAN RC",
              color = ElectricCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
          Text(
            text = when (connectionState) {
              is BluetoothState.Connected -> "● CONNECTED: ${connectionState.deviceName}"
              is BluetoothState.Connecting -> "● CONNECTING..."
              is BluetoothState.Error -> "● CONNECTION ERROR"
              else -> "● DISCONNECTED (TAP)"
            },
            color = when (connectionState) {
              is BluetoothState.Connected -> NeonGreen
              is BluetoothState.Error -> EmergencyRed
              else -> Color(0xFF94A3B8)
            },
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // Center: Car Status (MANUAL / AUTO)
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (drivingMode == DrivingMode.AUTO) WarningOrange.copy(alpha = 0.2f) else Color(0xFF090D14),
        border = BorderStroke(1.dp, if (drivingMode == DrivingMode.AUTO) WarningOrange else ElectricCyan.copy(alpha = 0.5f))
      ) {
        Text(
          text = if (drivingMode == DrivingMode.AUTO) "CAR STATUS: AUTO OBSTACLE" else "CAR STATUS: MANUAL",
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
          color = if (drivingMode == DrivingMode.AUTO) WarningOrange else ElectricCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
      }

      // Right: Speed Control & Emergency Stop
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Speed Display & Step adjust
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF090D14),
          border = BorderStroke(1.dp, Color(0xFF252C38))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text("SPEED: ${speedPercentage}%", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            IconButton(
              onClick = { onSpeedChange((speedPercentage / 10 - 1).coerceIn(0, 10)) },
              modifier = Modifier.size(22.dp)
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = ElectricCyan, modifier = Modifier.size(16.dp))
            }
            IconButton(
              onClick = { onSpeedChange((speedPercentage / 10 + 1).coerceIn(0, 10)) },
              modifier = Modifier.size(22.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Increase", tint = ElectricCyan, modifier = Modifier.size(16.dp))
            }
          }
        }

        // Emergency Stop Button
        Button(
          onClick = onEmergencyStop,
          colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
          modifier = Modifier.height(36.dp)
        ) {
          Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(16.dp))
          Spacer(Modifier.width(4.dp))
          Text("STOP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
      }
    }
  }
}
