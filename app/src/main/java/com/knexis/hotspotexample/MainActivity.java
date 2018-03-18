package com.knexis.hotspotexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        hotspot = new Hotspot(this);

        hotspot.startListener(this);

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
