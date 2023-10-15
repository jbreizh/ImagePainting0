package com.jbreizh.ImagePainting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

public class UdpClient {
    //debug
    private static String TAG = "UdpClient";

    // variable
    private String udpAddress;
    private int udpPort;
    private DatagramSocket udpSocket;
    private InetAddress udpHost;

    // listener
    private OnUdpClientListener mListener;

    public void setOnUdpClientListener(OnUdpClientListener listener)
    {
        this.mListener = listener;
    }

    public interface OnUdpClientListener
    {
        // event send when the phone is flat
        public void connect(boolean success);
        public void disconnect(boolean success);
    }


    // set the address
    public void setAddress(String udpAddress)
    {
        this.udpAddress = udpAddress;

    }

    // set the port
    public void setPort(int udpPort)
    {
        this.udpPort = udpPort;
    }

    // get the socket
    public DatagramSocket getUdpSocket() {
        return udpSocket;
    }


    // create a socket
    public void connect()
    {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> async_client = new  AsyncTask<Void, Void, Void>()
        {
            boolean success =true;
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    udpSocket = new DatagramSocket();
                    udpHost = InetAddress.getByName(udpAddress);
                }
                catch (Exception e)
                {
                    success =false;
                    udpHost = null;
                    udpSocket = null;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
                if (mListener != null) {
                    mListener.connect(success);
                }
                else
                {
                    Log.d(TAG, "mlistener = null");
                }

            }
        };


        async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    // disconnect
    public void disconnect()
    {
        boolean success = true;
        // if the socket is open we close it
        if (udpSocket != null)
        {
            udpSocket.close();
            udpSocket = null;
            udpHost = null;
        }
        else
        {
            success = false;
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
    public void send(final byte[] frameByte)
    {
        // create the packet
        DatagramPacket outPacket = new DatagramPacket(frameByte, frameByte.length, udpHost, udpPort);

        // send it if the socket is up
        if (udpSocket != null) {
            try
            {
                udpSocket.send(outPacket);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    // to send from the ui thread
    public void asyncSend(final byte[] frameByte)
    {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> async_client = new  AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                // create the packet
                DatagramPacket outPacket = new DatagramPacket(frameByte, frameByte.length, udpHost, udpPort);

                // send it if the socket is up
                if (udpSocket != null)
                {
                    try
                    {
                        udpSocket.send(outPacket);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result)
            {
                super.onPostExecute(result);
            }
        };

        async_client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


}

