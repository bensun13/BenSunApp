package com.example.bsun.bensunapp;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ShowRandomPicture extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "com.example.bensunapp.MESSAGE";
    public final static String EXTRA_SOURCE = "com.example.bensunapp.SOURCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_random_picture);
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {

        Log.d("Android SDK Version:", android.os.Build.VERSION.SDK.toString());

        if (Integer.valueOf(android.os.Build.VERSION.SDK) >= 23 && ActivityCompat.checkSelfPermission(ShowRandomPicture.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("Requesting Permission", Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ActivityCompat.requestPermissions(ShowRandomPicture.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Intent intent = new Intent(this, DisplayRandomPicture.class);
            EditText editText = (EditText) findViewById(R.id.edit_message);
            String message = editText.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message);

            RadioGroup rg = (RadioGroup) findViewById(R.id.radio_source);

            Log.d("RadioGroup:", rg.toString());

            final String source = ((RadioButton)findViewById(rg.getCheckedRadioButtonId())).getText().toString();

            intent.putExtra(EXTRA_SOURCE, source);

            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Activity:", "ShowRandomPicture");
        Log.d("Request Code: ", Integer.toString(requestCode));
        Log.d("Requested Permission: ", permissions[0].toString());
        Log.d("Grant Results: ", grantResults.toString());
        if (grantResults[0] >= 0) {
            this.sendMessage(findViewById(R.id.send_message));

        }
    }

}
