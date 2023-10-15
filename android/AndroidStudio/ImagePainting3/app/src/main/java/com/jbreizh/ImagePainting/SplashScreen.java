package com.jbreizh.ImagePainting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    // version
    public static String EXTRA_NETWORK = "splash_network";

    // Gui
    Button btnBluetoothMod, btnUdpMod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        //request "dangerous" permissions for reading external image
        if (ContextCompat.checkSelfPermission(this,  Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        // widgets
        btnBluetoothMod = findViewById(R.id.buttonBluetoothMod);
        btnUdpMod = findViewById(R.id.buttonUdpMod);

        // actions button
        btnBluetoothMod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent data = new Intent(SplashScreen.this, LedScreen.class);
                data.putExtra(EXTRA_NETWORK, 1);
                startActivity(data);
                finish();
            }
        });

        // actions button
        btnUdpMod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent data = new Intent(SplashScreen.this, LedScreen.class);
                data.putExtra(EXTRA_NETWORK, 2);
                startActivity(data);
                finish();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            // if from READ_EXTERNAL_STORAGE permission resquest
            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // thanks buddy
                    msg("Permission granted, let's go !!");
                }
                else
                {
                    // f**k man, you're screw
                    msg("Permission denied, try again !!");
                    finish();
                }
                return;
            }

        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}
