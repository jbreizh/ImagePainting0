package com.jbreizh.ImagePainting;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class ShakeEventListener implements SensorEventListener {

    private static String TAG = "ShakeEventListener";
    private static float SHAKE_GRAVITY = 2.7F;
    private static int SHAKE_MIN_TIME_MS = 500;

    private OnShakeListener mListener;
    private long mShakeTimestamp;


    public void setOnShakeListener(OnShakeListener listener)
    {
        this.mListener = listener;
    }

    public interface OnShakeListener
    {
        public void onShake();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (mListener != null)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            double gForce = Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_GRAVITY)
            {
                // get the current time
                final long now = System.currentTimeMillis();

                // ignore shake events too close to each other (500ms)
                if (mShakeTimestamp + SHAKE_MIN_TIME_MS > now)
                {
                    return;
                }

                //
                mShakeTimestamp = now;
                mListener.onShake();
            }
        }
    }

    public void setSHAKE_GRAVITY(float sHAKE_GRAVITY)
    {
        SHAKE_GRAVITY = sHAKE_GRAVITY;
    }

    public float getSHAKE_GRAVITY()
    {
        return SHAKE_GRAVITY;
    }

    public void setSHAKE_MIN_TIME_MS(int sHAKE_MIN_TIME_MS)
    {
        SHAKE_MIN_TIME_MS = sHAKE_MIN_TIME_MS;
    }

    public float getSHAKE_MIN_TIME_MS()
    {
        return SHAKE_MIN_TIME_MS;
    }

}

