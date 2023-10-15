package com.jbreizh.ImagePainting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;


public class SettingsScreen extends AppCompatActivity {
    //widgets
    Button btnPixels2, btnRepeat2, btnFolder, btnReturn;
    ImageView imageViewImageSelect;
    SeekBar seekBarPixels, seekBarRepeat;

    //settings
    int pixels, repeat;
    public static String EXTRA_PIXELS = "settings_pixels";
    public static String EXTRA_REPEAT = "settings_repeat";
    Uri bitmapUri;
    public static String EXTRA_BITMAPURI = "settings_bitmapUri";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        // Settings
        Intent data = getIntent();
        pixels = data.getIntExtra(EXTRA_PIXELS, 60);
        repeat = data.getIntExtra(EXTRA_REPEAT, 3);
        bitmapUri = data.getParcelableExtra(EXTRA_BITMAPURI);

        //widgets
        btnPixels2 = findViewById(R.id.buttonPixels2);
        btnRepeat2 = findViewById(R.id.buttonRepeat2);
        btnFolder = findViewById(R.id.buttonFolder);
        btnReturn = findViewById(R.id.buttonReturn);
        seekBarPixels = findViewById(R.id.seekBarPixels);
        seekBarRepeat = findViewById(R.id.seekBarRepeat);
        imageViewImageSelect = findViewById(R.id.imageViewImageSelect);

        // assign to widgets
        seekBarPixels.setProgress(pixels);
        btnPixels2.setText(String.valueOf(pixels));
        seekBarRepeat.setProgress(repeat);
        btnRepeat2.setText(String.valueOf(repeat));
        imageViewImageSelect.setImageURI(bitmapUri);

        // buttons actions
        btnReturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                closeScreen();
            }
        });

        btnFolder.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                imagePicker();
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

    }

    // launch the imagePicker
    private void imagePicker()
    {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 0);
    }

    // grab information back from the imagePicker
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
    {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        // from image picker
        if (requestCode==0) {
            if (resultCode == RESULT_OK)
            {
                // print the selected image in imageViewSelect
                bitmapUri = imageReturnedIntent.getData();
                imageViewImageSelect.setImageURI(bitmapUri);
            }
            // from nowhere !!!!
            else
            {
                msg("No image pick.");;
            }
        }
    }

    // call the UdpLedScreen activity
    private void closeScreen()
    {
        // Get and resize the bitmap from the imageView
        //Bitmap bitmap = scaleBitmapKeepAspectRatio(((BitmapDrawable) imageViewImageSelect.getDrawable()).getBitmap(),pixels);

        // put the information for the activity
        Intent data = new Intent();
        data.putExtra(EXTRA_PIXELS, pixels);
        data.putExtra(EXTRA_REPEAT, repeat);
        data.putExtra(EXTRA_BITMAPURI,bitmapUri);

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