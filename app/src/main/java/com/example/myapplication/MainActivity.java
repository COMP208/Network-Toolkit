package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UI_control.setCustomActionBar(MainActivity.this, R.layout.actionbar_mainmenu);

        if(!checkWifiOnAndConnected()){
            Log.e("wifi","not connected");
        }

        //click listeners for "activity_main" layout

        //jump to "GeolocationActivity"
        ImageButton geolocation_tool = findViewById(R.id.geolocation_tool);
        geolocation_tool.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GeolocationActivity.class);
                startActivity(intent);
            }
        });

        //jump to "PingActivity"
        ImageButton ping_tool = findViewById(R.id.ping_tool);
        ping_tool.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PingActivity.class);
                intent.putExtra("result", 7);
                startActivity(intent);
            }
        });

        //jump to "TraceRoute"
        ImageButton traceroute_tool = findViewById(R.id.traceroute_tool);
        traceroute_tool.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TraceRouteActivity.class);
                startActivity(intent);
            }
        });

        //jump to "LAN Manager"
        ImageButton lanManager_tool = findViewById(R.id.lan_manager_tool);
        lanManager_tool.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, lan_managerActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     *
     * @return
     */
    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr =  (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if( wifiInfo.getNetworkId() == -1 ){
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        }
        else {
            return false; // Wi-Fi adapter is OFF
        }
    }


}
