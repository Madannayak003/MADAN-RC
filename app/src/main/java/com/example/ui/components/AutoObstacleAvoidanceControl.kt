package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.WarningOrange

@Composable
fun AutoObstacleAvoidanceControl(
  isConnected: Boolean,
  onExitAuto: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth()
        .widthIn(max = 600.dp),
      shape = RoundedCornerShape(24.dp),
      color = WarningOrange.copy(alpha = 0.15f),
      border = BorderStroke(2.dp, WarningOrange)
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Icon(
            Icons.Default.Warning,
            contentDescription = "Auto Mode Warning",
            tint = WarningOrange,
            modifier = Modifier.size(36.dp)
          )
          Text(
            text = "AUTO OBSTACLE ACTIVE",
            color = WarningOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        }

        Text(
          text = "Arduino onboard microcontroller is managing its own DC motors, ultrasonic HC-SR04 sensor, and servo scanning autonomously.\n\nManual driving controls are temporarily disabled. No manual commands are transmitted.",
          color = Color.White,
          fontSize = 14.sp,
          textAlign = TextAlign.Center,
          lineHeight = 20.sp
        )

        Spacer(Modifier.height(8.dp))

        Button(
          onClick = onExitAuto,
          enabled = isConnected,
          colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
        ) {
          Icon(Icons.Default.Cancel, contentDescription = "Exit Auto", tint = Color.White)
          Spacer(Modifier.width(8.dp))
          Text(
            text = "EXIT AUTO & STOP MOTORS (x)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }
      }
    }
  }
}
