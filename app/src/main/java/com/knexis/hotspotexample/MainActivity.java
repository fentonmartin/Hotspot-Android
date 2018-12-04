package com.knexis.hotspotexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.knexis.hotspot.ConnectedDevice;
import com.knexis.hotspot.ConnectionResult;
import com.knexis.hotspot.Hotspot;
import com.knexis.hotspot.HotspotListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements HotspotListener{

    private Hotspot hotspot;
    private TextView tvDevicesConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDevicesConnected = findViewById(R.id.tv_devices_connected);

        setPermission();

        hotspot = new Hotspot(this);
        hotspot.startListener(this);

    }

    private void setPermission() {
        youDesirePermissionCode(this);
    }

    static int CODE_WRITE_SETTINGS_PERMISSION = 101;

    public static void youDesirePermissionCode(Activity context){
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(context);
        } else {
            permission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
        }  else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivityForResult(intent, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_SETTINGS}, MainActivity.CODE_WRITE_SETTINGS_PERMISSION);
            }
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(this)){
            Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
            //do your code
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MainActivity.CODE_WRITE_SETTINGS_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //do your code
        }
    }

    public void OnStartHotspotClicked(View view) {
        hotspot.start("Hotspot-Android", "12345678");
    }

    public void onRefreshDevicesConnected(View view){
        hotspot.getClientList(true, 5000);
    }

    public void OnStopHotspotClicked(View view) {
        hotspot.stop();
    }

    @Override
    public void OnDevicesConnectedRetrieved(ArrayList<ConnectedDevice> clients) {

        if(clients != null)
            tvDevicesConnected.setText(String.valueOf(clients.size()));

    }

    @Override
    public void OnHotspotStartResult(ConnectionResult result) {

    }
}
