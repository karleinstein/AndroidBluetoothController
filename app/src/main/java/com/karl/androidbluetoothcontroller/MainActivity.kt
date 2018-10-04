package com.karl.androidbluetoothcontroller

import android.app.Activity
import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.net.ServerSocket
import java.nio.charset.Charset
import java.util.*
import android.R.id.message
import android.bluetooth.BluetoothSocket
import android.os.Build
import android.bluetooth.BluetoothDevice
import java.io.IOException


class MainActivity : AppCompatActivity(), View.OnClickListener
        , RcvPairedAdapter.OnItemClickListener {
    private var onItemSelected: Int = 0
    override fun OnItemClicked(position: Int) {
        onItemSelected = position
        val tempAddress = listPairedDevice[position].address

//        } else {
        //Log.d("fuck", tempAddress)
        Toast.makeText(this, "Trash Mode Enabled", Toast.LENGTH_SHORT).show()
        sendData("1")
//        }
        if (tempAddress != myAddress) {
            sendData("2")
            Toast.makeText(this, "Trash Mode Disabled", Toast.LENGTH_SHORT).show()

        }
    }

    private lateinit var listPairedDevice: ArrayList<Model>
    private var bluetoothHeadset: BluetoothHeadset? = null
    private var outputStream: OutputStream? = null
    private var uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private val bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private lateinit var listDiscoverDevice: ArrayList<String>
    private var bluetoothSocket: BluetoothSocket? = null
    private var myAddress: String = "00:18:E5:03:98:EC"

    companion object {
        const val REQUEST_BLUETOOTH = 8080

    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.txtFeatures -> {
            }
        }
    }


    private var receiverDevice = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            val device: BluetoothDevice = intent!!.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            val deviceName = device.name
            Toast.makeText(this@MainActivity, deviceName, Toast.LENGTH_SHORT).show()
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    //val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    //val deviceName = device.name
                    //listDiscoverDevice.add(deviceName)
                    //Toast.makeText(this@MainActivity, deviceName, Toast.LENGTH_SHORT).show()
                }
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    val devicez: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val uuidName = devicez.name
                    Log.d("fuck", uuidName)
                }

            }
            if (listDiscoverDevice.size == 0) {

            }
//            val adapter = RcvPairedAdapter(this@MainActivity, listDiscoverDevice)
//            rcvShowPaired.layoutManager = LinearLayoutManager(this@MainActivity)
//            rcvShowPaired.adapter = adapter
        }

    }
    private var profileListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothHeadset.HEADSET) {
                bluetoothHeadset = null
            }
        }

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            if (profile == BluetoothHeadset.HEADSET) {
                bluetoothHeadset = proxy as BluetoothHeadset
                for (device in proxy.connectedDevices) {
                    val nameDevice = device.name
                    val address = device.address
                    Toast.makeText(this@MainActivity, address, Toast.LENGTH_SHORT).show()
                    Log.d("fuck", address)
                }
            }
            BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, proxy)

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiverDevice)
        bluetoothSocket?.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        listPairedDevice = ArrayList()
        txtFeatures.setOnClickListener(this)
        listDiscoverDevice = ArrayList()


        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Your device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
            return
        }

        bluetoothAdapter.getProfileProxy(this, profileListener, BluetoothHeadset.HEADSET)
        if (!bluetoothAdapter.isEnabled) {
            val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetooth, REQUEST_BLUETOOTH)
        } else {
            registerReceiver(receiverDevice, filter)
            showDevicePaired()
            if (bluetoothSocket == null) {

                val device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(myAddress)
                //create RFCOMM
                bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(uuid)
                bluetoothAdapter.cancelDiscovery()
                try {
                    bluetoothSocket?.connect()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    bluetoothSocket?.close()
                }


            } else {
                bluetoothSocket?.close()
            }

        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BLUETOOTH && resultCode == Activity.RESULT_OK) {
            showDevicePaired()
        }
    }

    private fun showDevicePaired() {
        val pairedDevice: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
        pairedDevice?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address
            listPairedDevice.add(Model(deviceName, deviceHardwareAddress))

        }

        val adapter = RcvPairedAdapter(this, listPairedDevice)
        adapter.setOnItemClickListener(this)
        rcvShowPaired.layoutManager = LinearLayoutManager(this)
        rcvShowPaired.adapter = adapter
        if (listPairedDevice.size > 0) {

        }
    }


    private fun sendData(message: String) {
        val buffer = message.toByteArray()
        try {
            outputStream = bluetoothSocket!!.outputStream
            outputStream!!.write(buffer)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }


    }


}
