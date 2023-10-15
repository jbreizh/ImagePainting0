package com.jbreizh.ImagePainting;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


public class BluetoothScreen extends AppCompatActivity
{
    //widgets
    Button btnReturn;
    ListView deviceList;

    //Bluetooth
    private BluetoothAdapter myBluetooth = null;

    //settings
    String address = null;
    public static String EXTRA_ADDRESS = "device_address";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_screen);

        // Settings
        Intent data = getIntent();
        address = data.getStringExtra(EXTRA_ADDRESS);

        //widgets
        btnReturn = findViewById(R.id.buttonReturn);
        deviceList = findViewById(R.id.listView);

        // buttons actions
        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        // test bluetooth
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        //populate device list with paired devices
        pairedDevicesList();

    }

    // populate device list with paired devices
    private void pairedDevicesList()
    {
        if(myBluetooth == null)
        {
            //Show a message. that the device has no bluetooth adapter and finish apk
            msg("Bluetooth Device Not Available.");
            finish();
        }
        else if(!myBluetooth.isEnabled())
        {
            //Ask to the user turn the bluetooth on
            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBTon,1);
        }
        else
        {
            Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
            ArrayList list = new ArrayList();

            if (pairedDevices.size() > 0) {
                for (BluetoothDevice bt : pairedDevices) {
                    list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
                }
            } else {
                msg("No Paired Bluetooth Devices Found.");
                finish();
            }

            final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            deviceList.setAdapter(adapter);
            deviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                pairedDevicesList();
            }
            else
            {
                msg("Bluetooth Device Not Enable.");
                finish();
            }
        }
        else
        {
            msg("Error.");
        }
    }

    // grab the adress off the paired device select by user
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            address = info.substring(info.length() - 17);

            // put the information for the activity
            Intent data = new Intent();
            data.putExtra(EXTRA_ADDRESS, address);

            // Change the activity
            setResult(RESULT_OK,data);
            finish();
        }
    };

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

}

