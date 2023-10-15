package com.jbreizh.ImagePainting;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class RollEventListener implements SensorEventListener {

    // TAG debug
    private static String TAG = "RollEventListener";

    // sensors values initialization
    private float[] mGravity = null;
    private float[] mGeomagnetic = null;
    private OnRollListener mListener;

    //
    public void setOnRollListener(OnRollListener listener)
    {
        this.mListener = listener;
    }

    public interface OnRollListener
    {
        // event send when the phone is flat
        void onRollFlat();

        // event send when the phone is flat
        void onRollTurnOn();
        void onRollTurnOff();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //
        if (mListener != null)
        {
            // Sensors values update
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                mGravity = event.values;
            }
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                mGeomagnetic = event.values;
            }

            // calcul the orientation : azimut, pitch and roll
            if (mGravity != null && mGeomagnetic != null)
            {
                //
                float R[] = new float[9];
                float I[] = new float[9];

                //
                boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);

                //
                if (success)
                {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R, orientation);

                    // the phone is roll on the right
                    if (orientation[2] > 1)
                    {
                        mListener.onRollTurnOn();
                    }
                    // the phone is consider "flat"
                    else if (-0.7 < orientation[2] && orientation[2] < 0.7)
                    {
                        mListener.onRollFlat();
                    }
                    // the phone is roll on the left
                    else if (orientation[2] < -1)
                    {
                        mListener.onRollTurnOff();
                    }
                }
            }
        }
    }
}



