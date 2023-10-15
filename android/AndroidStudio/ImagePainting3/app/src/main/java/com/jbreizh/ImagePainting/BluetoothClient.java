package com.jbreizh.ImagePainting;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.UUID;

public class BluetoothClient {

    String blueToothAddress;
    BluetoothSocket bluetoothSocket;

    //constructor
    public BluetoothClient(String address)
    {
        blueToothAddress = address;
        bluetoothSocket = null;
    }


    // Create the socket
    public void  connect()
    {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> async_connect = new AsyncTask<Void, Void, Void>() {
            private BluetoothAdapter myBluetooth;
            private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

            @Override
            protected Void doInBackground(Void... devices)
            {
                try {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(blueToothAddress);//connects to the device's address and checks if it's available
                    bluetoothSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();//start connection

                } catch (IOException e) {

                    bluetoothSocket = null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }
        };

        async_connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // disconnect
    public void disconnect()
    {
        // if the socket is open we close it
        if (bluetoothSocket != null)
        {
            try
            {
                bluetoothSocket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        // we nullify the socket
        bluetoothSocket = null;
    }

    // send
    public void send(byte[] frameByte)
    {
        if (bluetoothSocket != null)
        {
            try
            {
                bluetoothSocket.getOutputStream().write(frameByte);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


}
