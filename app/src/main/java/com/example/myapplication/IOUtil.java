package com.example.myapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * last update: 2020/4/12
 * by: Minhao.Jin
 */

public class IOUtil {

    /**
     * Save Sting to .txt file
     *
     * @param content
     */
    public static void Save_File(String content) {
        FileOutputStream fs = null;
        try {
            File file = new File("data/data/com.example.myapplication/test.txt");
            fs = new FileOutputStream(file);
            fs.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Read information about the registered devices
     *
     * @return
     */
    public static ArrayList<DeviceInLAN> Read_File() {
        ArrayList<DeviceInLAN> devices = new ArrayList();
        File file = new File("data/data/com.example.myapplication/test.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] attributes = line.split(";");
                DeviceInLAN d = new DeviceInLAN(attributes[0], attributes[1], attributes[2], attributes[3]);
                devices.add(d);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices;
    }

}
