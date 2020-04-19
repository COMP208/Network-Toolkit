package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * last update: 2020/4/12
 * by: Minhao.Jin
 *
 * last update: 2020/4/12
 * by: Weihao.Jin
 */
public class lan_managerActivity extends AppCompatActivity {
    private byte[] myIpBytes;
    private String myIP;
    private String myMAC;
    private int lineNumber=0;
    public static ArrayList<DeviceInLAN> devices = new ArrayList();
    public static ArrayList<DeviceInLAN> registered_Devices;
    private boolean hasDialog = false;


    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registered_Devices = IOUtil.Read_File();
        setContentView(R.layout.activity_lan__manager);
        UI_control.setCustomActionBar(lan_managerActivity.this, R.layout.actionbar_lan_manager);

        final ImageButton goMenu = findViewById(R.id.back_to_menu_from_lanmanager);
        final LinearLayout loadingPing = findViewById(R.id.loading_for_lan_manager);
        final ImageButton show_graph = findViewById(R.id.show_topology_graph);
        final ImageButton refresh = findViewById(R.id.refresh);
        final LinearLayout devices_output = findViewById(R.id.lan_manager_output_list);
        final TextView device_num = findViewById(R.id.device_number);

        loadingPing.setVisibility(View.INVISIBLE);
        show_graph.setVisibility(View.INVISIBLE);

        goMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!checkWifiOnAndConnected()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            noInternetConnectionDialog();
                        }
                    });
                    return;
                }
                device_num.setText("   " + 0 + "  Devices found");
                devices_output.removeAllViews();
                show_graph.setVisibility(View.INVISIBLE);
                refresh.setVisibility(View.INVISIBLE);
                loadingPing.setVisibility(View.VISIBLE);
                registered_Devices = IOUtil.Read_File();

                //clear the container
                setLocalAddress();
                addLocalDevice();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        for (int i = 1; i <= 10; i++) {
                            try {
                                scanSubnet();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            Thread.sleep(12000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        readArp();

                        try {
                            Thread.sleep(300*lineNumber);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //your thread here, or in try catch part
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingPing.setVisibility(View.INVISIBLE);
                                refresh.setVisibility(View.VISIBLE);
                                if (lineNumber <= 10) {
                                    show_graph.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        show_graph.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(lan_managerActivity.this, topology_graphActivity.class);
                startActivity(intent);


            }
        });
        refresh.performClick();
    }

    /**
     * to update the registered devices
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            final LinearLayout devices_output = findViewById(R.id.lan_manager_output_list);
            for (int i = 0; i < devices_output.getChildCount(); i++) {
                View chunk = devices_output.getChildAt(i);
                final Switch switch_registered = chunk.findViewById(R.id.switch_registered);
                final TextView device_mac = chunk.findViewById(R.id.one_device_mac);

                if (switch_registered.isChecked()) {
                    boolean found = false;
                    for (DeviceInLAN d : new ArrayList<>(registered_Devices)) {
                        if (device_mac.getText().toString().equals(d.MAC)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        switch_registered.setChecked(false);
                    }
                }
            }
        }
    }

    /**
     * set the customer design menu(tool bar)
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lan_manager, menu);
        return true;
    }

    /**
     * set the onClick effect for every item in menu(tool bar)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lan_manager_myIp:
                if (!checkWifiOnAndConnected()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(lan_managerActivity.this, "You are currently offline", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final String myIP = IPUtil.Get_myIp_info();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(lan_managerActivity.this, myIP, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;

            case R.id.show_registered_devices:
                Intent intent = new Intent(lan_managerActivity.this, Registered_devicesActivity.class);
                startActivityForResult(intent, 1);
                break;
        }
        return true;
    }

    /**
     * Add the information of this device to a chunk
     */
    private void addLocalDevice() {
        devices.clear();

        final DeviceInLAN d = new DeviceInLAN(myIP, myMAC);
        final LinearLayout devices_output = findViewById(R.id.lan_manager_output_list);
        final TextView device_num = findViewById(R.id.device_number);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    d.setBrand();
                    d.setHostName("My Device");
                    devices.add(d);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            View chunk = getLayoutInflater().inflate(R.layout.chunk_lan_manager_output, devices_output, false);

                            TextView device_mac = chunk.findViewById(R.id.one_device_mac);
                            device_mac.setText(d.MAC);

                            TextView device_ip_ = chunk.findViewById(R.id.one_device_ip);
                            device_ip_.setText(d.IP);

                            TextView device_manufacturer = chunk.findViewById(R.id.one_device_manufacturer);
                            device_manufacturer.setText(d.brand);

                            TextView device_name = chunk.findViewById(R.id.one_device_name);
                            device_name.setText(d.hostName);

                            final Switch switch_registered = chunk.findViewById(R.id.switch_registered);

                            // load file and set attribute here
                            if (registered_Devices.size() == 0) {
                                switch_registered.setChecked(false);
                            } else {
                                if (isContained(d.MAC)) {
                                    switch_registered.setChecked(true);
                                } else {
                                    switch_registered.setChecked(false);
                                }
                            }

                            switch_registered.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    //to do
                                    boolean state = switch_registered.isChecked();
                                    if (state) {
                                        addDevice(d);
                                    } else {
                                        removeDevice(d);
                                    }
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e("save content", saveContent());
                                            IOUtil.Save_File(saveContent());

                                        }
                                    }).start();
                                }
                            });

                            //then add it
                            devices_output.addView(chunk);
                            //update number
                            device_num.setText("   " + devices.size() + "  Devices found");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Read ARP table
     */
    private void readArp() {
        lineNumber=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    line = line.trim();
                    if (line.length() < 63) continue;
                    if (line.toUpperCase(Locale.US).contains("IP")) continue;
                    String ip = line.substring(0, 17).trim();
                    String flag = line.substring(29, 32).trim();
                    String mac = line.substring(41, 63).trim();
                    if (mac.contains("00:00:00:00:00:00")) continue;
                    Log.e("scanner", "readArp: mac= " + mac + " ; ip= " + ip + " ;flag= " + flag);
                    final DeviceInLAN d = new DeviceInLAN(ip, mac);

                    final LinearLayout devices_output = findViewById(R.id.lan_manager_output_list);
                    final TextView device_num = findViewById(R.id.device_number);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                d.setBrand();
                                d.setHostName();
                                devices.add(d);
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        //just to show the scroll view
                                        //how to add a chunk
                                        View chunk = getLayoutInflater().inflate(R.layout.chunk_lan_manager_output, devices_output, false);

                                        TextView device_mac = chunk.findViewById(R.id.one_device_mac);
                                        device_mac.setText(d.MAC);

                                        TextView device_ip_ = chunk.findViewById(R.id.one_device_ip);
                                        device_ip_.setText(d.IP);

                                        TextView device_manufacturer = chunk.findViewById(R.id.one_device_manufacturer);
                                        device_manufacturer.setText(d.brand);

                                        TextView device_name = chunk.findViewById(R.id.one_device_name);
                                        device_name.setText(d.hostName);

                                        final Switch switch_registered = chunk.findViewById(R.id.switch_registered);
                                        // load file and set attribute here
                                        if (registered_Devices.size() == 0) {
                                            switch_registered.setChecked(false);
                                        } else {
                                            if (isContained(d.MAC)) {
                                                switch_registered.setChecked(true);
                                            } else {
                                                switch_registered.setChecked(false);
                                            }

                                        }
                                        switch_registered.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                //to do
                                                boolean state = switch_registered.isChecked();
                                                if (state) {
                                                    addDevice(d);
                                                } else {
                                                    removeDevice(d);
                                                }

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.e("save content", saveContent());
                                                        IOUtil.Save_File(saveContent());
                                                    }
                                                }).start();
                                            }
                                        });

                                        //then add it
                                        devices_output.addView(chunk);
                                        //update number
                                        device_num.setText("   " + devices.size() + "  Devices found");


                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                } catch (Exception e) {
                }
            }
            br.close();

        } catch (Exception e) {
        }
    }

    /**
     * Scan devices in current network
     *
     * @throws Exception
     */
    public void scanSubnet() throws Exception {
        WifiManager wm = null;
        try {
            wm = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        } catch (Exception e) {
            wm = null;
        }
        if (wm != null && wm.isWifiEnabled()) {
            WifiInfo wifi = wm.getConnectionInfo();

            if (wifi.getRssi() != -200) {
                myIpBytes = getWifiIPAddress(wifi.getIpAddress());
            }
        }

        for (int i = 1; i <= 255; i++) {
            myIpBytes[3] = (byte) i;
            InetAddress address = InetAddress.getByAddress(myIpBytes);
            Thread ut = new UDPThread(address.getHostAddress());
            ut.start();
        }
    }

    /**
     * Get local IP address in bytes
     *
     * @param ipaddr
     * @return
     */
    private byte[] getWifiIPAddress(int ipaddr) {
        if (ipaddr == 0) return null;
        byte[] addressBytes = {(byte) (0xff & ipaddr), (byte) (0xff & (ipaddr >> 8)),
                (byte) (0xff & (ipaddr >> 16)), (byte) (0xff & (ipaddr >> 24))};
        return addressBytes;
    }

    /**
     * Set local IP and MAC address
     */
    public void setLocalAddress() {
        myIP = getLocalIpAddress();
        myMAC = getMacAddr();
        Log.e("my info", myIP + " " + myMAC);
    }

    /**
     * Get local MAC address
     *
     * @return
     */
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get local IP address in String
     * @return
     */
    public String getLocalIpAddress() {
        WifiManager wm = null;
        try {
            wm = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        } catch (Exception e) {
            wm = null;
        }
        if (wm != null && wm.isWifiEnabled()) {
            WifiInfo wifi = wm.getConnectionInfo();

            if (wifi.getRssi() != -200) {
                return int2ip(wifi.getIpAddress());
            }
        }
        return "";
    }

    /**
     * A helper for converting integer to IP address
     *
     * @param ipInt
     * @return
     */
    public String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * Check whether a device is registered
     *
     * @param MAC
     * @return
     */
    private boolean isContained(String MAC) {
        for (DeviceInLAN d : registered_Devices) {
            if (MAC.equals(d.MAC)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add one device to the registered table
     *
     * @param toAdd
     */
    private void addDevice(DeviceInLAN toAdd) {
        String MAC = toAdd.MAC;
        if (!isContained(MAC)) {
            registered_Devices.add(toAdd);
        }
    }

    /**
     * Remove one device from the registered table
     *
     * @param toRemove
     */
    private void removeDevice(DeviceInLAN toRemove) {
        for (DeviceInLAN d : new ArrayList<>(registered_Devices)) {
            if (toRemove.MAC.equals(d.MAC)) {
                registered_Devices.remove(d);
            }
        }
    }

    /**
     * Generate text for save
     *
     * @return
     */
    private String saveContent() {
        StringBuffer toSave = new StringBuffer();
        for (DeviceInLAN d : registered_Devices) {
            toSave.append(d.toString());
        }
        return toSave.toString();
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

    /**
     * Directly print dialog to warn user to check Internet connection
     */
    private void noInternetConnectionDialog() {
        if (hasDialog) {
            return;
        } else {
            hasDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(lan_managerActivity.this);
            builder.setTitle("oops!");
            builder.setMessage("You are currently offline -_- !");
            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    hasDialog = false;
                }
            });
            builder.create().show();
        }

    }
}
