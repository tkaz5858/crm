package com.bands.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean recordPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storagePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (recordPermission && storagePermission) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private static final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, MyBackgroundService.class);
        startService(intent);

        if (!checkPermission()) {
            requestPermission();
        }

        // İzinleri kontrol edin
        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSIONS);
            } else {
                startCallRecordingService();
            }
        } else {
            startCallRecordingService();
        }
    }



    private void startCallRecordingService() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        CallReceiver receiver = new CallReceiver();
        registerReceiver(receiver, filter);
    }
}
