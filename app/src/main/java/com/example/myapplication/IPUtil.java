package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;


/**
 * last update: 2020/4/12
 * by: Minhao.Jin
 */
public class IPUtil extends Activity {
    public static ArrayList<IP_info> IP_info_container = new ArrayList();
    private static IP_info myIPInfo;
    private static String TAG = "myMessage";

    /**
     * Obtain details of the IP from API
     *
     * @param queryIP
     * @return
     * @throws UnknownHostException
     */
    public static String GetIp_info(String queryIP) throws UnknownHostException {
        String res = "";
        queryIP = IP_for_query(queryIP);
        try {
            String url = "https://extreme-ip-lookup.com/json/" + queryIP;
            JSONObject jsonObject = readJsonFromUrl(url);
            String status = jsonObject.getString("status");

            if (status.equals("success")) {
                String IP = jsonObject.getString("query");
                String city = jsonObject.getString("city");
                String country = jsonObject.getString("country");
                String continent = jsonObject.getString("continent");
                String latitude = jsonObject.getString("lat");
                String longitude = jsonObject.getString("lon");
                IP_info i = new IP_info(IP, city, country, continent, latitude, longitude);
                IP_info_container.add(i);
                res = i.toString();

            } else {
                res = "Fail message: " + jsonObject.getString("message") + "\n";
            }
        } catch (Exception e) {
            res = "";
            Log.e(TAG, e.toString());
        }
        return res;
    }

    /**
     * Obtain details of the public IP of this device from API
     *
     * @return
     * @throws UnknownHostException
     */
    public static String Get_myIp_info() throws UnknownHostException {
        String res = "";
        try {
            String url = "https://extreme-ip-lookup.com/json/";
            JSONObject jsonObject = readJsonFromUrl(url);
            String status = jsonObject.getString("status");

            if (status.equals("success")) {
                String IP = jsonObject.getString("query");
                String city = jsonObject.getString("city");
                String country = jsonObject.getString("country");
                String continent = jsonObject.getString("continent");
                String latitude = jsonObject.getString("lat");
                String longitude = jsonObject.getString("lon");
                myIPInfo = new IP_info(IP, city, country, continent, latitude, longitude);
                res = myIPInfo.toString();
            } else {
                res = "Fail message: " + jsonObject.getString("message") + "\n";
            }
        } catch (Exception e) {
            res = "";
            Log.e(TAG, e.toString());
        }
        return res;
    }

    /**
     * A helper to read Json file
     *
     * @param url
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        StringBuilder jsonText = new StringBuilder();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String line;
            while ((line = rd.readLine()) != null) {
                jsonText.append(line + "\n");
            }
            rd.close();
            return new JSONObject(jsonText.toString());
        } finally {
            is.close();
        }
    }

    /**
     * Check input IP or domain name is valid
     *
     * @param input
     * @return
     * @throws UnknownHostException
     */
    public static String IP_for_query(String input) throws UnknownHostException {
        if(input.equals("")){
            return "invalid IP";
        }
        String IP = "";
        try {
            InetAddress addr = InetAddress.getByName(input);
            IP = addr.getHostAddress();
        } catch (Exception e) {
            IP = "invalid IP";
            Log.e(TAG, e.toString());
        }
        return IP;
    }


    /**
     * Save IP_info objects in the container
     *
     * @param IPs
     * @return
     */
    public static ArrayList<IP_info> IP2IP_info(ArrayList<String> IPs) {
        IP_info_container = new ArrayList();
        for(String ip:IPs){
            Log.e("ips",ip);
            try {
                GetIp_info(ip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        return IP_info_container;
    }

    /**
     * Check input IP or domain name is valid
     *
     *
     * @param input
     * @return
     */
    public static String valid_ping_input(String input){
        if(input.equals("")){
            return "invalid IP";
        }
        try {
            InetAddress addr = InetAddress.getByName(input);
            addr.getHostAddress();
            addr.getHostName();
        } catch (Exception e) {
            input = "invalid IP";
        }
        Log.e("ping", input);
        return input;
    }


}
