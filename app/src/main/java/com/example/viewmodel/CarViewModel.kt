package com.example.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppSettings
import com.example.data.BluetoothConnectionManager
import com.example.data.BluetoothState
import com.example.data.CommandLogEntry
import com.example.data.DrivingMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CarViewModel(context: Context) : ViewModel() {

  private val btManager = BluetoothConnectionManager(context.applicationContext)
  val connectionState: StateFlow<BluetoothState> = btManager.connectionState

  private val _drivingMode = MutableStateFlow(DrivingMode.MANUAL)
  val drivingMode: StateFlow<DrivingMode> = _drivingMode.asStateFlow()

  // Speed index 0 to 10 (0% to 100%)
  private val _speedStep = MutableStateFlow(8) // Default 80% (step 8 -> command '8')
  val speedStep: StateFlow<Int> = _speedStep.asStateFlow()

  private val _frontLightOn = MutableStateFlow(false)
  val frontLightOn: StateFlow<Boolean> = _frontLightOn.asStateFlow()

  private val _rearLightOn = MutableStateFlow(false)
  val rearLightOn: StateFlow<Boolean> = _rearLightOn.asStateFlow()

  private val _hornActive = MutableStateFlow(false)
  val hornActive: StateFlow<Boolean> = _hornActive.asStateFlow()

  private val _commandLogs = MutableStateFlow<List<CommandLogEntry>>(emptyList())
  val commandLogs: StateFlow<List<CommandLogEntry>> = _commandLogs.asStateFlow()

  private val _settings = MutableStateFlow(AppSettings())
  val settings: StateFlow<AppSettings> = _settings.asStateFlow()

  private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

  private var repeatCommandJob: Job? = null

  fun isBluetoothSupported(): Boolean = btManager.isBluetoothSupported()
  fun isBluetoothEnabled(): Boolean = btManager.isBluetoothEnabled()

  @SuppressLint("MissingPermission")
  fun getPairedDevices(): Set<BluetoothDevice> = btManager.getPairedDevices()

  fun connectDevice(device: BluetoothDevice) {
    viewModelScope.launch {
      val success = btManager.connect(device)
      logCommand("CONNECT", if (success) "Connected to ${device.name ?: device.address}" else "Connection Failed", CommandLogEntry.LogType.SYSTEM)
    }
  }

  fun disconnectDevice() {
    viewModelScope.launch {
      stopRepeatJob()
      btManager.disconnect()
      logCommand("DISCONNECT", "Disconnected from RC Car", CommandLogEntry.LogType.SYSTEM)
    }
  }

  fun setSpeedStep(step: Int) {
    val clamped = step.coerceIn(0, 10)
    _speedStep.value = clamped
    val cmdChar = speedStepToChar(clamped)
    sendCharCommand(cmdChar, "Speed set to ${clamped * 10}%")
  }

  private fun speedStepToChar(step: Int): Char {
    return when (step) {
      0 -> '0'
      1 -> '1'
      2 -> '2'
      3 -> '3'
      4 -> '4'
      5 -> '5'
      6 -> '6'
      7 -> '7'
      8 -> '8'
      9 -> '9'
      10 -> 'q'
      else -> '8'
    }
  }

  fun sendDirection(directionCmd: Char, actionDesc: String) {
    if (_drivingMode.value == DrivingMode.AUTO) {
      return
    }

    // If L or R, start repeating loop while held
    if (directionCmd == 'L' || directionCmd == 'R') {
      startRepeatJob(directionCmd, actionDesc)
    } else {
      stopRepeatJob()
      sendCharCommand(directionCmd, actionDesc)
    }
  }

  private fun startRepeatJob(cmd: Char, desc: String) {
    stopRepeatJob()
    repeatCommandJob = viewModelScope.launch {
      // Send immediately on press
      sendCharCommand(cmd, desc)
      while (true) {
        delay(120) // 120ms repeat interval for Arduino indicator blink logic
        sendCharCommand(cmd, "$desc (repeat)")
      }
    }
  }

  private fun stopRepeatJob() {
    repeatCommandJob?.cancel()
    repeatCommandJob = null
  }

  fun sendStop() {
    stopRepeatJob()
    if (_drivingMode.value == DrivingMode.AUTO) {
      exitAutoMode()
    } else {
      sendCharCommand('S', "Emergency Stop / Release")
    }
  }

  fun toggleAutoMode(enable: Boolean) {
    stopRepeatJob()
    if (enable) {
      sendCharCommand('S', "Pre-Auto Stop (S)")
      _drivingMode.value = DrivingMode.AUTO
      sendCharCommand('X', "Enable Auto Obstacle Avoidance (X)")
    } else {
      exitAutoMode()
    }
  }

  fun exitAutoMode() {
    stopRepeatJob()
    _drivingMode.value = DrivingMode.MANUAL
    sendCharCommand('x', "Exit Auto Obstacle Avoidance (x)")
    sendCharCommand('S', "Motor Stop (S)")
  }

  fun toggleFrontLight(on: Boolean) {
    _frontLightOn.value = on
    val cmd = if (on) 'W' else 'w'
    sendCharCommand(cmd, if (on) "Front Lights ON (W)" else "Front Lights OFF (w)")
  }

  fun toggleRearLight(on: Boolean) {
    _rearLightOn.value = on
    val cmd = if (on) 'U' else 'u'
    sendCharCommand(cmd, if (on) "Rear Lights ON (U)" else "Rear Lights OFF (u)")
  }

  fun setHornActive(active: Boolean) {
    _hornActive.value = active
    val cmd = if (active) 'V' else 'v'
    sendCharCommand(cmd, if (active) "Horn ON (V)" else "Horn OFF (v)")
  }

  /**
   * Fixed feature command buttons 1 to 8:
   * 1 -> C, 2 -> D, 3 -> E, 4 -> K, 5 -> M, 6 -> N, 7 -> O, 8 -> P
   */
  fun sendFeatureCommand(buttonNumber: Int) {
    val cmd = when (buttonNumber) {
      1 -> 'C'
      2 -> 'D'
      3 -> 'E'
      4 -> 'K'
      5 -> 'M'
      6 -> 'N'
      7 -> 'O'
      8 -> 'P'
      else -> return
    }
    sendCharCommand(cmd, "Feature $buttonNumber ($cmd)")
  }

  fun updateSettings(newSettings: AppSettings) {
    _settings.value = newSettings
    logCommand("SETTINGS", "Updated application settings", CommandLogEntry.LogType.SYSTEM)
  }

  private fun sendCharCommand(char: Char, description: String) {
    viewModelScope.launch {
      val success = btManager.sendCommand(char)
      if (!success) {
        stopRepeatJob()
      }
      logCommand(
        command = char.toString(),
        description = description,
        type = if (success) CommandLogEntry.LogType.SENT else CommandLogEntry.LogType.ERROR
      )
    }
  }

  private fun logCommand(command: String, description: String, type: CommandLogEntry.LogType) {
    val entry = CommandLogEntry(
      timestamp = dateFormat.format(Date()),
      command = command,
      type = type,
      description = description
    )
    _commandLogs.update { logs ->
      (listOf(entry) + logs).take(200)
    }
  }

  fun clearLogs() {
    _commandLogs.value = emptyList()
  }

  override fun onCleared() {
    super.onCleared()
    stopRepeatJob()
    viewModelScope.launch {
      btManager.disconnect()
    }
  }
}
