package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CommandLogEntry
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.NeonGreen

@Composable
fun CommandLogScreen(
  logs: List<CommandLogEntry>,
  onClearLogs: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "COMMAND TRANSMISSION LOG",
          color = ElectricCyan,
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
        Text(
          text = "Real-time outgoing ASCII command protocol history",
          color = Color(0xFF94A3B8),
          fontSize = 13.sp
        )
      }

      Button(
        onClick = onClearLogs,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18243A)),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.Default.Delete, contentDescription = "Clear", tint = Color.White)
        Spacer(Modifier.width(4.dp))
        Text("Clear Log", color = Color.White)
      }
    }

    if (logs.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Text("No commands transmitted yet.", color = Color(0xFF94A3B8))
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .weight(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(logs) { log ->
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF111A2A),
            border = BorderStroke(1.dp, Color(0xFF18243A)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = when (log.type) {
                    CommandLogEntry.LogType.SENT -> ElectricCyan.copy(alpha = 0.2f)
                    CommandLogEntry.LogType.RECEIVED -> NeonGreen.copy(alpha = 0.2f)
                    CommandLogEntry.LogType.ERROR -> EmergencyRed.copy(alpha = 0.2f)
                    CommandLogEntry.LogType.SYSTEM -> Color(0xFF94A3B8).copy(alpha = 0.2f)
                  }
                ) {
                  Text(
                    text = "[${log.command}]",
                    color = when (log.type) {
                      CommandLogEntry.LogType.SENT -> ElectricCyan
                      CommandLogEntry.LogType.RECEIVED -> NeonGreen
                      CommandLogEntry.LogType.ERROR -> EmergencyRed
                      CommandLogEntry.LogType.SYSTEM -> Color.White
                    },
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }

                Column {
                  Text(
                    text = log.description,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                  )
                  Text(
                    text = log.timestamp,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
