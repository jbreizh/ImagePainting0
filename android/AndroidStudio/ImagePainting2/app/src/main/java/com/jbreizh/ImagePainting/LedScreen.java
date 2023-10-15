/*
package com.jbreizh.ImagePainting;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

public class LedScreen extends AppCompatActivity {

    // network Bluetooth or UDP
    int network;

    // Gui
    Button btnLight, btnStop, btnPlay, btnPause, btnSettings , btnUdp, btnBluetooth, btnPixels, btnRepeat, btnBrightness2, btnDelay2, btnCutFrom2, btnCutTo2;
    SeekBar seekBrightness, seekDelay, seekCutFrom, seekCutTo;
    ImageView imageViewImage;
    CheckBox cbRepeat, cbBounce, cbInvert, cbEndOff;

    // UDP
    String serverAddress = "192.168.43.169";
    int serverPort = 6789;
    UdpClient ClientUdp;

    // bluetooth
    String bluetoothAddress = null;
    BluetoothClient ClientBluetooth;

    //settings
    int brightness;
    long delay;
    int pixels, repeat;
    Bitmap bitmap;

    //frame
    Frame frameOn, frameOff;
    Image Image;

    // logic
    private boolean isRunning, isPause;
    int indexStart, index, indexStop;
    int repeatCounter;

    // handler
    private Handler playHandler;
    private PlayRunnable playRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_screen);

        // get the network from the splash screen
        Intent data = getIntent();
        network = data.getIntExtra(SplashScreen.EXTRA_NETWORK, 1);

        // Settings
        brightness =40;
        delay = 50;
        pixels =60;
        repeat = 3;
        bitmap =  BitmapFactory.decodeStream(this.getResources().openRawResource(+R.drawable.pacman));

        // widgets
        btnLight = findViewById(R.id.buttonLight);
        btnStop = findViewById(R.id.buttonStop);
        btnPlay = findViewById(R.id.buttonPlay);
        btnPause = findViewById(R.id.buttonPause);
        btnSettings = findViewById(R.id.buttonSettings);
        btnUdp = findViewById(R.id.buttonUdp);
        btnBluetooth = findViewById(R.id.buttonBluetooth);
        btnPixels =findViewById(R.id.buttonPixels);
        btnRepeat =findViewById(R.id.buttonRepeat);
        seekBrightness = findViewById(R.id.seekBarBrightness);
        btnBrightness2 = findViewById(R.id.buttonBrightness2);
        seekDelay = findViewById(R.id.seekBarDelay);
        btnDelay2 = findViewById(R.id.buttonDelay2);
        imageViewImage = findViewById(R.id.imageViewImage);
        seekCutFrom = findViewById(R.id.seekBarCutFrom);
        btnCutFrom2 = findViewById(R.id.buttonCutFrom2);
        seekCutTo = findViewById(R.id.seekBarCutTo);
        btnCutTo2 = findViewById(R.id.buttonCutTo2);
        cbRepeat = findViewById(R.id.checkBoxRepeat);
        cbBounce = findViewById(R.id.checkBoxBounce);
        cbInvert = findViewById(R.id.checkBoxInvert);
        cbEndOff = findViewById(R.id.checkBoxEndOff);

        // frame
        frameOn = new Frame( pixels,255, 255, 255);
        frameOff = new Frame(pixels,0,0,0);
        Image = new Image(bitmap);

        // logic
        isRunning = false;
        isPause = false;
        repeatCounter = repeat;
        index = 0;
        indexStart = 0;
        indexStop = Image.FrameArray.length-1;

        // handler
        playHandler = new Handler();
        playRunnable = new PlayRunnable();

        // UDP and Bluetooth client initialisation
        ClientUdp = new UdpClient(serverAddress, serverPort);
        ClientBluetooth = new BluetoothClient(bluetoothAddress);

        // assign to widgets
        btnPixels.setText(String.valueOf(pixels));
        btnRepeat.setText(String.valueOf(repeat));
        seekBrightness.setProgress(brightness);
        btnBrightness2.setText(String.valueOf(brightness));
        seekDelay.setProgress((int)delay);
        btnDelay2.setText(String.valueOf(delay));
        imageViewImage.setImageBitmap(bitmap);
        seekCutFrom.setMax(indexStop);
        seekCutTo.setMax(indexStop);
        seekCutFrom.setProgress(indexStart);
        seekCutTo.setProgress(indexStop);
        btnCutFrom2.setText(String.valueOf(indexStart));
        btnCutTo2.setText(String.valueOf(indexStop));

        // adapt widget depending network
        if (network == 1)
        {
            btnUdp.setVisibility(View.GONE);
        }
        else if (network == 2)
        {
            btnBluetooth.setVisibility(View.GONE);
        }

        // actions button
        btnLight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sendWrapper(frameOn.frameByte);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stop();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                play();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pause();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                settingsScreen();
            }
        });

        btnUdp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                udpScreen();
            }
        });

        btnBluetooth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bluetoothScreen();
            }
        });

        // action checkbox
        cbRepeat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(cbRepeat.isChecked())
                {
                    cbBounce.setEnabled(false);
                }else
                {
                    cbBounce.setEnabled(true);
                }
            }
        });

        cbBounce.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(cbBounce.isChecked())
                {
                    cbRepeat.setEnabled(false);
                }else
                {
                    cbRepeat.setEnabled(true);
                }
            }
        });

        //action seeker
        seekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                brightness = progress;
                btnBrightness2.setText(String.valueOf(brightness));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                sendWrapper((new Frame(pixels,brightness)).frameByte);
            }
        });

        seekDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                delay = (long)progress;
                btnDelay2.setText(String.valueOf(delay));
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

        seekCutFrom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    // update seekbar and label without overcross
                    if (progress <= indexStop)
                    {
                        indexStart = progress;
                        btnCutFrom2.setText(String.valueOf(indexStart));
                    }
                    else
                    {
                        seekCutFrom.setProgress(indexStop);
                        btnCutFrom2.setText(String.valueOf(indexStop));
                    }
                }
                // draw curtains
                curtains();
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

        seekCutTo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    // update seekbar and label without overcross
                    if (indexStart <= progress)
                    {
                        indexStop = progress;
                        btnCutTo2.setText(String.valueOf(indexStop));
                    }
                    else
                    {
                        seekCutTo.setProgress(indexStart);
                        btnCutTo2.setText(String.valueOf(indexStart));
                    }
                }
                // draw curtains
                curtains();
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

    // draws curtains on the imageView
    private void curtains()
    {
        // create a copy of bitmap mutable
        Bitmap bitmapCurtain = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapCurtain);
        Paint paint = new Paint();

        // draw red rectangle on the bitmap
        paint.setColor(Color.RED);
        canvas.drawRect(0, 0, indexStart, pixels, paint);
        canvas.drawRect(indexStop +1, 0, Image.FrameArray.length, pixels, paint);

        // put back the result on the imageview
        imageViewImage.setImageBitmap(bitmapCurtain);
    }

    // choose the right send command depending the network
    public void sendWrapper(byte[] frame)
    {
        if(network == 1)
        {
            ClientBluetooth.send(frame);
        }
        else if (network == 2)
        {
            ClientUdp.send(frame);
        }
    }

    // stop
    private void stop()
    {
        // logic
        isRunning =false;
        isPause = false;
        repeatCounter = repeat;

        // send
        playHandler.removeCallbacks(playRunnable);
        sendWrapper(frameOff.frameByte);
    }

    // play
    private void play()
    {
        // if it's not running
        if (!isRunning)
        {
            // position
            if(!isPause)
            {
                //logic
                repeatCounter = repeat;
                if (cbInvert.isChecked())
                {
                    index = indexStop;
                }
                else
                {
                    index =indexStart;
                }
            }

            // logic
            isRunning = true;
            isPause = false;

            // launch the handler
            playHandler.postDelayed(playRunnable, delay);

        }
    }

    // pause
    private void pause()
    {
        // if it's running
        if (isRunning)
        {
            // logic
            isRunning =false;
            isPause = true;

            // cancel the handler
            playHandler.removeCallbacks(playRunnable);

            // send
            if(cbEndOff.isChecked())
            {
                sendWrapper(frameOff.frameByte);
            }
        }
    }

    // launch SettingsScreen
    private void settingsScreen()
    {
        // put the information for the activity
        Intent data = new Intent(this, SettingsScreen.class);
        data.putExtra(SettingsScreen.EXTRA_PIXELS, pixels);
        data.putExtra(SettingsScreen.EXTRA_REPEAT, repeat);
        data.putExtra(SettingsScreen.EXTRA_SELECTIMAGE, bitmap);
        // Change the activity
        startActivityForResult(data,1);
    }

    // launch UdpScreen
    private void udpScreen()
    {
        // put the information for the activity
        Intent data = new Intent(this, UdpScreen.class);
        data.putExtra(SettingsScreen.EXTRA_PIXELS, pixels);
        data.putExtra(UdpScreen.EXTRA_SERVERADDRESS, serverAddress);
        data.putExtra(UdpScreen.EXTRA_SERVERPORT, serverPort);
        // Change the activity
        startActivityForResult(data,2);
    }

    // launch BluetoothScreen
    private void bluetoothScreen()
    {
        if (ClientBluetooth.bluetoothSocket==null)
        {// put the information for the activity
            Intent data = new Intent(this, BluetoothScreen.class);
            data.putExtra(BluetoothScreen.EXTRA_ADDRESS, bluetoothAddress);
            // Change the activity
            startActivityForResult(data,3);
        }
        else
        {
            // disconnection
            ClientBluetooth.disconnect();

            // change bluetooth icon
            btnBluetooth.setBackgroundResource(R.drawable.bluetoothc);
        }
    }


    // grab back information from settings screen
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // from settings screen
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                // get data
                pixels = data.getIntExtra(SettingsScreen.EXTRA_PIXELS, 60);
                repeat = data.getIntExtra(SettingsScreen.EXTRA_REPEAT, 3);
                bitmap = data.getParcelableExtra(SettingsScreen.EXTRA_SELECTIMAGE);

                // frame
                frameOn = new Frame( pixels,255, 255, 255);
                frameOff = new Frame(pixels,0,0,0);
                Image = new Image(bitmap);

                // logic
                isRunning = false;
                isPause = false;
                index = 0;
                indexStart = 0;
                indexStop = Image.FrameArray.length-1;

                // assign to widgets
                btnPixels.setText(String.valueOf(pixels));
                btnRepeat.setText(String.valueOf(repeat));
                imageViewImage.setImageBitmap(bitmap);
                seekCutFrom.setMax(indexStop);
                seekCutTo.setMax(indexStop);
                seekCutFrom.setProgress(indexStart);
                seekCutTo.setProgress(indexStop);
                btnCutFrom2.setText(String.valueOf(indexStart));
                btnCutTo2.setText(String.valueOf(indexStop));
            }
            else
            {
                msg("Settings not Changed.");
            }
        }
        // from udp screen
        else if (requestCode == 2)
        {
            if (resultCode == RESULT_OK)
            {
                // get data
                serverAddress = data.getStringExtra(UdpScreen.EXTRA_SERVERADDRESS);
                serverPort = data.getIntExtra(UdpScreen.EXTRA_SERVERPORT, 6789);

                //UDP Client
                ClientUdp = new UdpClient(serverAddress, serverPort);
            }
            else
            {
                msg("UDP settings not Changed.");
            }
        }
        // from bluetooth screen
        else if (requestCode == 3)
        {
            if (resultCode == RESULT_OK)
            {
                //get data
                bluetoothAddress = data.getStringExtra(BluetoothScreen.EXTRA_ADDRESS);

                // disconnection
                ClientBluetooth.disconnect();

                // change bluetooth icon
                btnBluetooth.setBackgroundResource(R.drawable.bluetoothc);

                //Create the new bluetooth task
                ClientBluetooth = new BluetoothClient(bluetoothAddress);
                ClientBluetooth.connect();

                // change bluetooth icon
                btnBluetooth.setBackgroundResource(R.drawable.bluetoothd);
            }
            else
            {
                msg("Bluetooth settings not Changed.");
            }
        }
        // from nowhere !!!
        else
        {
            msg("Error.");
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    //
    public class PlayRunnable implements Runnable {
        @Override
        public void run()
        {
            // if the index is in the limit
            if (indexStart<= index && index <= indexStop)
            {
                // send
                sendWrapper(Image.FrameArray[index].frameByte);
                //logic
                isRunning = true;
                // position
                if (cbInvert.isChecked())
                {
                    index -=1;
                }
                else
                {
                    index +=1;
                }
                // Repeat this the same runnable code block again
                playHandler.postDelayed(this, delay);
            }
            // the index is out of the limit
            else
            {
                // if repeat or bounce is check, go again
                if ((cbRepeat.isChecked() || cbBounce.isChecked()) && repeatCounter > 1)
                {
                    // logic
                    isRunning = true;
                    repeatCounter-=1;
                    if (cbBounce.isChecked())
                    {
                        cbInvert.setChecked(!cbInvert.isChecked());
                    }
                    // position
                    if (cbInvert.isChecked())
                    {
                        index = indexStop;
                    }
                    else
                    {
                        index =indexStart;
                    }
                    // send
                    playHandler.postDelayed(playRunnable, delay);
                }
                // if repeat or bounce isn't check, stop
                else
                {
                    // logic
                    isRunning = false;
                    // send
                    if(cbEndOff.isChecked())
                    {
                        sendWrapper(frameOff.frameByte);
                    }
                }
            }
        }
    }


}*/



package com.jbreizh.ImagePainting;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;


public class LedScreen extends AppCompatActivity {


    // shake
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity



    // network Bluetooth or UDP
    private int network;

    // Gui
    private Button btnLight, btnStop, btnPlay, btnPause, btnSettings , btnUdp, btnBluetooth, btnPixels, btnRepeat, btnBrightness2, btnDelay2, btnCutFrom2, btnCutTo2;
    private SeekBar seekBrightness, seekDelay, seekCutFrom, seekCutTo;
    private ImageView imageViewImage;
    private CheckBox cbRepeat, cbBounce, cbInvert, cbEndOff;

    // UDP
    private String udpAddress = "192.168.43.169";
    private int udpPort = 6789;
    private UdpClient ClientUdp;

    // bluetooth
    private String bluetoothAddress = null;
    private BluetoothClient ClientBluetooth;

    //settings
    private int brightness, pixels, repeat;
    private long delay;
    private Bitmap bitmap;
    private Uri bitmapUri;

    //frame
    private Frame frameOn, frameOff;
    private Image Image;

    // logic
    private boolean isRunning, isPause;
    private boolean isRepeat, isBounce, isInvert, isEndOff;
    private int indexStart, index, indexStop;
    private int repeatCounter;

    // timer
    private Timer playTimer;
    private  PlayTask playTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_screen);

        // get the network from the splash screen
        Intent data = getIntent();
        network = data.getIntExtra(SplashScreen.EXTRA_NETWORK, 1);

        // Settings
        brightness =40;
        delay = 50;
        pixels =60;
        repeat = 3;
        bitmap =  BitmapFactory.decodeStream(this.getResources().openRawResource(+R.drawable.pacman));
        bitmapUri = Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +R.drawable.pacman);

        // widgets
        btnLight = findViewById(R.id.buttonLight);
        btnStop = findViewById(R.id.buttonStop);
        btnPlay = findViewById(R.id.buttonPlay);
        btnPause = findViewById(R.id.buttonPause);
        btnSettings = findViewById(R.id.buttonSettings);
        btnUdp = findViewById(R.id.buttonUdp);
        btnBluetooth = findViewById(R.id.buttonBluetooth);
        btnPixels =findViewById(R.id.buttonPixels);
        btnRepeat =findViewById(R.id.buttonRepeat);
        seekBrightness = findViewById(R.id.seekBarBrightness);
        btnBrightness2 = findViewById(R.id.buttonBrightness2);
        seekDelay = findViewById(R.id.seekBarDelay);
        btnDelay2 = findViewById(R.id.buttonDelay2);
        imageViewImage = findViewById(R.id.imageViewImage);
        seekCutFrom = findViewById(R.id.seekBarCutFrom);
        btnCutFrom2 = findViewById(R.id.buttonCutFrom2);
        seekCutTo = findViewById(R.id.seekBarCutTo);
        btnCutTo2 = findViewById(R.id.buttonCutTo2);
        cbRepeat = findViewById(R.id.checkBoxRepeat);
        cbBounce = findViewById(R.id.checkBoxBounce);
        cbInvert = findViewById(R.id.checkBoxInvert);
        cbEndOff = findViewById(R.id.checkBoxEndOff);

        // frame
        frameOn = new Frame( pixels,255, 255, 255);
        frameOff = new Frame(pixels,0,0,0);
        Image = new Image(bitmap);

        // logic
        synchronizeLogic();

        //position
        index = 0;
        indexStart = 0;
        indexStop = Image.FrameArray.length-1;

        // Timer
        playTimer = new Timer();
        playTask = new PlayTask();

        // UDP and Bluetooth client initialisation
        ClientUdp = new UdpClient(udpAddress, udpPort);
        ClientBluetooth = new BluetoothClient(bluetoothAddress);

        // assign to widgets
        btnPixels.setText(String.valueOf(pixels));
        btnRepeat.setText(String.valueOf(repeat));
        seekBrightness.setProgress(brightness);
        btnBrightness2.setText(String.valueOf(brightness));
        seekDelay.setProgress((int)delay);
        btnDelay2.setText(String.valueOf(delay));
        imageViewImage.setImageBitmap(bitmap);
        seekCutFrom.setMax(indexStop);
        seekCutTo.setMax(indexStop);
        seekCutFrom.setProgress(indexStart);
        seekCutTo.setProgress(indexStop);
        btnCutFrom2.setText(String.valueOf(indexStart));
        btnCutTo2.setText(String.valueOf(indexStop));

        // adapt widget depending network
        if (network == 1)
        {
            btnUdp.setVisibility(View.GONE);
        }
        else if (network == 2)
        {
            btnBluetooth.setVisibility(View.GONE);
        }

        // actions button
        btnLight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                asyncSendWrapper(frameOn.frameByte);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                stop();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                play();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                pause();
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                settingsScreen();
            }
        });

        btnUdp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                udpScreen();
            }
        });

        btnBluetooth.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                bluetoothScreen();
            }
        });

        // action checkbox
        cbRepeat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(cbRepeat.isChecked())
                {
                    cbBounce.setEnabled(false);
                }
                else
                {
                    cbBounce.setEnabled(true);
                }
            }
        });

        cbBounce.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(cbBounce.isChecked())
                {
                    cbRepeat.setEnabled(false);
                }
                else
                {
                    cbRepeat.setEnabled(true);
                }
            }
        });



        //action seeker
        seekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                brightness = progress;
                btnBrightness2.setText(String.valueOf(brightness));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                asyncSendWrapper((new Frame(pixels,brightness)).frameByte);
            }
        });

        seekDelay.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                delay = (long)progress;
                btnDelay2.setText(String.valueOf(delay));
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

        seekCutFrom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    // update seekbar and label without overcross
                    if (progress <= indexStop)
                    {
                        indexStart = progress;
                        btnCutFrom2.setText(String.valueOf(indexStart));
                    }
                    else
                    {
                        seekCutFrom.setProgress(indexStop);
                        btnCutFrom2.setText(String.valueOf(indexStop));
                    }
                }
                // draw curtains
                curtains();
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

        seekCutTo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    // update seekbar and label without overcross
                    if (indexStart <= progress)
                    {
                        indexStop = progress;
                        btnCutTo2.setText(String.valueOf(indexStop));
                    }
                    else
                    {
                        seekCutTo.setProgress(indexStart);
                        btnCutTo2.setText(String.valueOf(indexStart));
                    }
                }
                // draw curtains
                curtains();
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

        shake();

    }

    // draws curtains on the imageView
    private void curtains()
    {
        // create a copy of bitmap mutable
        Bitmap bitmapCurtain = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmapCurtain);
        Paint paint = new Paint();

        // draw red rectangle on the bitmap
        paint.setColor(Color.RED);
        canvas.drawRect(0, 0, indexStart, pixels, paint);
        canvas.drawRect(indexStop +1, 0, Image.FrameArray.length, pixels, paint);

        // put back the result on the imageview
        imageViewImage.setImageBitmap(bitmapCurtain);
    }

    // choose the right send command depending the network
    public void sendWrapper(byte[] frame)
    {
        if(network == 1)
        {
            ClientBluetooth.send(frame);
        }
        else if (network == 2)
        {
            ClientUdp.send(frame);
        }
    }

    // choose the right send command depending the network (async version for android network policy)
    public void asyncSendWrapper(byte[] frame)
    {
        if(network == 1)
        {
            ClientBluetooth.send(frame);
        }
        else if (network == 2)
        {
            ClientUdp.asyncSend(frame);
        }
    }

    //synchronize checkBox with their settings
    private void synchronizeLogic()
    {
        isRepeat = cbRepeat.isChecked();
        isBounce = cbBounce.isChecked();
        isInvert = cbInvert.isChecked();
        isEndOff = cbEndOff.isChecked();
        repeatCounter = repeat;
        isPause = false;
        isRunning =false;

    }

    // stop
    private void stop()
    {
        // cancel timer
        playTimer.cancel();
        playTimer.purge();

        //send
        asyncSendWrapper(frameOff.frameByte);

        // logic
        synchronizeLogic();
    }

    // play
    private void play()
    {
        // if it's not running
        if (!isRunning)
        {
            // position
            if(!isPause)
            {
                //logic
                synchronizeLogic();
                //position
                if (isInvert)
                {
                    index = indexStop;
                }
                else
                {
                    index =indexStart;
                }
            }

            // logic
            isRunning = true;
            isPause = false;

            // launch the timer
            playTimer = new Timer();
            playTask = new PlayTask();
            playTimer.scheduleAtFixedRate(playTask,0 , delay);

        }
    }

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
            if (mAccel > 6) {
                if (!isRunning)
                {
                    //Toast.makeText(getApplicationContext(), "Play.", Toast.LENGTH_LONG).show();
                    play();
                }
                else
                {
                    //Toast.makeText(getApplicationContext(), "Pause.", Toast.LENGTH_LONG).show();
                    pause();
                }
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    //
    private void shake()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (mSensorManager != null)
        {
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        }
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

    }

    // pause
    private void pause()
    {
        // if it's running
        if (isRunning)
        {
            // logic
            isRunning =false;
            isPause = true;

            // cancel the timer
            playTimer.cancel();
            playTimer.purge();

            // send
            if(isEndOff)
            {
                asyncSendWrapper(frameOff.frameByte);
            }
        }
    }

    // launch SettingsScreen
    private void settingsScreen()
    {
        // put the information for the activity
        Intent data = new Intent(this, SettingsScreen.class);
        data.putExtra(SettingsScreen.EXTRA_PIXELS, pixels);
        data.putExtra(SettingsScreen.EXTRA_REPEAT, repeat);
        data.putExtra(SettingsScreen.EXTRA_BITMAPURI, bitmapUri);

        // Change the activity
        startActivityForResult(data,1);
    }

    // launch UdpScreen
    private void udpScreen()
    {
        if(ClientUdp.udpSocket==null)
        {
            // put the information for the activity
            Intent data = new Intent(this, UdpScreen.class);
            data.putExtra(SettingsScreen.EXTRA_PIXELS, pixels);
            data.putExtra(UdpScreen.EXTRA_UDPADDRESS, udpAddress);
            data.putExtra(UdpScreen.EXTRA_UDPPORT, udpPort);

            // Change the activity
            startActivityForResult(data,2);
        }
        else
        {
            // disconnection
            ClientUdp.disconnect();

            // change bluetooth icon
            btnUdp.setBackgroundResource(R.drawable.udpc);
        }
    }

    // launch BluetoothScreen
    private void bluetoothScreen()
    {
        if (ClientBluetooth.bluetoothSocket==null)
        {
            // put the information for the activity
            Intent data = new Intent(this, BluetoothScreen.class);
            data.putExtra(BluetoothScreen.EXTRA_ADDRESS, bluetoothAddress);

            // Change the activity
            startActivityForResult(data,3);
        }
        else
        {
            // disconnection
            ClientBluetooth.disconnect();

            // change bluetooth icon
            btnBluetooth.setBackgroundResource(R.drawable.bluetoothc);
        }
    }


    // grab back information from settings screen
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // from settings screen
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                // get data
                pixels = data.getIntExtra(SettingsScreen.EXTRA_PIXELS, 60);
                repeat = data.getIntExtra(SettingsScreen.EXTRA_REPEAT, 3);
                bitmapUri = data.getParcelableExtra(SettingsScreen.EXTRA_BITMAPURI);
                try
                {
                    bitmap = scaleBitmap(scaleUri(bitmapUri,pixels),pixels);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }


                // frame
                frameOn = new Frame( pixels,255, 255, 255);
                frameOff = new Frame(pixels,0,0,0);
                Image = new Image(bitmap);

                // logic
                isRunning = false;
                isPause = false;
                index = 0;
                indexStart = 0;
                indexStop = Image.FrameArray.length-1;

                // assign to widgets
                btnPixels.setText(String.valueOf(pixels));
                btnRepeat.setText(String.valueOf(repeat));
                imageViewImage.setImageURI(bitmapUri);
                //  imageViewImage.setImageBitmap(bitmap);
                seekCutFrom.setMax(indexStop);
                seekCutTo.setMax(indexStop);
                seekCutFrom.setProgress(indexStart);
                seekCutTo.setProgress(indexStop);
                btnCutFrom2.setText(String.valueOf(indexStart));
                btnCutTo2.setText(String.valueOf(indexStop));
            }
            else
            {
                msg("Settings not Changed.");
            }
        }
        // from udp screen
        else if (requestCode == 2)
        {
            if (resultCode == RESULT_OK)
            {
                // get data
                udpAddress = data.getStringExtra(UdpScreen.EXTRA_UDPADDRESS);
                udpPort = data.getIntExtra(UdpScreen.EXTRA_UDPPORT, 6789);

                // disconnection
                ClientUdp.disconnect();

                // change the Udp icon
                btnUdp.setBackgroundResource(R.drawable.udpc);

                //UDP Client
                ClientUdp = new UdpClient(udpAddress, udpPort);
                ClientUdp.connect();

                // change the Udp icon
                btnUdp.setBackgroundResource(R.drawable.udpd);
            }
            else
            {
                msg("UDP settings not Changed.");
            }
        }
        // from bluetooth screen
        else if (requestCode == 3)
        {
            if (resultCode == RESULT_OK)
            {
                //get data
                bluetoothAddress = data.getStringExtra(BluetoothScreen.EXTRA_ADDRESS);

                // disconnection
                ClientBluetooth.disconnect();

                // change bluetooth icon
                btnBluetooth.setBackgroundResource(R.drawable.bluetoothc);

                //Create the new bluetooth task
                ClientBluetooth = new BluetoothClient(bluetoothAddress);
                ClientBluetooth.connect();

                // change bluetooth icon
                btnBluetooth.setBackgroundResource(R.drawable.bluetoothd);
            }
            else
            {
                msg("Bluetooth settings not Changed.");
            }
        }
        // from nowhere !!!
        else
        {
            msg("Error.");
        }
    }

    // resize bitmap at given height keepin the aspect ration
    private Bitmap scaleBitmap(Bitmap bitmap, int requireHeight)
    {
        // get dimension
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // calculate new width to keep ratio
        int newWidth = (bitmapWidth * requireHeight)/bitmapHeight;

        // return the scale bitmap
        return Bitmap.createScaledBitmap(bitmap, newWidth, requireHeight, true);
    }

    // pre scale the image before fine resize (avoid high memory usage)
    private Bitmap scaleUri(Uri selectedImage, int requireHeight) throws FileNotFoundException
    {
        // Decode the actual image height
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);
        int height_tmp = o.outHeight;

        // Find the correct scale value. It should be the power of 2.
        int scale = 1;
        while (true) {
            if (height_tmp / 2 < requireHeight) {
                break;
            }
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }

    public class PlayTask  extends TimerTask
    {
        @Override
        public void run()
        {
            // if the index is in the limit
            if (indexStart <= index && index <= indexStop)
            {
                // send
                sendWrapper(Image.FrameArray[index].frameByte);
                //logic
                isRunning = true;
                // position
                if (isInvert)
                {
                    index -=1;
                }
                else
                {
                    index +=1;
                }
            }
            // the index is out of the limit
            else
            {
                // if repeat is check, go again
                if (isRepeat && repeatCounter > 1)
                {
                    // logic
                    isRunning = true;
                    repeatCounter-=1;

                    // position
                    if (isInvert)
                    {
                        index = indexStop;
                    }
                    else
                    {
                        index =indexStart;
                    }
                }
                // if bounce is check, go again
                else if ( isBounce && repeatCounter > 1)
                {
                    // logic
                    isRunning = true;
                    repeatCounter-=1;
                    isInvert = !isInvert; //invert the invert (following ??)

                    // position
                    if (isInvert)
                    {
                        index = indexStop;
                    }
                    else
                    {
                        index =indexStart;
                    }
                }
                // if repeat or bounce isn't check, stop
                else
                {
                    // logic
                    isRunning = false;

                    // send
                    if(isEndOff)
                    {
                        sendWrapper(frameOff.frameByte);
                    }

                    // cancel the timer
                    playTimer.cancel();
                    playTimer.purge();
                }
            }
        }
    }

}


