package com.knexis.hotspot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by Nana Kwame Nyantakyi on 11/01/2018.
 * Purpose:
 *
 * Copyright 2018 Nana Kwame Nyantakyi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Hotspot {

    private final WifiManager mWifiManager;
    private Context context;

    public Hotspot(Context context) {
        this.context = context;
        mWifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
    }

    public void start(){
      start(null);
    }

    public void start(String name, String passPhrase){
        WifiConfiguration configuration = initWifiConfig(name, passPhrase);
        start(configuration);
    }

    public void start(WifiConfiguration configuration){
        enabled(true, configuration);
    }

    public void stop(){
        enabled(false, null);
    }

    private void enabled(boolean enabled, WifiConfiguration configuration) {
        try {
            if (enabled) { // disable WiFi in any case
                mWifiManager.setWifiEnabled(false);
            }

            if(!this.isON())
            {
                try{
                    Method method = mWifiManager.getClass().getMethod("setWifiApEnabled",WifiConfiguration.class,boolean.class);
                    method.invoke(mWifiManager,configuration, enabled);
                    mWifiManager.saveConfiguration();
                }
                catch (Exception e)
                {
                    e.getMessage();
                }
            }

        } catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
    }

    public boolean isON(){
        try{
            @SuppressLint("PrivateApi")
            Method method = mWifiManager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);
        }
        catch(Throwable ignoreException)
        {
            return false;
        }
    }

    private static WifiConfiguration initWifiConfig(String name, String passPhrase){

        WifiConfiguration wifiConfig = new WifiConfiguration();

        wifiConfig.SSID = name;
        // must be 8 or more in length
        wifiConfig.preSharedKey = passPhrase;

        wifiConfig.hiddenSSID = false;

        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        return wifiConfig;
    }

    /**
     * Gets a list of the clients connected to the Hotspot, reachable timeout is 300
     *
     * @param onlyReachables  {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @param listener, Interface called when the scan method finishes
     */
    public void getClientList(boolean onlyReachables, OnConnectedDevicesRetrievedListener listener) {
        getClientList(onlyReachables, 300, listener);
    }

    /**
     * Gets a list of the clients connected to the Hotspot
     *
     * @param onlyReachables   {@code false} if the list should contain unreachable (probably disconnected) clients, {@code true} otherwise
     * @param reachableTimeout Reachable Timout in miliseconds
     * @param finishListener,  Interface called when the scan method finishes
     */
    private void getClientList(final boolean onlyReachables, final int reachableTimeout, final OnConnectedDevicesRetrievedListener finishListener) {
        Runnable runnable = new Runnable() {
            public void run() {

                BufferedReader br = null;
                final ArrayList<ConnectedDevice> result = new ArrayList<ConnectedDevice>();

                try {
                    br = new BufferedReader(new FileReader("/proc/net/arp"));
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] splitted = line.split(" +");

                        if ((splitted != null) && (splitted.length >= 4)) {
                            // Basic sanity check
                            String mac = splitted[3];

                            if (mac.matches("..:..:..:..:..:..")) {
                                boolean isReachable = InetAddress.getByName(splitted[0]).isReachable(reachableTimeout);

                                if (!onlyReachables || isReachable) {
                                    result.add(new ConnectedDevice(splitted[0], splitted[3], splitted[5], isReachable));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e(this.getClass().toString(), e.toString());
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(this.getClass().toString(), e.getMessage());
                    }
                }

                // Get a handler that can be used to post to the main thread
                Handler mainHandler = new Handler(context.getMainLooper());
                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        finishListener.OnDevicesConnectedRetrieved(result);
                    }
                };
                mainHandler.post(myRunnable);
            }
        };

        Thread mythread = new Thread(runnable);
        mythread.start();
    }

}
