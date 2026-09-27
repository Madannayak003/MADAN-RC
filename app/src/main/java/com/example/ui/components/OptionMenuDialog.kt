package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed

@Composable
fun OptionMenuDialog(
  onDismiss: () -> Unit,
  onConnectToCar: () -> Unit,
  onOpenSettings: () -> Unit,
  onCloseApp: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF141B28),
    tonalElevation = 8.dp,
    shape = RoundedCornerShape(16.dp),
    title = {
      Text(
        text = "Option Menu",
        color = ElectricCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.fillMaxWidth()
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OptionMenuItem(
          title = "Connect to car",
          icon = Icons.Default.Search,
          iconTint = ElectricCyan,
          onClick = {
            onDismiss()
            onConnectToCar()
          }
        )

        OptionMenuItem(
          title = "Settings",
          icon = Icons.Default.Settings,
          iconTint = ElectricCyan,
          onClick = {
            onDismiss()
            onOpenSettings()
          }
        )

        OptionMenuItem(
          title = "Close app",
          icon = Icons.Default.Close,
          iconTint = EmergencyRed,
          onClick = {
            onDismiss()
            onCloseApp()
          }
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = Color(0xFF9FAEC3))
      }
    }
  )
}

@Composable
private fun OptionMenuItem(
  title: String,
  icon: ImageVector,
  iconTint: Color,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = Color(0xFF090D14),
    border = BorderStroke(1.dp, Color(0xFF252C38)),
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = iconTint,
        modifier = Modifier.size(22.dp)
      )
      Text(
        text = title,
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
      )
    }
  }
}
