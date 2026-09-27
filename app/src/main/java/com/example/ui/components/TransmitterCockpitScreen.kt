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
import com.example.data.BluetoothState
import com.example.data.DrivingMode
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.WarningOrange

@Composable
fun TransmitterCockpitScreen(
  connectionState: BluetoothState,
  drivingMode: DrivingMode,
  hornActive: Boolean,
  onDirectionPress: (Char, String) -> Unit,
  onDirectionRelease: () -> Unit,
  onHornPress: (Boolean) -> Unit,
  onEmergencyStop: () -> Unit,
  onTriggerFeature: (Int) -> Unit
) {
  val view = LocalView.current
  val canDrive = connectionState is BluetoothState.Connected

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF080B12))
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxSize(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // ---------------------------------------------------------
      // LEFT ZONE: THROTTLE (Forward F, Backward B, Hold Horn V/v)
      // ---------------------------------------------------------
      Column(
        modifier = Modifier
          .width(170.dp)
          .fillMaxHeight()
          .background(Color(0xFF141B28), RoundedCornerShape(16.dp))
          .border(1.5.dp, Color(0xFF252C38), RoundedCornerShape(16.dp))
          .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "THROTTLE",
          color = ElectricCyan,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )

        TransmitterZoneButton(
          label = "FORWARD",
          command = "F",
          icon = Icons.Default.KeyboardArrowUp,
          enabled = canDrive,
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 4.dp),
          onPress = { onDirectionPress('F', "Forward (F)") },
          onRelease = onDirectionRelease
        )

        TransmitterZoneButton(
          label = "BACKWARD",
          command = "B",
          icon = Icons.Default.KeyboardArrowDown,
          enabled = canDrive,
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 4.dp),
          onPress = { onDirectionPress('B', "Backward (B)") },
          onRelease = onDirectionRelease
        )

        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (hornActive) WarningOrange else Color(0xFF090D14))
            .border(1.dp, if (hornActive) WarningOrange else Color(0xFF252C38), RoundedCornerShape(10.dp))
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
          Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(Icons.Default.VolumeUp, contentDescription = "Horn", tint = if (hornActive) Color.Black else ElectricCyan, modifier = Modifier.size(16.dp))
            Text(if (hornActive) "HORN ACTIVE" else "HOLD HORN", color = if (hornActive) Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
          }
        }
      }

      Spacer(Modifier.width(8.dp))

      // ---------------------------------------------------------
      // CENTER ZONE: 4 Feature Buttons Left + 3x3 D-Pad + 4 Feature Buttons Right
      // ---------------------------------------------------------
      Row(
        modifier = Modifier
          .weight(1f)
          .fillMaxHeight()
          .background(Color(0xFF141B28), RoundedCornerShape(16.dp))
          .border(1.5.dp, Color(0xFF252C38), RoundedCornerShape(16.dp))
          .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        // Left 4 Feature Buttons: 1 (C), 2 (D), 3 (E), 4 (K)
        Column(
          modifier = Modifier.width(52.dp).fillMaxHeight(),
          verticalArrangement = Arrangement.SpaceAround,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          val leftButtons = listOf(
            Pair(1, 'C'),
            Pair(2, 'D'),
            Pair(3, 'E'),
            Pair(4, 'K')
          )
          for ((slot, cmd) in leftButtons) {
            FeatureCircleButton(
              displayNumber = slot,
              command = cmd,
              enabled = canDrive,
              onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onTriggerFeature(slot)
              }
            )
          }
        }

        // Center 3x3 D-Pad
        Box(
          modifier = Modifier
            .size(250.dp)
            .padding(2.dp),
          contentAlignment = Alignment.Center
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape)
              .background(Color(0xFF090D14))
              .border(2.dp, Color(0xFF252C38), CircleShape)
          )

          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              GridDirectionButton(icon = Icons.Default.NorthWest, label = "G", enabled = canDrive, onPress = { onDirectionPress('G', "Forward-Left (G)") }, onRelease = onDirectionRelease)
              GridDirectionButton(icon = Icons.Default.KeyboardArrowUp, label = "F", enabled = canDrive, onPress = { onDirectionPress('F', "Forward (F)") }, onRelease = onDirectionRelease)
              GridDirectionButton(icon = Icons.Default.NorthEast, label = "I", enabled = canDrive, onPress = { onDirectionPress('I', "Forward-Right (I)") }, onRelease = onDirectionRelease)
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              GridDirectionButton(icon = Icons.Default.KeyboardArrowLeft, label = "L", enabled = canDrive, onPress = { onDirectionPress('L', "Left Spin (L)") }, onRelease = onDirectionRelease)

              Box(
                modifier = Modifier
                  .size(66.dp)
                  .clip(CircleShape)
                  .background(EmergencyRed)
                  .border(2.dp, Color.White, CircleShape)
                  .pointerInput(canDrive) {
                    if (!canDrive) return@pointerInput
                    detectTapGestures(
                      onTap = {
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                        onEmergencyStop()
                      }
                    )
                  },
                contentAlignment = Alignment.Center
              ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White, modifier = Modifier.size(24.dp))
                  Text("STOP", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
              }

              GridDirectionButton(icon = Icons.Default.KeyboardArrowRight, label = "R", enabled = canDrive, onPress = { onDirectionPress('R', "Right Spin (R)") }, onRelease = onDirectionRelease)
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              GridDirectionButton(icon = Icons.Default.SouthWest, label = "H", enabled = canDrive, onPress = { onDirectionPress('H', "Backward-Left (H)") }, onRelease = onDirectionRelease)
              GridDirectionButton(icon = Icons.Default.KeyboardArrowDown, label = "B", enabled = canDrive, onPress = { onDirectionPress('B', "Backward (B)") }, onRelease = onDirectionRelease)
              GridDirectionButton(icon = Icons.Default.SouthEast, label = "J", enabled = canDrive, onPress = { onDirectionPress('J', "Backward-Right (J)") }, onRelease = onDirectionRelease)
            }
          }
        }

        // Right 4 Feature Buttons: 5 (M), 6 (N), 7 (O), 8 (P)
        Column(
          modifier = Modifier.width(52.dp).fillMaxHeight(),
          verticalArrangement = Arrangement.SpaceAround,
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          val rightButtons = listOf(
            Pair(5, 'M'),
            Pair(6, 'N'),
            Pair(7, 'O'),
            Pair(8, 'P')
          )
          for ((slot, cmd) in rightButtons) {
            FeatureCircleButton(
              displayNumber = slot,
              command = cmd,
              enabled = canDrive,
              onClick = {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onTriggerFeature(slot)
              }
            )
          }
        }
      }

      Spacer(Modifier.width(8.dp))

      // ---------------------------------------------------------
      // RIGHT ZONE: STEERING (Steer Left L, Steer Right R, Link Status)
      // ---------------------------------------------------------
      Column(
        modifier = Modifier
          .width(170.dp)
          .fillMaxHeight()
          .background(Color(0xFF141B28), RoundedCornerShape(16.dp))
          .border(1.5.dp, Color(0xFF252C38), RoundedCornerShape(16.dp))
          .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "STEERING",
          color = ElectricCyan,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )

        TransmitterZoneButton(
          label = "STEER LEFT",
          command = "L",
          icon = Icons.Default.KeyboardArrowLeft,
          enabled = canDrive,
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 4.dp),
          onPress = { onDirectionPress('L', "Steer Left (L)") },
          onRelease = onDirectionRelease
        )

        TransmitterZoneButton(
          label = "STEER RIGHT",
          command = "R",
          icon = Icons.Default.KeyboardArrowRight,
          enabled = canDrive,
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 4.dp),
          onPress = { onDirectionPress('R', "Steer Right (R)") },
          onRelease = onDirectionRelease
        )

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF090D14),
          border = BorderStroke(1.dp, if (canDrive) NeonGreen else EmergencyRed),
          modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (canDrive) NeonGreen else EmergencyRed)
            )
            Spacer(Modifier.width(6.dp))
            Text(
              text = if (canDrive) "LINK OK" else "DISCONNECTED",
              color = if (canDrive) NeonGreen else EmergencyRed,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
fun FeatureCircleButton(
  displayNumber: Int,
  command: Char,
  enabled: Boolean,
  onClick: () -> Unit
) {
  var isPressed by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .size(42.dp)
      .clip(CircleShape)
      .background(
        if (!enabled) Color(0xFF090D14).copy(alpha = 0.4f)
        else if (isPressed) ElectricCyan
        else Color(0xFF090D14)
      )
      .border(
        1.5.dp,
        if (isPressed) Color.White else ElectricCyan.copy(alpha = 0.5f),
        CircleShape
      )
      .pointerInput(enabled) {
        if (!enabled) return@pointerInput
        detectTapGestures(
          onPress = {
            isPressed = true
            onClick()
            try {
              awaitRelease()
            } finally {
              isPressed = false
            }
          }
        )
      },
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = "$displayNumber",
        color = if (!enabled) Color.Gray else if (isPressed) Color.Black else ElectricCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
      Text(
        text = "[$command]",
        color = if (!enabled) Color.DarkGray else if (isPressed) Color.DarkGray else Color(0xFF9FAEC3),
        fontSize = 8.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}

@Composable
fun TransmitterZoneButton(
  label: String,
  command: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  enabled: Boolean,
  modifier: Modifier = Modifier,
  onPress: () -> Unit,
  onRelease: () -> Unit
) {
  val view = LocalView.current
  var isPressed by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .background(
        if (!enabled) Color(0xFF090D14).copy(alpha = 0.5f)
        else if (isPressed) ElectricCyan
        else Color(0xFF090D14)
      )
      .border(
        1.5.dp,
        if (isPressed) Color.White else Color(0xFF252C38),
        RoundedCornerShape(14.dp)
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
        contentDescription = label,
        tint = if (!enabled) Color.Gray else if (isPressed) Color.Black else ElectricCyan,
        modifier = Modifier.size(28.dp)
      )
      Spacer(Modifier.height(2.dp))
      Text(
        text = label,
        color = if (!enabled) Color.Gray else if (isPressed) Color.Black else Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      )
      Text(
        text = "[$command]",
        color = if (!enabled) Color.DarkGray else if (isPressed) Color.DarkGray else ElectricCyan,
        fontWeight = FontWeight.Bold,
        fontSize = 9.sp
      )
    }
  }
}

@Composable
fun GridDirectionButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  enabled: Boolean,
  onPress: () -> Unit,
  onRelease: () -> Unit
) {
  val view = LocalView.current
  var isPressed by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .size(62.dp)
      .clip(RoundedCornerShape(12.dp))
      .background(
        if (!enabled) Color(0xFF090D14).copy(alpha = 0.5f)
        else if (isPressed) ElectricCyan
        else Color(0xFF141B28)
      )
      .border(
        1.5.dp,
        if (isPressed) Color.White else Color(0xFF252C38),
        RoundedCornerShape(12.dp)
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
        contentDescription = label,
        tint = if (!enabled) Color.Gray else if (isPressed) Color.Black else ElectricCyan,
        modifier = Modifier.size(22.dp)
      )
      Text(
        text = label,
        color = if (!enabled) Color.Gray else if (isPressed) Color.Black else Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp
      )
    }
  }
}
