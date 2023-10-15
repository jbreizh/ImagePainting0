package com.jbreizh.ImagePainting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;

import java.io.FileNotFoundException;

public class BitmapTraitement {

    private static String TAG = "BitmapTraitement";



    // resize bitmap at given height keepin the aspect ration
    public static Bitmap scaleBitmap(Bitmap bitmap, int requireHeight)
    {
        Log.d(TAG,"scaleBitmap");
        // get dimension
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        Log.d(TAG,"actual : "+bitmapHeight + "/ require : "+requireHeight);
        // calculate new width to keep ratio
        int newWidth = (bitmapWidth * requireHeight)/bitmapHeight;

        // return the scale bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, requireHeight, true);
    }

    // pre scale the image before fine resize (avoid high memory usage)
    public static Bitmap preScaleBitmapFromUri(Context context, Uri selectedImage, int requireHeight) throws FileNotFoundException
    {
        Log.d(TAG, "preScaleBitmapFromUri");
        // Decode the actual image height
        BitmapFactory.Options inputOption = new BitmapFactory.Options();
        inputOption.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, inputOption);
        int bitmapHeight = inputOption.outHeight;
        Log.d(TAG,"actual : "+bitmapHeight + "/ require : "+requireHeight);

        // Find the correct scale value. It should be the power of 2.
        int scale = 1;
        while (true) {
            if (bitmapHeight / 2 < requireHeight) {
                break;
            }
            bitmapHeight /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options outputOption = new BitmapFactory.Options();
        outputOption.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, outputOption);
    }

    public static Bitmap curtains(Bitmap bitmap, int indexStart , int indexStop )
    {
        // get dimension
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // create a copy of bitmap mutable
        Bitmap bitmapCurtain = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapCurtain);
        Paint paint = new Paint();

        // draw red rectangle on the bitmap
        paint.setColor(Color.RED);
        canvas.drawRect(0, 0, indexStart, bitmapHeight, paint);
        canvas.drawRect(indexStop +1, 0, bitmapWidth, bitmapHeight, paint);

        return bitmapCurtain;
    }

}
