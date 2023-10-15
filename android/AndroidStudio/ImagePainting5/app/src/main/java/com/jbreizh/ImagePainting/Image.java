package com.jbreizh.ImagePainting;

import android.graphics.Bitmap;
import android.graphics.Color;

public class Image {
    // variables
    Frame[] FrameArray;

    // constructor from bitmap values
    public Image(Bitmap bitmap)
    {
        // Get the dimension of bitmap (height = pixels)
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // declare the new frameArray
        FrameArray = new Frame[width];

        // for every columns
        for (int x = 0 ; x < width ; x++)
        {
            // declare the new frame
            byte[] frameByte = new byte[height*3];

            // for every pixels
            for (int y = 0 ; y < height ; y++)
            {
                // get the color from the pixel
                int color = bitmap.getPixel(x, y);

                // put the pixel RGB in the frame
                frameByte[3 * y ] = (byte) Color.red(color);
                frameByte[3 * y + 1] = (byte) Color.green(color);
                frameByte[3 * y + 2] = (byte) Color.blue(color);
            }

            // put the frame in the frameArray
            FrameArray[x] = new Frame(frameByte);
        }
    }

}
