package com.jbreizh.ImagePainting;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BluetoothClient {
    //debug
    private static String TAG="BluetoothClient";

    //variable
    private String blueToothAddress;
    private BluetoothSocket bluetoothSocket;

    private OnBluetoothClientListener mListener;

    //listener
    public void setOnBluetoothClientListener(OnBluetoothClientListener listener)
    {
        this.mListener = listener;
    }

    public interface OnBluetoothClientListener
    {
        // event send when the phone is flat
        void connect(boolean success);
        void disconnect(boolean success);
    }

    // set the address
    public void setAddress(String address)
    {
        blueToothAddress = address;
    }

    // get the socket
    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }

    // Create the socket
    public void  connect()
    {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> async_connect = new AsyncTask<Void, Void, Void>() {
            private BluetoothAdapter myBluetooth;
            private final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            boolean success =true;
            @Override
            protected Void doInBackground(Void... devices)
            {
                try
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(blueToothAddress);//connects to the device's address and checks if it's available
                    bluetoothSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    bluetoothSocket.connect();//start connection

                }
                catch (IOException e)
                {
                    success =false;
                    bluetoothSocket = null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);

                if (mListener != null)
                {
                    mListener.connect(success);
                }
                else
                {
                    Log.d(TAG, "mlistener = null");
                }
            }
        };

        async_connect.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    // disconnect
    public void disconnect()
    {
        boolean success = true;
        // if the socket is open we close it
        if (bluetoothSocket != null)
        {
            try
            {
                bluetoothSocket.close();
            }
            catch (IOException e)
            {
                success = false;
                bluetoothSocket = null;
            }
        }
        else
        {
            success =false;
        }

        // listener event
        if (mListener != null)
        {
            mListener.disconnect(success);
        }
        else
        {
            Log.d(TAG, "mlistener = null");
        }
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
