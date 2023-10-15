package com.jbreizh.ImagePainting;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Image {

    // variables
    private Frame[] frameArray;

    // set the frameArray from bitmap
    public void setFrameArrayFromBitmap(Bitmap bitmap)
    {
        // Get the dimension of bitmap (height = pixels)
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // declare the new frameArray
        frameArray = new Frame[width];

        // for every columns
        for (int x = 0 ; x < width ; x++)
        {
            // declare the new pixelArray
            byte[] pixelArray = new byte[height*3];

            // for every pixels
            for (int y = 0 ; y < height ; y++)
            {
                // get the color from the pixel
                int color = bitmap.getPixel(x, y);

                // put the pixel RGB in the pixelArray
                pixelArray[3 * y ] = (byte) Color.red(color);
                pixelArray[3 * y + 1] = (byte) Color.green(color);
                pixelArray[3 * y + 2] = (byte) Color.blue(color);
            }

            // put the frame in the frameArray
            frameArray[x] = new Frame();
            frameArray[x].setPixelArray(pixelArray);

        }
    }

    //
    public Frame getFrameFromIndex(int index)
    {
        return frameArray[index];
    }

    //
    public Frame[] getFrameArray()
    {
        return frameArray;
    }

}
