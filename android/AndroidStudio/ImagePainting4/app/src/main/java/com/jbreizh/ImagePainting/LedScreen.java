package com.jbreizh.ImagePainting;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
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

    String TAG = "LedScreen";

    // network Bluetooth or UDP
    private int network;

    // Gui
    private Button btnLight, btnStop, btnPlay, btnPause, btnSettings, btnFolder, btnUdp, btnBluetooth, btnCutFrom2, btnCutTo2;
    private SeekBar seekCutFrom, seekCutTo;
    private ImageView imageViewImage;
    private CheckBox cbShake, cbRoll, cbRepeat, cbBounce, cbInvert, cbEndOff;

    // UDP
    private String udpAddress = "192.168.4.1";
    private int udpPort = 5000;
    private UdpClient ClientUdp =new UdpClient();

    // bluetooth
    private String bluetoothAddress = null;
    private BluetoothClient ClientBluetooth = new BluetoothClient();

    // shake
    private SensorManager mSensorManager;
    private Sensor accelerometer, magnetometer;
    private ShakeEventListener shakeEventListener;
    private RollEventListener rollEventListener;

    //settings
    private int brightness = 40;
    private int pixels = 60;
    private int repeat = 3;
    private int delay = 30;
    private float sensibility = 3.2f;
    private Bitmap bitmap;
    private Uri bitmapUri;

    //frame
    byte[] framePattern = new byte[] {'F', 'R', 'M'};
    private Frame frameOn = new Frame();
    private Frame frameOff = new Frame();
    private Image Image = new Image();

    // logic
    private boolean isRunning = false;
    private boolean isPause = false;
    private boolean isRepeat, isBounce, isInvert, isEndOff;
    private int indexStart, index, indexStop;
    private int repeatCounter;

    // timer
    private Timer playTimer = new Timer();
    private  PlayTask playTask = new PlayTask();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_screen);

        // widgets
        btnLight = findViewById(R.id.buttonLight);
        btnStop = findViewById(R.id.buttonStop);
        btnPlay = findViewById(R.id.buttonPlay);
        btnPause = findViewById(R.id.buttonPause);
        btnSettings = findViewById(R.id.buttonSettings);
        btnFolder = findViewById(R.id.buttonFolder);
        btnUdp = findViewById(R.id.buttonUdp);
        btnBluetooth = findViewById(R.id.buttonBluetooth);

        imageViewImage = findViewById(R.id.imageViewImage);
        seekCutFrom = findViewById(R.id.seekBarCutFrom);
        btnCutFrom2 = findViewById(R.id.buttonCutFrom2);
        seekCutTo = findViewById(R.id.seekBarCutTo);
        btnCutTo2 = findViewById(R.id.buttonCutTo2);

        cbShake =  findViewById(R.id.checkBoxShake);
        cbRoll =  findViewById(R.id.checkBoxRoll);
        cbRepeat = findViewById(R.id.checkBoxRepeat);
        cbBounce = findViewById(R.id.checkBoxBounce);
        cbInvert = findViewById(R.id.checkBoxInvert);
        cbEndOff = findViewById(R.id.checkBoxEndOff);

        // get the network from the splash screen
        Intent data = getIntent();
        network = data.getIntExtra(SplashScreen.EXTRA_NETWORK, 1);

        // assign network to widget
        if (network == 1)
        {
            btnUdp.setVisibility(View.GONE);
        }
        else if (network == 2)
        {
            btnBluetooth.setVisibility(View.GONE);
        }

        // bitmap
        bitmapUri = Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" +R.drawable.pacman);
        bitmap =  BitmapFactory.decodeStream(this.getResources().openRawResource(+R.drawable.pacman));

        // frame
        frameOn.setFrameByteFromRGB(pixels, new byte[] {(byte) 255, (byte) 255, (byte) 255});
        frameOff.setFrameByteFromRGB(pixels, new byte[] {(byte) 0, (byte) 0, (byte) 0});
        Image.setFrameArrayFromBitmap(bitmap);

        // logic
        index = 0;
        indexStart = 0;
        indexStop = Image.getFrameArray().length - 1;

        // assign bitmap to widgets
        imageViewImage.setImageBitmap(bitmap);
        seekCutFrom.setMax(indexStop);
        seekCutTo.setMax(indexStop);
        seekCutFrom.setProgress(indexStart);
        seekCutTo.setProgress(indexStop);
        btnCutFrom2.setText(String.valueOf(indexStart));
        btnCutTo2.setText(String.valueOf(indexStop));

        // sensors
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(mSensorManager!=null)
        {
            accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        shakeEventListener = new ShakeEventListener();
        shakeEventListener.setSHAKE_GRAVITY(sensibility);
        rollEventListener = new RollEventListener();

        // assign sensors to widget
        cbShake.setVisibility(View.GONE);
        cbRoll.setVisibility(View.GONE);
        if(mSensorManager != null && accelerometer != null)
        {
            // if the accelerometer exist, we can shake
            cbShake.setVisibility(View.VISIBLE);

            if(magnetometer != null)
            {
                // if the accelerometer and the magnetometer exist, we can roll
                cbRoll.setVisibility(View.VISIBLE);
            }
        }

        // actions button
        btnLight.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                asyncSendWrapper(frameOn.getWholeFrame(framePattern));
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

        btnFolder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                folderScreen();
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
        cbShake.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(cbShake.isChecked())
                {
                    // if shake, no roll possible
                    cbRoll.setEnabled(false);

                    // start the sensor listener
                    if (mSensorManager != null) {
                        mSensorManager.registerListener(shakeEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }
                else
                {
                    // if not shake, roll possible
                    cbRoll.setEnabled(true);

                    // stop the sensor listener
                    if (mSensorManager != null)
                    {
                        mSensorManager.unregisterListener(shakeEventListener);
                    }
                }
            }
        });

        cbRoll.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(cbRoll.isChecked())
                {
                    // if roll, no shake possible
                    cbShake.setEnabled(false);

                    // start the sensor listener
                    if (mSensorManager != null) {
                        mSensorManager.registerListener(rollEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                        mSensorManager.registerListener(rollEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }
                else
                {
                    // if not roll, shake possible
                    cbShake.setEnabled(true);

                    // stop the sensor listener
                    if (mSensorManager != null)
                    {
                        mSensorManager.unregisterListener(rollEventListener);
                    }
                }
            }
        });

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
                        indexStart = indexStop;
                        seekCutFrom.setProgress(indexStart);
                        btnCutFrom2.setText(String.valueOf(indexStart));
                    }
                }
                // draw curtains
                imageViewImage.setImageBitmap(BitmapTraitement.curtains(bitmap,indexStart,indexStop));
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
                        indexStop = indexStart;
                        seekCutTo.setProgress(indexStop);
                        btnCutTo2.setText(String.valueOf(indexStop));
                    }
                }
                // draw curtains
                imageViewImage.setImageBitmap(BitmapTraitement.curtains(bitmap,indexStart,indexStop));
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

        // shake and roll action
        shakeEventListener.setOnShakeListener(new ShakeEventListener.OnShakeListener()
        {
            @Override
            public void onShake()
            {
                if (!isRunning)
                {
                    play();
                }
                else
                {
                    pause();
                }
            }
        });

        rollEventListener.setOnRollListener(new RollEventListener.OnRollListener() {
            @Override
            public void onRollFlat()
            {

            }

            @Override
            public void onRollTurnOn()
            {
                play();
            }

            @Override
            public void onRollTurnOff()
            {
                pause();
            }
        });

        ClientUdp.setOnUdpClientListener(new UdpClient.OnUdpClientListener()
        {
            @Override
            public void connect(boolean success)
            {
                if (success)
                {
                    // change bluetooth icon
                    btnUdp.setBackgroundResource(R.drawable.udpc);
                    msg("UDP Connection success.");
                }
                else
                {
                    // change bluetooth icon
                    btnUdp.setBackgroundResource(R.drawable.udpd);
                    msg("UDP Connection fail.");
                }
            }
            @Override
            public void disconnect(boolean success)
            {
                if (success)
                {
                    // change bluetooth icon
                    btnUdp.setBackgroundResource(R.drawable.udpd);
                    msg("UDP disconnect success.");
                }
                else
                {
                    msg("UDP disconnect fail.");
                }
            }
        });

        ClientBluetooth.setOnBluetoothClientListener(new BluetoothClient.OnBluetoothClientListener()
        {
            @Override
            public void connect(boolean success)
            {
                if (success)
                {
                    // change bluetooth icon
                    btnBluetooth.setBackgroundResource(R.drawable.bluetoothc);
                    msg("Bluetooth Connection success.");
                }
                else
                {
                    // change bluetooth icon
                    btnBluetooth.setBackgroundResource(R.drawable.bluetoothd);
                    msg("Bluetooth Connection fail.");
                }
            }
            @Override
            public void disconnect(boolean success)
            {
                if (success)
                {
                    // change bluetooth icon
                    btnBluetooth.setBackgroundResource(R.drawable.bluetoothd);
                    msg("Bluetooth disconnect success.");
                }
                else
                {
                    msg("Bluetooth disconnect fail.");
                }
            }
        });
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

    // stop
    private void stop()
    {
        // cancel timer
        playTimer.cancel();
        playTimer.purge();

        //send
        asyncSendWrapper(frameOff.getWholeFrame(framePattern));

        // logic
        isPause = false;
        isRunning =false;
    }

    // play
    protected void play()
    {
        // if it's not running
        if (!isRunning)
        {
            // position
            if(!isPause)
            {
                repeatCounter = repeat;

                //logic
                isRepeat = cbRepeat.isChecked();
                isBounce = cbBounce.isChecked();
                isInvert = cbInvert.isChecked();
                isEndOff = cbEndOff.isChecked();

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

    // pause
    protected void pause()
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
                asyncSendWrapper(frameOff.getWholeFrame(framePattern));
            }
        }
    }

    // launch the imagePicker
    private void folderScreen()
    {
        // put the information for the activity
        Intent data = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Change the activity
        startActivityForResult(data , 0);
    }

    // launch SettingsScreen
    private void settingsScreen()
    {
        // put the information for the activity
        Intent data = new Intent(this, SettingsScreen.class);
        data.putExtra(SettingsScreen.EXTRA_PIXELS, pixels);
        data.putExtra(SettingsScreen.EXTRA_REPEAT, repeat);
        data.putExtra(SettingsScreen.EXTRA_BRIGHTNESS, brightness);
        data.putExtra(SettingsScreen.EXTRA_DELAY, delay);
        data.putExtra(SettingsScreen.EXTRA_SENSIBILITY, sensibility);

        // Change the activity
        startActivityForResult(data,1);
    }

    // launch UdpScreen
    private void udpScreen()
    {
        if(ClientUdp.getUdpSocket()==null)
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
        }
    }

    // launch BluetoothScreen
    private void bluetoothScreen()
    {
        // if we are not connected
        if (ClientBluetooth.getBluetoothSocket()==null)
        {
            // put the information for the activity
            Intent data = new Intent(this, BluetoothScreen.class);
            data.putExtra(BluetoothScreen.EXTRA_ADDRESS, bluetoothAddress);

            // Change the activity
            startActivityForResult(data,3);
        }
        // if we are connected
        else
        {
            // disconnection
            ClientBluetooth.disconnect();
        }
    }

    // grab back information from image picker, settings screen, bluetooth screen and udp screen
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // from folder screen
        if (requestCode==0) {
            if (resultCode == RESULT_OK)
            {
                //  get data
                bitmapUri = data.getData();

                //create bitmap from uri
                try
                {
                    bitmap = BitmapTraitement.scaleBitmap(BitmapTraitement.preScaleBitmapFromUri(this,bitmapUri,pixels),pixels);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }

                // frame
                frameOn.setFrameByteFromRGB(pixels, new byte[] {(byte) 255, (byte) 255, (byte) 255});
                frameOff.setFrameByteFromRGB(pixels, new byte[] {(byte) 0, (byte) 0, (byte) 0});
                Image.setFrameArrayFromBitmap(bitmap);

                // logic
                isRunning = false;
                isPause = false;

                // position
                index = 0;
                indexStart = 0;
                indexStop = Image.getFrameArray().length - 1;

                //assign to widgets
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
                msg("No image pick.");
            }
        }
        // from settings screen
        else if (requestCode == 1)
        {
            if (resultCode == RESULT_OK) {

                // get data
                repeat = data.getIntExtra(SettingsScreen.EXTRA_REPEAT, 3);
                brightness = data.getIntExtra(SettingsScreen.EXTRA_BRIGHTNESS, 40);
                delay = data.getIntExtra(SettingsScreen.EXTRA_DELAY, 40);
                sensibility = data.getFloatExtra(SettingsScreen.EXTRA_SENSIBILITY, 3.2f);

                // updating the bitmap is painfull, don't do it if pixel hasn't change
                if (pixels != data.getIntExtra(SettingsScreen.EXTRA_PIXELS, 60))
                {
                    // get data
                    pixels = data.getIntExtra(SettingsScreen.EXTRA_PIXELS, 60);

                    //create bitmap from uri
                    try {
                        bitmap = BitmapTraitement.scaleBitmap(BitmapTraitement.preScaleBitmapFromUri(this, bitmapUri, pixels), pixels);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    // frame
                    frameOn.setFrameByteFromRGB(pixels, new byte[]{(byte) 255, (byte) 255, (byte) 255});
                    frameOff.setFrameByteFromRGB(pixels, new byte[]{(byte) 0, (byte) 0, (byte) 0});
                    Image.setFrameArrayFromBitmap(bitmap);

                    // logic
                    isRunning = false;
                    isPause = false;

                    //position
                    index = 0;
                    indexStart = 0;
                    indexStop = Image.getFrameArray().length - 1;

                    //assign to widgets
                    imageViewImage.setImageBitmap(bitmap);
                    seekCutFrom.setMax(indexStop);
                    seekCutTo.setMax(indexStop);
                    seekCutFrom.setProgress(indexStart);
                    seekCutTo.setProgress(indexStop);
                    btnCutFrom2.setText(String.valueOf(indexStart));
                    btnCutTo2.setText(String.valueOf(indexStop));
                }

                // assign to the shakeEvent
                shakeEventListener.setSHAKE_GRAVITY( sensibility);

                // sending settings to the device
                asyncSendWrapper(new byte[] {'S' , 'E',  'T', (byte) brightness, (byte) pixels, (byte) delay });
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

                //UDP Client
                ClientUdp.setAddress(udpAddress);
                ClientUdp.setPort(udpPort);
                ClientUdp.connect();
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

                //Create the new bluetooth task
                ClientBluetooth.setAddress(bluetoothAddress);
                ClientBluetooth.connect();
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

    // TimerTask
    public class PlayTask  extends TimerTask
    {

        @Override
        public void run()
        {
            // if the index is in the limit
            if (indexStart <= index && index <= indexStop)
            {
                // send
                sendWrapper(Image.getFrameFromIndex(index).getWholeFrame(framePattern));
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
                        sendWrapper(frameOff.getWholeFrame(framePattern));
                    }

                    // cancel the timer
                    playTimer.cancel();
                    playTimer.purge();
                }
            }
        }
    }

}


