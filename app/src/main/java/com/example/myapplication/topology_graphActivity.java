package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.net.UnknownHostException;

/**
 * last update: 2020/4/12
 * by: Weihao.Jin
 */

public class topology_graphActivity extends AppCompatActivity {

    myCanvas myCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topology_graph);

        myCanvas=new myCanvas(this);
        myCanvas.setOnTouchListener(new DragTouchListener());
        myCanvas.setBackgroundColor(Color.parseColor("#1A1A1A"));
        ConstraintLayout bitmap_container = findViewById(R.id.bitmap_container);
        bitmap_container.addView(myCanvas);


        //set custom action bar
        UI_control.setCustomActionBar(topology_graphActivity.this, R.layout.actionbar_topology_graph);

        //Initialize controls by id
        final ImageButton go_lan_manager = findViewById(R.id.back_to_lan_manager);

        go_lan_manager.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * set the customer design menu(tool bar)
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_topoloy_graph, menu);
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
            case R.id.topology_graph_myIp:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final String myIP = IPUtil.Get_myIp_info();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(topology_graphActivity.this, myIP, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            case R.id.reset_graph:
                ConstraintLayout bitmap_container = findViewById(R.id.bitmap_container);
                bitmap_container.removeAllViews();

                myCanvas=new myCanvas(this);
                myCanvas.setOnTouchListener(new DragTouchListener());
                myCanvas.setBackgroundColor(Color.parseColor("#1A1A1A"));

                bitmap_container.addView(myCanvas);
                break;
        }
        return true;
    }
}
