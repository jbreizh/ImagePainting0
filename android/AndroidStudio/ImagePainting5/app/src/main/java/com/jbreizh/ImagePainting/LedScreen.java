package com.jbreizh.ImagePainting;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
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

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    // network Bluetooth or UDP
    private int network;

    // Gui
    private Button btnLight, btnStop, btnPlay, btnPause, btnSettings, btnUdp, btnBluetooth, btnPixels, btnRepeat, btnBrightness, btnDelay, btnCutFrom2, btnCutTo2;
    private SeekBar seekCutFrom, seekCutTo;
    private ImageView imageViewImage;
    private CheckBox cbShake, cbRoll, cbRepeat, cbBounce, cbInvert, cbEndOff;

    // UDP
    private String udpAddress;
    private int udpPort;
    private UdpClient ClientUdp;

    // bluetooth
    private String bluetoothAddress;
    private BluetoothClient ClientBluetooth;

    // shake
    private SensorManager mSensorManager;
    private Sensor accelerometer, magnetometer;
    private ShakeEventListener shakeEventListener;
    private RollEventListener rollEventListener;

    //settings
    private int brightness, pixels, repeat, delay;
    private float sensibility;
    private Bitmap bitmap;
    private Uri bitmapUri;
    private String bitmapUriString;

    //frame
    private Frame frameOn, frameOff;
    private Image Image;

    // logic
    private boolean isRunning;
    private boolean isPause;
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
        btnBrightness =findViewById(R.id.buttonBrightness);
        btnDelay =findViewById(R.id.buttonDelay);

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

        // Settings
        sharedPreferences = getPreferences(MODE_PRIVATE);
        brightness=sharedPreferences.getInt("brigthness",40);
        delay=sharedPreferences.getInt("delay",50);
        pixels=sharedPreferences.getInt("pixels",60);
        repeat=sharedPreferences.getInt("repeat",3);
        sensibility=sharedPreferences.getFloat("sensibility",3.2f);
        udpAddress = sharedPreferences.getString("udpAddress","192.168.43.169");
        udpPort=sharedPreferences.getInt("udpPort",6789);
        bluetoothAddress = sharedPreferences.getString("bluetoothAddress",null);
        bitmapUriString = sharedPreferences.getString("bitmapUriString","android.resource://"+R.class.getPackage().getName()+"/" +R.drawable.pacman);

        // assign settings to widgets
        initializeSettings();

        // assign bitmap to widgets
        bitmapUri = Uri.parse(bitmapUriString);
        initializeBitmap();

        // UDP and Bluetooth client initialisation
        ClientUdp = new UdpClient(udpAddress, udpPort);
        ClientBluetooth = new BluetoothClient(bluetoothAddress);

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

        // Timer
        playTimer = new Timer();
        playTask = new PlayTask();

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

        // actions imageview
        imageViewImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                folderScreen();
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

    //
    public void initializeBitmap()
    {
        //create bitmap from uri
        try
        {
            bitmap = scaleBitmap(preScaleBitmapFromUri(bitmapUri,pixels),pixels);
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

        //imageViewImage.setImageURI(bitmapUri);
        imageViewImage.setImageBitmap(bitmap);
        seekCutFrom.setMax(indexStop);
        seekCutTo.setMax(indexStop);
        seekCutFrom.setProgress(indexStart);
        seekCutTo.setProgress(indexStop);
        btnCutFrom2.setText(String.valueOf(indexStart));
        btnCutTo2.setText(String.valueOf(indexStop));
    }

    // resize bitmap at given height keepin the aspect ration
    private Bitmap scaleBitmap(Bitmap bitmap, int requireHeight)
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
    private Bitmap preScaleBitmapFromUri(Uri selectedImage, int requireHeight) throws FileNotFoundException
    {
        Log.d(TAG, "preScaleBitmapFromUri");
        // Decode the actual image height
        BitmapFactory.Options inputOption = new BitmapFactory.Options();
        inputOption.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, inputOption);
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
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, outputOption);
    }

    // assign settings to widgets
    public void initializeSettings()
    {
        // assign to widgets
        btnPixels.setText(String.valueOf(pixels));
        btnRepeat.setText(String.valueOf(repeat));
        btnBrightness.setText(String.valueOf(brightness));
        btnDelay.setText(String.valueOf(delay));
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
                asyncSendWrapper(frameOff.frameByte);
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
            msg("UDP Disconnected.");
        }
    }

    // launch BluetoothScreen
    private void bluetoothScreen()
    {
        // if we are not connected
        if (ClientBluetooth.bluetoothSocket==null)
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

            // change bluetooth icon
            btnBluetooth.setBackgroundResource(R.drawable.bluetoothc);
            msg("Bluetooth Disconnected.");
        }
    }

    // grab back information from image picker, settings screen, bluetooth screen and udp screen
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode==0) {
            if (resultCode == RESULT_OK)
            {
                // print the selected image in imageViewSelect
                bitmapUri = data.getData();

                // assign to sharedPreferences
                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString("bitmapUriString",bitmapUri.toString());
                sharedPreferencesEditor.apply();

                //initialize
                initializeBitmap();
            }
            else
            {
                msg("Image not Changed.");
            }
        }
        // from settings screen
        else if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                // get data
                pixels = data.getIntExtra(SettingsScreen.EXTRA_PIXELS, pixels);
                repeat = data.getIntExtra(SettingsScreen.EXTRA_REPEAT, repeat);
                brightness = data.getIntExtra(SettingsScreen.EXTRA_BRIGHTNESS, brightness);
                delay = data.getIntExtra(SettingsScreen.EXTRA_DELAY, delay);
                sensibility = data.getFloatExtra(SettingsScreen.EXTRA_SENSIBILITY, sensibility);

                // assign to sharedPreferences
                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putInt("brigthness",brightness);
                sharedPreferencesEditor.putInt("delay",delay);
                sharedPreferencesEditor.putInt("pixels",pixels);
                sharedPreferencesEditor.putInt("repeat",repeat);
                sharedPreferencesEditor.putFloat("sensibility",sensibility);
                sharedPreferencesEditor.apply();

                // assign to widgets
                initializeSettings();
                initializeBitmap();

                // assign to the shakeEvent
                shakeEventListener.setSHAKE_GRAVITY( sensibility);

                // sending settings to the device
                asyncSendWrapper((new Frame(pixels,brightness)).frameByte);
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
                udpPort = data.getIntExtra(UdpScreen.EXTRA_UDPPORT, udpPort);

                // assign to sharedPreferences
                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString("udpAddress",udpAddress);
                sharedPreferencesEditor.putInt("udpPort",udpPort);
                sharedPreferencesEditor.apply();

                // disconnection
                ClientUdp.disconnect();

                // change the Udp icon
                btnUdp.setBackgroundResource(R.drawable.udpc);

                //UDP Client
                ClientUdp = new UdpClient(udpAddress, udpPort);
                ClientUdp.connect();

                // change the Udp icon
                btnUdp.setBackgroundResource(R.drawable.udpd);
                msg("UDP Connected.");
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

                // assign to sharedPreferences
                sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.putString("bluetoothAddress",bluetoothAddress);
                sharedPreferencesEditor.apply();

                // disconnection
                ClientBluetooth.disconnect();

                // change bluetooth icon
                btnBluetooth.setBackgroundResource(R.drawable.bluetoothc);

                //Create the new bluetooth task
                ClientBluetooth = new BluetoothClient(bluetoothAddress);
                ClientBluetooth.connect();

                // change bluetooth icon
                btnBluetooth.setBackgroundResource(R.drawable.bluetoothd);
                msg("Bluetooth Connected.");
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


