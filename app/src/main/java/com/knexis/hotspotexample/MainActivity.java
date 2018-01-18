package com.knexis.hotspotexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.knexis.hotspot.Hotspot;

public class MainActivity extends AppCompatActivity {

    Hotspot hotspot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hotspot = new Hotspot(this);
    }

    public void OnStartHotspotClicked(View view) {
        hotspot.start("Hotspot-Android", "12345678");
    }


    public void OnStopHotspotClicked(View view) {
        hotspot.stop();
    }
}
