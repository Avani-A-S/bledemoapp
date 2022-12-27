package com.example.mybleapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private var bluetoothAdapter: BluetoothAdapter? = null
    private val deviceList : ArrayList<BluetoothDevice> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if(bluetoothAdapter == null) {
            Toast.makeText(this, "this device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }
        else {
            Toast.makeText(this, "this device supports bluetooth", Toast.LENGTH_SHORT).show()
        }

        val btn: Button = findViewById<Button>(R.id.clickBtn)
        btn.setOnClickListener {
            val simpleDate = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = simpleDate.format(Date())
            var text = hello("Date: $currentDate");
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        }

        val scanBtn: Button = findViewById<Button>(R.id.scanBtn)
        scanBtn.setOnClickListener {
            val simpleDate = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = simpleDate.format(Date())
            var text = scan();
            Toast.makeText(this@MainActivity, text, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        val btn: Button = findViewById<Button>(R.id.clickBtn)
        btn.setOnClickListener(null);

        val scanBtn: Button = findViewById<Button>(R.id.scanBtn)
        scanBtn.setOnClickListener(null);

        super.onDestroy()
    }


    private external fun hello(to: String): String

    private external fun scan(): String

    init {
        System.loadLibrary("rust_android")
    }
}