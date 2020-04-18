package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * last update: 2020/4/12
 * by: Pengcheng.Jin
 */

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private ArrayList<LatLng> locate = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UI_control.setCustomActionBar(MapsActivity.this, R.layout.actionbar_map);

        ImageButton back_to_inputIp = findViewById(R.id.back_to_input_ip);
        back_to_inputIp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Turn MAP model to dark by json file
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.night));


        //add marker in MAP for every IP
        ArrayList<IP_info> ip_infoArray=IPUtil.IP_info_container;
        for (int i = 0; i < ip_infoArray.size(); i++) {
            IP_info ip_info =ip_infoArray.get(i);
            if(!ip_info.latitude.equals("")&&!ip_info.longitude.equals("")){
                LatLng location = new LatLng(ip_info.getLatitude(), ip_info.getLongitude());
                mMap.addMarker(new MarkerOptions().position(location).title("IP Information " + (i+1)).snippet(ip_info.toString()));
                locate.add(location);
            }

        }


        //move map camera to the first marker
        mMap.moveCamera(CameraUpdateFactory.newLatLng(locate.get(0)));

        //extend marker text frame to contain all IP information
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                      @Override
                                      public View getInfoWindow(Marker arg0) {
                                          return null;
                                      }

                                      @Override

                                      public View getInfoContents(Marker marker) {

                                          Context context = getApplicationContext();

                                          LinearLayout info = new LinearLayout(context);
                                          info.setOrientation(LinearLayout.VERTICAL);
                                          TextView title = new TextView(context);
                                          title.setTextColor(Color.BLACK);
                                          title.setGravity(Gravity.CENTER);
                                          title.setTypeface(null, Typeface.BOLD);
                                          title.setText(marker.getTitle());
                                          TextView snippet = new TextView(context);
                                          snippet.setTextColor(Color.GRAY);
                                          snippet.setText(marker.getSnippet());
                                          info.addView(title);
                                          info.addView(snippet);

                                          return info;
                                      }
                                  }
        );


        //start Animation
        if(ip_infoArray.size()>1){

            MapAnimator.getInstance().animateRoute(mMap, locate);
        }
    }
}

