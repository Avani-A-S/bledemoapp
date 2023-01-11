package com.example.mybleapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isBlePermissionGranted = false
    private var isBleAdminPermissionGranted = false
    private var isBleScanPermissionGranted = false
    private var isBleAdvertisePermissionGranted = false
    private var isBleConnectPermissionGranted = false
    private var isBleFineLocationPermissionGranted = false
    private var isBleCoarseLocationPermissionGranted = false


    private var bluetoothAdapter: BluetoothAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

// Check for permissions.
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissons ->
            isBlePermissionGranted = permissons[Manifest.permission.BLUETOOTH] ?: isBlePermissionGranted
            isBleAdminPermissionGranted = permissons[Manifest.permission.BLUETOOTH_ADMIN] ?: isBleAdminPermissionGranted
            isBleScanPermissionGranted = permissons[Manifest.permission.BLUETOOTH_SCAN] ?: isBleScanPermissionGranted
            isBleAdvertisePermissionGranted = permissons[Manifest.permission.BLUETOOTH_ADVERTISE] ?: isBleAdvertisePermissionGranted
            isBleConnectPermissionGranted = permissons[Manifest.permission.BLUETOOTH_CONNECT] ?: isBleConnectPermissionGranted
            isBleFineLocationPermissionGranted = permissons[Manifest.permission.ACCESS_FINE_LOCATION] ?: isBleFineLocationPermissionGranted
            isBleCoarseLocationPermissionGranted = permissons[Manifest.permission.ACCESS_COARSE_LOCATION] ?: isBleCoarseLocationPermissionGranted
        }

        requestPermission()

        // Make sure if the device support bluetooth or not.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if(bluetoothAdapter == null) {
            Toast.makeText(this, "This device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        // Check to see if the Bluetooth classic feature is available.
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH) }?.also {
            Toast.makeText(this, "bluetooth_not_supported", Toast.LENGTH_SHORT).show()
            finish()
        }
        // Check to see if the BLE feature is available.
        packageManager.takeIf { it.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) }?.also {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show()
            finish()
        }

        val btn: Button = findViewById<Button>(R.id.scanBtn)

        btn.setOnClickListener {
            requestPermission()
            var devices = scan();   // Calling Rust library method to start scanning for bluetooth device
            val countLabel: TextView = findViewById<TextView>(R.id.countLabel)

            if (devices != "") {
                val deviceList = devices.split("|")
                deviceList[0].trim()

                countLabel.text = "Found "+ deviceList.size + " Devices. Click to Pair."

                var arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList)
                val listView: ListView = findViewById<ListView>(R.id.deviceList)
                listView.adapter = arrayAdapter

                listView.setOnItemClickListener { parent, _, position, _ ->
                    val selectedItem = parent.getItemAtPosition(position) as String
                    var result: String = connect(selectedItem)
                    Toast.makeText(this, result, Toast.LENGTH_LONG).show()
                }
            }
            else {
                countLabel.text = "No bluetooth devices found. Try Again!"
            }
        }

    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)

    private fun requestPermission() {
        isBlePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH
        ) == PackageManager.PERMISSION_GRANTED

        isBleAdvertisePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_ADVERTISE
        ) == PackageManager.PERMISSION_GRANTED

        isBleFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        isBleCoarseLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        isBleConnectPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED

        isBleScanPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED

        isBleAdminPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_ADMIN
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest : MutableList<String> = ArrayList()

        if (!isBlePermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH))
        }

        if (!isBleAdvertisePermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH_ADVERTISE))
        }

        if (!isBleFineLocationPermissionGranted) {
            permissionRequest.add((Manifest.permission.ACCESS_FINE_LOCATION))
        }

        if (!isBleCoarseLocationPermissionGranted) {
            permissionRequest.add((Manifest.permission.ACCESS_COARSE_LOCATION))
        }

        if (!isBleConnectPermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH_CONNECT))
        }

        if (!isBleScanPermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH_SCAN))
        }

        if (!isBleAdminPermissionGranted) {
            permissionRequest.add((Manifest.permission.BLUETOOTH_ADMIN))
        }

        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    private external fun connect(to: String): String

    private external fun scan(): String

    private external fun jnionload()

    init {
        System.loadLibrary("rust_android")
        jnionload()
    }
}