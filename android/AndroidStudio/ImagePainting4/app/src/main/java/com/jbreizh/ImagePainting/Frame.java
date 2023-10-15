package com.jbreizh.ImagePainting;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Frame {

    // variable
    private byte[] pixelArray;


    public void setPixelArray(byte[] FrameByte)
    {
        pixelArray = FrameByte;
    }

    public void setFrameByteFromRGB(int pixels, byte[] rgb)
    {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        // put the frame information
        for (int j = 0; j < pixels; j++)
        {
            try
            {
                outputStream.write( rgb );
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        pixelArray = outputStream.toByteArray( );
    }

    public byte[] getWholeFrame(byte[] pattern)
    {
        byte[] wholeFrame = new byte[pattern.length + pixelArray.length];
        System.arraycopy(pattern, 0, wholeFrame, 0, pattern.length);
        System.arraycopy(pixelArray, 0, wholeFrame, pattern.length, pixelArray.length);
        return wholeFrame;
    }

}
