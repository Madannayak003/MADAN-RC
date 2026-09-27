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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DrivingMode
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.WarningOrange

@Composable
fun TransmitterBottomBar(
  drivingMode: DrivingMode,
  frontLightOn: Boolean,
  rearLightOn: Boolean,
  canDrive: Boolean,
  onToggleFrontLight: (Boolean) -> Unit,
  onToggleRearLight: (Boolean) -> Unit,
  onToggleAuto: (Boolean) -> Unit,
  onOpenCommandLog: () -> Unit,
  onOpenSettings: () -> Unit
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .height(52.dp),
    color = Color(0xFF141B28),
    tonalElevation = 4.dp,
    border = BorderStroke(1.dp, Color(0xFF252C38))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Left: Front & Rear Lights Controls
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Front Light Toggle
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            Icons.Default.Highlight,
            contentDescription = "Front Light",
            tint = if (frontLightOn) ElectricCyan else Color.Gray,
            modifier = Modifier.size(18.dp)
          )
          Text("FRONT", color = Color(0xFF9FAEC3), fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Switch(
            checked = frontLightOn,
            onCheckedChange = { if (canDrive) onToggleFrontLight(it) },
            enabled = canDrive,
            colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan)
          )
        }

        // Rear Light Toggle
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Icon(
            Icons.Default.Lightbulb,
            contentDescription = "Rear Light",
            tint = if (rearLightOn) WarningOrange else Color.Gray,
            modifier = Modifier.size(18.dp)
          )
          Text("REAR", color = Color(0xFF9FAEC3), fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Switch(
            checked = rearLightOn,
            onCheckedChange = { if (canDrive) onToggleRearLight(it) },
            enabled = canDrive,
            colors = SwitchDefaults.colors(checkedThumbColor = WarningOrange)
          )
        }
      }

      // Center: Auto Obstacle Mode Toggle
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (drivingMode == DrivingMode.AUTO) WarningOrange.copy(alpha = 0.2f) else Color(0xFF090D14),
        border = BorderStroke(1.5.dp, if (drivingMode == DrivingMode.AUTO) WarningOrange else Color(0xFF252C38)),
        modifier = Modifier
          .height(38.dp)
      ) {
        Button(
          onClick = { if (canDrive) onToggleAuto(drivingMode == DrivingMode.MANUAL) },
          enabled = canDrive,
          colors = ButtonDefaults.buttonColors(
            containerColor = if (drivingMode == DrivingMode.AUTO) WarningOrange else Color(0xFF090D14)
          ),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
          modifier = Modifier.fillMaxHeight()
        ) {
          Icon(
            Icons.Default.AutoMode,
            contentDescription = "Auto",
            tint = if (drivingMode == DrivingMode.AUTO) Color.Black else WarningOrange,
            modifier = Modifier.size(16.dp)
          )
          Spacer(Modifier.width(6.dp))
          Text(
            text = if (drivingMode == DrivingMode.AUTO) "AUTO OBSTACLE ACTIVE (X)" else "MANUAL / AUTO OBSTACLE",
            color = if (drivingMode == DrivingMode.AUTO) Color.Black else WarningOrange,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
          )
        }
      }

      // Right: Logs, Settings
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedButton(
          onClick = onOpenCommandLog,
          modifier = Modifier.height(36.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF9FAEC3)),
          border = BorderStroke(1.dp, Color(0xFF252C38))
        ) {
          Icon(Icons.Default.Terminal, contentDescription = "Log", modifier = Modifier.size(16.dp))
          Spacer(Modifier.width(4.dp))
          Text("Log", fontSize = 11.sp)
        }

        OutlinedButton(
          onClick = onOpenSettings,
          modifier = Modifier.height(36.dp),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricCyan),
          border = BorderStroke(1.dp, ElectricCyan.copy(alpha = 0.5f))
        ) {
          Icon(Icons.Default.Settings, contentDescription = "Settings", modifier = Modifier.size(16.dp))
          Spacer(Modifier.width(4.dp))
          Text("Settings", fontSize = 11.sp)
        }
      }
    }
  }
}
