package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class ShowGeoInfo extends AppCompatActivity {
    public String res;
    String input;
    TextView outputInfo;
    private String TAG = "myMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_geo_info);

        input = getIntent().getStringExtra("geoInfo");


        System.out.println(input);
        outputInfo = findViewById(R.id.output_Info);




        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    res = GetIp_info(input);
                    Log.i("IP Address：", res);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        outputInfo.setText(res);
                    }
                });
            }
        }).start();









        //outputInfo.setText(test);


        ImageButton back_to_inputIp = findViewById(R.id.back_to_input_ip);
        back_to_inputIp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ShowGeoInfo.this, GeolocationActivity.class);
                startActivity(intent);
            }
        });
    }

    public String GetIp_info(String queryIP) throws UnknownHostException {
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

    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
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

    public String Domain2IP(String domainName) throws UnknownHostException {
        String IP = "";
        try {
            InetAddress giriAddress = java.net.InetAddress.getByName(domainName);
            IP = giriAddress.getHostAddress().toString();
        } catch (Exception e) {
            IP = "";
            Log.e(TAG, e.toString());
        }
        return IP;
    }

    public boolean isValidInet4Address(String ip) {
        try {
            return Inet4Address.getByName(ip).getHostAddress().equals(ip);
        } catch (UnknownHostException ex) {
            return false;
        }
    }

    public String IP_for_query(String input) throws UnknownHostException {
        char c = input.charAt(0);
        if (Character.isDigit(c)) {
            if (isValidInet4Address(input)) {
                return input;
            } else {
                Log.e(TAG, "Invalid IP!!!!!!!!!!!!!");
                return "";
            }
        } else {
            return Domain2IP(input);
        }

    }
}
