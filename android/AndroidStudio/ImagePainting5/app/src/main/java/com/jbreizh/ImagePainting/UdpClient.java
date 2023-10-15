package com.jbreizh.ImagePainting;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

public class UdpClient
{
    private String udpAddress;
    private int udpPort;
    public DatagramSocket udpSocket;
    private InetAddress udpHost;


    // constructor
    public UdpClient(String udpAddress, int udpPort)
    {
        this.udpAddress = udpAddress;
        this.udpPort = udpPort;
        this.udpSocket = null;
        this.udpHost = null;
    }

    // create a socket
    public void connect()
    {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> async_client = new  AsyncTask<Void, Void, Void>()
        {
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
                    e.printStackTrace();
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

    // disconnect
    public void disconnect()
    {
        // if the socket is open we close it
        if (udpSocket != null)
        {
            udpSocket.close();
        }
        // we nullify the socket
        udpSocket = null;
        udpHost = null;
    }

    // send
    public void send(final byte[] frameByte)
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

