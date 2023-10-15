package com.jbreizh.ImagePainting;


public class Frame {
    // variable
    byte[] frameByte;

    // constructor from fixed values
    public Frame(int pixels, int r, int g, int b)
    {
        // declare the message
        frameByte = new byte[(pixels+1)*3];

        // put the frame declaration
        frameByte[0] = ((byte) 'F');
        frameByte[1] = ((byte) 'R');
        frameByte[2] = ((byte) 'M');

        // put the frame information
        for (int j = 1; j <= pixels; j++)
        {
            frameByte[3 * j] = ((byte) r);
            frameByte[3 * j + 1] = ((byte) g);
            frameByte[3 * j + 2] = ((byte) b);
        }
    }

    // constructor from image values
    public Frame(byte[] frameByteTemp)
    {
        // create a new frame byte
        frameByte = new byte[frameByteTemp.length+3];

        // put the frame declaration
        frameByte[0]= (byte) 'F';
        frameByte[1]= (byte) 'R';
        frameByte[2]= (byte) 'M';

        // put the data in the frame byte
        for (int j = 0; j < frameByteTemp.length; j++)
        {
            frameByte[j+3]=frameByteTemp[j];
        }
    }

    // constructor from settings values
    public  Frame(int pixels, int brightness)
    {
        // declare the message
        frameByte = new byte[6];

        // put the frame declaration
        frameByte[0] = ((byte) 'S');
        frameByte[1] = ((byte) 'E');
        frameByte[2] = ((byte) 'T');

        // put the frame information
        frameByte[3] = ((byte) brightness);
        frameByte[4] = ((byte) pixels);
        frameByte[5] = ((byte) 0);
    }

}
