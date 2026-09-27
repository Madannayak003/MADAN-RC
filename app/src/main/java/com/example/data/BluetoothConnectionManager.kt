package com.example.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class BluetoothConnectionManager(private val context: Context) {

  private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
  private var bluetoothSocket: BluetoothSocket? = null
  private var outputStream: OutputStream? = null

  private val _connectionState = MutableStateFlow<BluetoothState>(BluetoothState.Disconnected)
  val connectionState: StateFlow<BluetoothState> = _connectionState.asStateFlow()

  // Standard SPP UUID for HC-05 / HC-06 Bluetooth Classic modules
  private val sppUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

  fun isBluetoothSupported(): Boolean = bluetoothAdapter != null

  fun isBluetoothEnabled(): Boolean = bluetoothAdapter?.isEnabled == true

  @SuppressLint("MissingPermission")
  fun getPairedDevices(): Set<BluetoothDevice> {
    return if (isBluetoothEnabled() && bluetoothAdapter != null) {
      try {
        bluetoothAdapter.bondedDevices ?: emptySet()
      } catch (e: Exception) {
        Log.e("BTManager", "Error getting bonded devices", e)
        emptySet()
      }
    } else {
      emptySet()
    }
  }

  suspend fun connect(device: BluetoothDevice): Boolean = withContext(Dispatchers.IO) {
    _connectionState.value = BluetoothState.Connecting
    try {
      bluetoothSocket?.close()
    } catch (e: Exception) {
      // ignore
    }

    @SuppressLint("MissingPermission")
    try {
      val socket = device.createRfcommSocketToServiceRecord(sppUuid)
      bluetoothAdapter?.cancelDiscovery()
      socket.connect()
      bluetoothSocket = socket
      outputStream = socket.outputStream
      _connectionState.value = BluetoothState.Connected(device.name ?: "HC-05 / HC-06 Car", device.address)
      true
    } catch (e: IOException) {
      Log.e("BTManager", "Connection failed", e)
      try {
        bluetoothSocket?.close()
      } catch (ex: Exception) {
        // ignore
      }
      bluetoothSocket = null
      outputStream = null
      _connectionState.value = BluetoothState.Error(e.localizedMessage ?: "Connection failed")
      false
    }
  }

  suspend fun disconnect() = withContext(Dispatchers.IO) {
    try {
      try {
        // Send safe stop command before disconnecting
        outputStream?.write('S'.code)
        outputStream?.flush()
      } catch (e: Exception) {
        // ignore
      }
      outputStream?.close()
      bluetoothSocket?.close()
    } catch (e: Exception) {
      Log.e("BTManager", "Error during disconnect", e)
    } finally {
      outputStream = null
      bluetoothSocket = null
      _connectionState.value = BluetoothState.Disconnected
    }
  }

  suspend fun sendCommand(charCommand: Char): Boolean = withContext(Dispatchers.IO) {
    val stream = outputStream
    if (stream == null || _connectionState.value !is BluetoothState.Connected) {
      Log.w("BTManager", "Cannot send command $charCommand: Not connected")
      return@withContext false
    }

    try {
      // Send exactly ONE ASCII character without extra newline or carriage return bytes
      stream.write(charCommand.code)
      stream.flush()
      true
    } catch (e: IOException) {
      Log.e("BTManager", "Failed to send command $charCommand", e)
      _connectionState.value = BluetoothState.Error("Lost connection: ${e.localizedMessage}")
      try {
        bluetoothSocket?.close()
      } catch (ex: Exception) {}
      bluetoothSocket = null
      outputStream = null
      false
    }
  }
}
