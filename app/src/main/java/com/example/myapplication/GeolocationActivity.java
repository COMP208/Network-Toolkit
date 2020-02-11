package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class GeolocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocation);

        ImageButton enter_ip = findViewById(R.id.enter_ip);
        enter_ip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(GeolocationActivity.this, ShowGeoInfo.class);
                EditText input_ip = findViewById(R.id.input_ip);
                intent.putExtra("geoInfo",input_ip.getText()+"");
                startActivity(intent);
            }
        });

        Button goMenu = findViewById(R.id.back_to_menu);
        goMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(GeolocationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
