package com.example.data

data class CommandLogEntry(
  val id: Long = System.currentTimeMillis(),
  val timestamp: String,
  val command: String,
  val type: LogType,
  val description: String
) {
  enum class LogType { SENT, RECEIVED, ERROR, SYSTEM }
}

data class AppSettings(
  val carName: String = "Madan's RC Car",
  val hapticFeedback: Boolean = true,
  val buttonSize: String = "Large",
  val orientationPreference: String = "Landscape",
  val reconnectPreference: Boolean = true
)

sealed interface BluetoothState {
  data object Disconnected : BluetoothState
  data object Connecting : BluetoothState
  data class Connected(val deviceName: String, val deviceAddress: String) : BluetoothState
  data class Error(val message: String) : BluetoothState
}

enum class DrivingMode {
  MANUAL,
  AUTO
}
