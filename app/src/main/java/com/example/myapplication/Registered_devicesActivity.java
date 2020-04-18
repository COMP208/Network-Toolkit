package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * last update: 2020/4/12
 * by: Minhao.Jin
 *
 * last update: 2020/4/12
 * by: Weihao.Jin
 */
public class Registered_devicesActivity extends AppCompatActivity {

    ArrayList<DeviceInLAN> registered_Devices;

    /**
     *
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_devices);

        UI_control.setCustomActionBar(Registered_devicesActivity.this, R.layout.actionbar_registered_devices);

        final ImageButton goLanManager = findViewById(R.id.back_to_lan_manager_from_registeredDevices);
        final TextView device_num = findViewById(R.id.registered_device_number);
        final LinearLayout devices_output = findViewById(R.id.registered_devices_list);

        goLanManager.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        registered_Devices = IOUtil.Read_File();
        //show the num of registered device
        device_num.setText("   " + registered_Devices.size() + "  Devices registered");

        for (DeviceInLAN device:registered_Devices) {
            final DeviceInLAN d=device;
            View chunk = getLayoutInflater().inflate(R.layout.chunk_lan_manager_output,
                    devices_output, false);

            TextView device_mac = chunk.findViewById(R.id.one_device_mac);
            device_mac.setText(d.MAC);

            TextView device_ip = chunk.findViewById(R.id.one_device_ip);
            device_ip.setText(d.IP);

            TextView device_manufacturer = chunk.findViewById(R.id.one_device_manufacturer);
            device_manufacturer.setText(d.brand);

            TextView device_name = chunk.findViewById(R.id.one_device_name);
            device_name.setText(d.hostName);

            final Switch switch_registered = chunk.findViewById(R.id.switch_registered);
            // all is registered
            switch_registered.setChecked(true);

            switch_registered.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //to do

                    boolean state = switch_registered.isChecked();
                    if (state) {
                        addDevice(d);
                    } else {
                        removeDevice(d);
                    }
                    lan_managerActivity.registered_Devices=registered_Devices;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("save content", saveContent());
                            IOUtil.Save_File(saveContent());
                        }
                    }).start();
                }
            });

            devices_output.addView(chunk);

        }


        //If needed, add them in UI thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    /**
     *Check whether a device is registered
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
}
