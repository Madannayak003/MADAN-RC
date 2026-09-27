package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.WarningOrange

@Composable
fun ManualDrivingControl(
  canDrive: Boolean,
  speedStep: Int,
  frontLightOn: Boolean,
  rearLightOn: Boolean,
  hornActive: Boolean,
  onDirectionPress: (Char, String) -> Unit,
  onDirectionRelease: () -> Unit,
  onSpeedChange: (Int) -> Unit,
  onToggleFrontLight: (Boolean) -> Unit,
  onToggleRearLight: (Boolean) -> Unit,
  onHornPress: (Boolean) -> Unit
) {
  val view = LocalView.current

  Row(
    modifier = Modifier
      .fillMaxSize()
      .padding(12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Left Panel: Lights, Horn & Speed
    Column(
      modifier = Modifier
        .width(220.dp)
        .fillMaxHeight(),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Lights Card
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF111A2A),
        border = BorderStroke(1.dp, Color(0xFF18243A))
      ) {
        Column(
          modifier = Modifier.padding(10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = "VEHICLE LIGHTS",
            color = ElectricCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )

          // Front Light Toggle (W / w)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Highlight, contentDescription = "Front", tint = if (frontLightOn) ElectricCyan else Color.Gray, modifier = Modifier.size(18.dp))
              Text("Front (W/w)", color = Color.White, fontSize = 12.sp)
            }
            Switch(
              checked = frontLightOn,
              onCheckedChange = {
                if (canDrive) {
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  onToggleFrontLight(it)
                }
              },
              enabled = canDrive,
              colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan)
            )
          }

          // Rear Light Toggle (U / u)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Icon(Icons.Default.Lightbulb, contentDescription = "Rear", tint = if (rearLightOn) WarningOrange else Color.Gray, modifier = Modifier.size(18.dp))
              Text("Rear (U/u)", color = Color.White, fontSize = 12.sp)
            }
            Switch(
              checked = rearLightOn,
              onCheckedChange = {
                if (canDrive) {
                  view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                  onToggleRearLight(it)
                }
              },
              enabled = canDrive,
              colors = SwitchDefaults.colors(checkedThumbColor = WarningOrange)
            )
          }
        }
      }

      // Horn Control (Press and Hold V / v)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(if (hornActive) WarningOrange else Color(0xFF18243A))
          .border(1.dp, if (hornActive) WarningOrange else Color(0xFF334155), RoundedCornerShape(14.dp))
          .pointerInput(canDrive) {
            if (!canDrive) return@pointerInput
            detectTapGestures(
              onPress = {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                onHornPress(true)
                try {
                  awaitRelease()
                } finally {
                  onHornPress(false)
                }
              }
            )
          },
        contentAlignment = Alignment.Center
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Icon(
            Icons.Default.VolumeUp,
            contentDescription = "Horn",
            tint = if (hornActive) Color.Black else ElectricCyan
          )
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = if (hornActive) "HORN ACTIVE (V)" else "HOLD FOR HORN",
              color = if (hornActive) Color.Black else Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Text(
              text = "Press & Hold V/v",
              color = if (hornActive) Color.DarkGray else Color(0xFF94A3B8),
              fontSize = 10.sp
            )
          }
        }
      }

      // Speed Slider Component (0 to 100%)
      Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF111A2A),
        border = BorderStroke(1.dp, Color(0xFF18243A))
      ) {
        Column(
          modifier = Modifier.padding(10.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("SPEED", color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("${speedStep * 10}%", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
          }
          Slider(
            value = speedStep.toFloat(),
            onValueChange = {
              if (canDrive) {
                onSpeedChange(it.toInt())
              }
            },
            valueRange = 0f..10f,
            steps = 9,
            enabled = canDrive,
            colors = SliderDefaults.colors(
              thumbColor = ElectricCyan,
              activeTrackColor = ElectricCyan,
              inactiveTrackColor = Color(0xFF334155)
            )
          )
        }
      }
    }

    // Center / Right: Large 8-Direction D-Pad Driving Controller
    Column(
      modifier = Modifier
        .fillMaxHeight()
        .weight(1f),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(280.dp)
          .padding(8.dp),
        contentAlignment = Alignment.Center
      ) {
        // Cockpit D-pad background ring
        Box(
          modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(Color(0xFF111A2A).copy(alpha = 0.8f))
            .border(2.dp, Color(0xFF18243A), CircleShape)
        )

        // 3x3 Grid Layout for 8 Directions + Center Stop
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
          verticalArrangement = Arrangement.SpaceBetween,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Top Row: Forward-Left (G), Forward (F), Forward-Right (I)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            DPadButton(
              icon = Icons.Default.NorthWest,
              label = "G",
              title = "F-Left",
              enabled = canDrive,
              onPress = { onDirectionPress('G', "Forward-Left (G)") },
              onRelease = onDirectionRelease
            )
            DPadButton(
              icon = Icons.Default.KeyboardArrowUp,
              label = "F",
              title = "Forward",
              enabled = canDrive,
              onPress = { onDirectionPress('F', "Forward (F)") },
              onRelease = onDirectionRelease
            )
            DPadButton(
              icon = Icons.Default.NorthEast,
              label = "I",
              title = "F-Right",
              enabled = canDrive,
              onPress = { onDirectionPress('I', "Forward-Right (I)") },
              onRelease = onDirectionRelease
            )
          }

          // Middle Row: Left (L), Center Stop/Hub, Right (R)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            DPadButton(
              icon = Icons.Default.KeyboardArrowLeft,
              label = "L",
              title = "Left",
              enabled = canDrive,
              onPress = { onDirectionPress('L', "Left Spin (L)") },
              onRelease = onDirectionRelease
            )

            // Center Hub / Stop button
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(if (canDrive) Color(0xFFFF1744).copy(alpha = 0.2f) else Color.DarkGray)
                .border(2.dp, if (canDrive) Color(0xFFFF1744) else Color.Gray, CircleShape)
                .pointerInput(canDrive) {
                  if (!canDrive) return@pointerInput
                  detectTapGestures(
                    onTap = {
                      view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                      onDirectionRelease()
                    }
                  )
                },
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  Icons.Default.Stop,
                  contentDescription = "Stop",
                  tint = if (canDrive) Color(0xFFFF1744) else Color.Gray,
                  modifier = Modifier.size(28.dp)
                )
                Text("S", color = if (canDrive) Color(0xFFFF1744) else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }

            DPadButton(
              icon = Icons.Default.KeyboardArrowRight,
              label = "R",
              title = "Right",
              enabled = canDrive,
              onPress = { onDirectionPress('R', "Right Spin (R)") },
              onRelease = onDirectionRelease
            )
          }

          // Bottom Row: Backward-Left (H), Backward (B), Backward-Right (J)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            DPadButton(
              icon = Icons.Default.SouthWest,
              label = "H",
              title = "B-Left",
              enabled = canDrive,
              onPress = { onDirectionPress('H', "Backward-Left (H)") },
              onRelease = onDirectionRelease
            )
            DPadButton(
              icon = Icons.Default.KeyboardArrowDown,
              label = "B",
              title = "Backward",
              enabled = canDrive,
              onPress = { onDirectionPress('B', "Backward (B)") },
              onRelease = onDirectionRelease
            )
            DPadButton(
              icon = Icons.Default.SouthEast,
              label = "J",
              title = "B-Right",
              enabled = canDrive,
              onPress = { onDirectionPress('J', "Backward-Right (J)") },
              onRelease = onDirectionRelease
            )
          }
        }
      }
    }
  }
}

@Composable
fun DPadButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  title: String,
  enabled: Boolean,
  onPress: () -> Unit,
  onRelease: () -> Unit
) {
  val view = LocalView.current
  var isPressed by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .size(74.dp) // Large touch target (>= 72dp)
      .clip(RoundedCornerShape(18.dp))
      .background(
        if (!enabled) Color(0xFF1E293B).copy(alpha = 0.4f)
        else if (isPressed) ElectricCyan
        else Color(0xFF18243A)
      )
      .border(
        1.5.dp,
        if (isPressed) Color.White else Color(0xFF334155),
        RoundedCornerShape(18.dp)
      )
      .pointerInput(enabled) {
        if (!enabled) return@pointerInput
        detectTapGestures(
          onPress = {
            isPressed = true
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            onPress()
            try {
              awaitRelease()
            } finally {
              isPressed = false
              view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
              onRelease()
            }
          }
        )
      },
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = title,
        tint = if (!enabled) Color.Gray else if (isPressed) Color.Black else ElectricCyan,
        modifier = Modifier.size(28.dp)
      )
      Text(
        text = label,
        color = if (!enabled) Color.Gray else if (isPressed) Color.Black else Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
      )
    }
  }
}
