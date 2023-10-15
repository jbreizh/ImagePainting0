package com.jbreizh.ImagePainting;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Pattern;

public class UdpScreen extends AppCompatActivity {
    // pattern
    private static final Pattern PARTIAl_IP_ADDRESS =
            Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
                    "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$");

    //widgets
    Button btnReturn;
    EditText editTextAddress, editTextPort;

    //settings
    int pixels;
    String udpAddress;
    int udpPort;
    public static String EXTRA_UDPADDRESS = "udp_address";
    public static String EXTRA_UDPPORT = "udp_port";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udp_screen);

        // Settings
        Intent data = getIntent();
        pixels = data.getIntExtra(SettingsScreen.EXTRA_PIXELS, 60);
        udpAddress = data.getStringExtra(EXTRA_UDPADDRESS);
        udpPort = data.getIntExtra(EXTRA_UDPPORT, 6789);

        //widgets
        btnReturn = findViewById(R.id.buttonReturn);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPort = findViewById(R.id.editTextPort);

        // assign to widgets
        editTextAddress.setText(udpAddress);
        editTextPort.setText(String.valueOf(udpPort));

        // buttons actions
        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeScreen();
            }
        });

        // editText actions
        editTextAddress.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }
            @Override
            public void beforeTextChanged(CharSequence s,int start,int count,int after)
            {

            }

            private String mPreviousText = "";
            @Override
            public void afterTextChanged(Editable s)
            {
                // assist user to give enter a valid IP
                if(PARTIAl_IP_ADDRESS.matcher(s).matches())
                {
                    mPreviousText = s.toString();
                }
                else
                {
                    s.replace(0, s.length(), mPreviousText);
                }
            }
        });

        editTextAddress.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                // When focus is lost check that the text field has valid values.
                if (!hasFocus)
                {
                    checkAddress();
                }
            }
        });

        editTextPort.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                // When focus is lost check that the text field has valid values.
                if (!hasFocus)
                {
                    checkPort();
                }
            }
        });

        // test and activate the wifi
        activateWifi();
    }


    // test and activate the wifi is possible/necessary
    private void activateWifi()
    {
        // get the wifi adaptor
        WifiManager myWifi =(WifiManager)getApplicationContext().getSystemService(UdpScreen.WIFI_SERVICE);

        // test if the device has wifi
        if(myWifi == null)
        {
            //Show a message. that the device has no wifi adapter and finish UDPscreen
            msg("Wifi Device Not Available.");
            finish();
        }

        //test if the wifi adaptor is enable
        else if(!myWifi.isWifiEnabled())
        {
            // Turn the wifi on
            myWifi.setWifiEnabled(true);
            msg("Image Painting turn the wifi on.");
        }
    }


    // check the ip address enter when the address edit lost focus
    private void checkAddress()
    {
        // get the propose IP from the textEdit
        String proposeIp = editTextAddress.getText().toString();

        // propose ip is valid, we save it
        if (Patterns.IP_ADDRESS.matcher(proposeIp).matches())
        {
            udpAddress = proposeIp;
        }
        // propose ip is invalid, we roll back
        else
        {
            editTextAddress.setText(udpAddress);
            msg("IP : "+proposeIp+" is invalid.");
        }
    }

    // check the ip address enter when the port edit lost focus
    private void checkPort()
    {
        // get the propose port from the textEdit
        String proposePort = editTextPort.getText().toString();

        // propose port is valid
        if(!TextUtils.isEmpty(proposePort))
        {
            udpPort = Integer.parseInt(proposePort);

        }
        // propose port is invalid, we roll back
        else
        {
            // ip is incorrect we roll back
            editTextPort.setText(String.valueOf(udpPort));
            msg("Port : "+proposePort+" is invalid.");
        }
    }

    // call the LedScreen activity
    private void closeScreen()
    {
        // check and store IP and port
        checkAddress();
        checkPort();
        // put the information for the activity
        Intent data = new Intent();
        data.putExtra(EXTRA_UDPADDRESS, udpAddress);
        data.putExtra(EXTRA_UDPPORT, udpPort);
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
