package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class GeolocationActivity extends AppCompatActivity {


    /**
     * String that store the geolocation according to user's input
     * empty input: ""
     * invalid input: "invalid IP"
     * correct input: details about geolocation
     * <p>
     * last update: 2020/4/12
     * by: Weihao.Jin
     */
    private String res = "";

    //if current activity already has a dialog showing or to show
    private boolean hasDialog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set Content View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geolocation);

        //set custom action bar
        UI_control.setCustomActionBar(GeolocationActivity.this, R.layout.actionbar_geolocation);

        //Initialize controls by id
        final TextView outputInfo = findViewById(R.id.output_Info);
        final LinearLayout loading = findViewById(R.id.loading);
        final EditText input_ip = findViewById(R.id.input_ip);
        final ImageButton clearInput = findViewById(R.id.clear_input);
        final ImageButton goMenu = findViewById(R.id.back_to_menu);
        final ImageButton showMap = findViewById(R.id.show_map);
        final ImageButton enter_ip = findViewById(R.id.enter_ip);

        //set outputInfo, clearText and loading animation to INVISIBLE
        clearInput.setVisibility(View.INVISIBLE);
        outputInfo.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.INVISIBLE);


        goMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        UI_control.editTextListener(input_ip, clearInput);

        showMap.setOnClickListener(new View.OnClickListener() {
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
                loading.setVisibility(View.VISIBLE);
                outputInfo.setVisibility(View.INVISIBLE);
                UI_control.hideSoftKeyboard(GeolocationActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String queryIP = IPUtil.IP_for_query(input_ip.getText().toString());
                            Thread.sleep(2000);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loading.setVisibility(View.INVISIBLE);
                                    outputInfo.setVisibility(View.INVISIBLE);
                                }
                            });
                            if (queryIP.equals("invalid IP")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        invalidIPDialog();
                                    }
                                });

                            } else {
                                IPUtil.IP_info_container = new ArrayList<>();
                                res = IPUtil.GetIp_info(input_ip.getText().toString());
                                if (!res.equals("")) {
                                    Intent intent = new Intent(GeolocationActivity.this, MapsActivity.class);
                                    intent.putExtra("prevActivity", "Geolocation");
                                    startActivity(intent);
                                }
                                Log.i("IP Address：", res);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    exceptionDialog();
                                    loading.setVisibility(View.INVISIBLE);
                                    outputInfo.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    }
                }).start();
            }
        });


        enter_ip.setOnClickListener(new View.OnClickListener() {
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
                loading.setVisibility(View.VISIBLE);
                outputInfo.setVisibility(View.INVISIBLE);
                UI_control.hideSoftKeyboard(GeolocationActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String queryIP = IPUtil.IP_for_query(input_ip.getText().toString());
                            Thread.sleep(2000);
                            if (queryIP.equals("invalid IP")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setVisibility(View.INVISIBLE);
                                        outputInfo.setVisibility(View.INVISIBLE);
                                        invalidIPDialog();
                                    }
                                });

                            } else {
                                res = IPUtil.GetIp_info(input_ip.getText().toString());
                                Log.i("IP Address：", res);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        outputInfo.setMaxHeight(500);
                                        outputInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
                                        outputInfo.setText(res);
                                        loading.setVisibility(View.INVISIBLE);
                                        outputInfo.setVisibility(View.VISIBLE);
                                    }
                                });
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    exceptionDialog();
                                    loading.setVisibility(View.INVISIBLE);
                                    outputInfo.setVisibility(View.INVISIBLE);
                                }
                            });
                        }

                    }
                }).start();
            }
        });


    }

    /**
     * Directly print dialog to warn user to input valid ip
     */
    private void invalidIPDialog() {
        if (hasDialog) {
            return;
        } else {
            hasDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(GeolocationActivity.this);
            builder.setTitle("oops!");
            builder.setMessage("Please input a valid IP/domain name -_- !");
            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    hasDialog = false;
                }
            });
            builder.create().show();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(GeolocationActivity.this);
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

    /**
     * Directly print dialog when an exception is caught
     */
    private void exceptionDialog() {
        if (hasDialog) {
            return;
        } else {
            hasDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(GeolocationActivity.this);
            builder.setTitle("oops!");
            builder.setMessage("We didn't find this ip, you may be currently offline -_- !");
            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    hasDialog = false;
                }
            });
            builder.create().show();
        }

    }

    /**
     * If users touch the blank area on the screen (lose focus), the soft keyboard will disappear
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN &&
                getCurrentFocus() != null &&
                getCurrentFocus().getWindowToken() != null) {

            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        return super.onTouchEvent(event);
    }

    /**
     * set the customer design menu(tool bar)
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * set the onClick effect for every item in menu(tool bar)
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!checkWifiOnAndConnected()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GeolocationActivity.this, "You are currently offline", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                            final String myIP = IPUtil.Get_myIp_info();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GeolocationActivity.this, myIP, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
        }
        return true;
    }

    /**
     * @return
     */
    private boolean checkWifiOnAndConnected() {
        WifiManager wifiMgr = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            if (wifiInfo.getNetworkId() == -1) {
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        } else {
            return false; // Wi-Fi adapter is OFF
        }
    }
}


