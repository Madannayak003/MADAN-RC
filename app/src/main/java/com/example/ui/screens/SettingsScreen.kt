package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppSettings
import com.example.ui.theme.ElectricCyan

@Composable
fun SettingsScreen(
  settings: AppSettings,
  onUpdateSettings: (AppSettings) -> Unit
) {
  var carNameInput by remember { mutableStateOf(settings.carName) }
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Text(
      text = "APPLICATION SETTINGS",
      color = ElectricCyan,
      fontWeight = FontWeight.Bold,
      fontSize = 18.sp
    )

    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color(0xFF111A2A),
      border = BorderStroke(1.dp, Color(0xFF18243A)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text("Car / Bluetooth Display Name", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OutlinedTextField(
          value = carNameInput,
          onValueChange = {
            carNameInput = it
            onUpdateSettings(settings.copy(carName = it))
          },
          label = { Text("Car Display Name") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Divider(color = Color(0xFF18243A))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Haptic Feedback on Touch", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Tactile vibration feedback for D-pad & buttons", color = Color(0xFF94A3B8), fontSize = 12.sp)
          }
          Switch(
            checked = settings.hapticFeedback,
            onCheckedChange = { onUpdateSettings(settings.copy(hapticFeedback = it)) },
            colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan)
          )
        }

        Divider(color = Color(0xFF18243A))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text("Auto-Reconnect Preference", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Remember last connected Bluetooth device", color = Color(0xFF94A3B8), fontSize = 12.sp)
          }
          Switch(
            checked = settings.reconnectPreference,
            onCheckedChange = { onUpdateSettings(settings.copy(reconnectPreference = it)) },
            colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan)
          )
        }
      }
    }

    // Command Reference Section
    CommandReferenceSection()

    // About Section
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = Color(0xFF111A2A),
      border = BorderStroke(1.dp, Color(0xFF18243A)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text("About MADAN RC", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("App Name: MADAN RC — Bluetooth Car Controller", color = Color.White, fontSize = 13.sp)
        Text("Developed for: Madan.R", color = Color.White, fontSize = 13.sp)
        Text("Hardware: Arduino Uno + HC-05 / HC-06 + AFMotor Shield", color = Color(0xFF94A3B8), fontSize = 12.sp)
        Text("Version: 1.0 Production Native", color = Color(0xFF94A3B8), fontSize = 12.sp)
      }
    }
  }
}

@Composable
private fun CommandReferenceSection() {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color(0xFF111A2A),
    border = BorderStroke(1.dp, Color(0xFF18243A)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      Column {
        Text("COMMAND REFERENCE", color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(
          "Reference guide for Arduino firmware command reception",
          color = Color(0xFF94A3B8),
          fontSize = 11.sp
        )
      }

      CommandCategoryBlock(
        categoryTitle = "DRIVING COMMANDS",
        commands = listOf(
          "Forward" to "F",
          "Backward" to "B",
          "Left" to "L",
          "Right" to "R",
          "Forward Left" to "G",
          "Forward Right" to "I",
          "Backward Left" to "H",
          "Backward Right" to "J",
          "Stop" to "S"
        )
      )

      Divider(color = Color(0xFF18243A))

      CommandCategoryBlock(
        categoryTitle = "LIGHT COMMANDS",
        commands = listOf(
          "Front Lights ON" to "W",
          "Front Lights OFF" to "w",
          "Rear Lights ON" to "U",
          "Rear Lights OFF" to "u"
        )
      )

      Divider(color = Color(0xFF18243A))

      CommandCategoryBlock(
        categoryTitle = "HORN COMMANDS",
        commands = listOf(
          "Horn ON" to "V",
          "Horn OFF" to "v"
        )
      )

      Divider(color = Color(0xFF18243A))

      CommandCategoryBlock(
        categoryTitle = "AUTO OBSTACLE COMMANDS",
        commands = listOf(
          "Auto Obstacle ON" to "X",
          "Auto Obstacle OFF" to "x"
        )
      )

      Divider(color = Color(0xFF18243A))

      CommandCategoryBlock(
        categoryTitle = "FEATURE COMMANDS",
        commands = listOf(
          "Feature 1" to "C",
          "Feature 2" to "D",
          "Feature 3" to "E",
          "Feature 4" to "K",
          "Feature 5" to "M",
          "Feature 6" to "N",
          "Feature 7" to "O",
          "Feature 8" to "P"
        )
      )
    }
  }
}

@Composable
private fun CommandCategoryBlock(
  categoryTitle: String,
  commands: List<Pair<String, String>>
) {
  Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
    Text(
      text = categoryTitle,
      color = Color(0xFF00E5FF),
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )
    commands.forEach { (name, charCode) ->
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(name, color = Color.White, fontSize = 13.sp)
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text("→", color = Color(0xFF94A3B8), fontSize = 13.sp)
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = Color(0xFF090D14),
            border = BorderStroke(1.dp, Color(0xFF252C38))
          ) {
            Text(
              text = charCode,
              color = ElectricCyan,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}
