package com.jbreizh.ImagePainting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;


public class SettingsScreen extends AppCompatActivity {
    //widgets
    Button btnPixels1, btnPixels2, btnRepeat1, btnRepeat2, btnBrightness1, btnBrightness2, btnDelay1, btnDelay2 , btnSensibility1, btnSensibility2, btnReturn;
    SeekBar seekBarPixels, seekBarRepeat, seekBarBrightness, seekBarDelay, seekBarSensibility;

    //settings
    int pixels, repeat, brightness, delay;
    float sensibility;
    public static String EXTRA_PIXELS = "settings_pixels";
    public static String EXTRA_REPEAT = "settings_repeat";
    public static String EXTRA_BRIGHTNESS = "settings_brightness";
    public static String EXTRA_DELAY = "settings_delay";
    public static String EXTRA_SENSIBILITY = "settings_sensibility";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        // Settings
        final Intent data = getIntent();
        pixels = data.getIntExtra(EXTRA_PIXELS, 60);
        repeat = data.getIntExtra(EXTRA_REPEAT, 3);
        brightness = data.getIntExtra(EXTRA_BRIGHTNESS, 40);
        delay = data.getIntExtra(EXTRA_DELAY, 40);
        sensibility = data.getFloatExtra(EXTRA_SENSIBILITY, 3.2f);

        //widgets
        btnPixels1 = findViewById(R.id.buttonPixels1);
        btnPixels2 = findViewById(R.id.buttonPixels2);
        btnRepeat1 = findViewById(R.id.buttonRepeat1);
        btnRepeat2 = findViewById(R.id.buttonRepeat2);
        btnBrightness1 = findViewById(R.id.buttonBrightness1);
        btnBrightness2 = findViewById(R.id.buttonBrightness2);
        btnDelay1 = findViewById(R.id.buttonDelay1);
        btnDelay2 = findViewById(R.id.buttonDelay2);
        btnSensibility1 = findViewById(R.id.buttonSensibiltity1);
        btnSensibility2 = findViewById(R.id.buttonSensibiltity2);
        btnReturn = findViewById(R.id.buttonReturn);
        seekBarPixels = findViewById(R.id.seekBarPixels);
        seekBarRepeat = findViewById(R.id.seekBarRepeat);
        seekBarBrightness = findViewById(R.id.seekBarBrightness);
        seekBarDelay = findViewById(R.id.seekBarDelay);
        seekBarSensibility = findViewById(R.id.seekBarSensibiltity);

        // assign to widgets
        seekBarPixels.setProgress(pixels);
        btnPixels2.setText(String.valueOf(pixels));
        seekBarRepeat.setProgress(repeat);
        btnRepeat2.setText(String.valueOf(repeat));
        seekBarBrightness.setProgress(brightness);
        btnBrightness2.setText(String.valueOf(brightness));
        seekBarDelay.setProgress(delay);
        btnDelay2.setText(String.valueOf(delay));
        seekBarSensibility.setProgress((int)sensibility*10);
        btnSensibility2.setText(String.valueOf(sensibility));

        // buttons actions
        btnPixels1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                msg("Number of pixel in your led strip.\n Unit : pixel.");
            }
        });

        btnRepeat1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                msg("Number of repeat or bounce.");
            }
        });

        btnBrightness1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                msg("Brightness of your led.");
            }
        });

        btnDelay1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                msg("Delay between each frame.\n Unit : millisecond.");
            }
        });

        btnSensibility1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                msg("Shake sensibility.\n Unit : m/s².");
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeScreen();
            }
        });

        // seekbars actions
        seekBarPixels.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                // upgrade pixels and his label
                if (fromUser)
                {
                    pixels = progress;
                    btnPixels2.setText(String.valueOf(pixels));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        seekBarRepeat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                // upgrade repeat and his label
                if (fromUser)
                {
                    repeat = progress;
                    btnRepeat2.setText(String.valueOf(repeat));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                // upgrade repeat and his label
                if (fromUser)
                {
                    brightness = progress;
                    btnBrightness2.setText(String.valueOf(brightness));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        seekBarDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                // upgrade repeat and his label
                if (fromUser)
                {
                    delay = progress;
                    btnDelay2.setText(String.valueOf(delay));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        seekBarSensibility.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                // upgrade repeat and his label
                if (fromUser)
                {
                    sensibility = (float) progress/10;
                    btnSensibility2.setText(String.valueOf(sensibility));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

    }

    // close the activity
    private void closeScreen()
    {
        // put the information for the activity
        Intent data = new Intent();
        data.putExtra(EXTRA_PIXELS, pixels);
        data.putExtra(EXTRA_REPEAT, repeat);
        data.putExtra(EXTRA_BRIGHTNESS, brightness);
        data.putExtra(EXTRA_DELAY, delay);
        data.putExtra(EXTRA_SENSIBILITY, sensibility);

        // finish the activity sending data back
        setResult(RESULT_OK,data);
        finish();
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

}