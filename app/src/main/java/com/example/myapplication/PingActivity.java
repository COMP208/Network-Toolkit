package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

/**
 * last update: 2020/4/12
 * by: Minhao.Jin
 * <p>
 * last update: 2020/4/12
 * by: Weihao.Jin
 */
public class PingActivity extends AppCompatActivity {

    private boolean hasDialog = false;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

        //set custom action bar
        UI_control.setCustomActionBar(PingActivity.this, R.layout.actionbar_ping);

        //Initialize controls by id
        final ImageButton clearPingIP = findViewById(R.id.clear_pingIP);
        final ImageButton clearPackage = findViewById(R.id.clear_packageNum);
        final ImageButton startPing = findViewById(R.id.start_ping);
        final EditText pingIP = findViewById(R.id.input_pingIP);
        final EditText pingPackage = findViewById(R.id.input_packageNum);
        final LinearLayout loadingPing = findViewById(R.id.loading_ping);
        final ImageButton goMenu = findViewById(R.id.back_to_menu_fromping);
        final LinearLayout ping_output = findViewById(R.id.ping_output_list);


        //set outputInfo, clearText and loading animation to INVISIBLE
        clearPackage.setVisibility(View.INVISIBLE);
        clearPingIP.setVisibility(View.INVISIBLE);
        loadingPing.setVisibility(View.INVISIBLE);
        ping_output.setVisibility(View.INVISIBLE);

        goMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        UI_control.editTextListener(pingIP, clearPingIP);
        UI_control.editTextListener(pingPackage, clearPackage);

        startPing.setOnClickListener(new View.OnClickListener() {
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
                loadingPing.setVisibility(View.VISIBLE);
                ping_output.setVisibility(View.VISIBLE);
                ping_output.removeAllViews();
                UI_control.hideSoftKeyboard(PingActivity.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (IPUtil.valid_ping_input(pingIP.getText().toString().trim()).equals("invalid IP")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    invalidIPDialog();
                                }
                            });

                        } else {
                            String input_packet_number = pingPackage.getText().toString().trim();
                            if(input_packet_number.startsWith("0") || input_packet_number.length() > 3){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        invalidPacketNumberDialog();
                                    }
                                });
                            }else{
                                int package_number = 4;
                                if(!input_packet_number.equals("")){
                                    package_number = Integer.parseInt(pingPackage.getText().toString());
                                }
                                String res = ping(pingIP.getText().toString(), package_number);
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingPing.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                }).start();

            }
        });
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
     * Directly print dialog to warn user to check Internet connection
     */
    private void noInternetConnectionDialog() {
        if (hasDialog) {
            return;
        } else {
            hasDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(PingActivity.this);
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
     * Directly print dialog to warn user to input valid ip
     */
    private void invalidIPDialog() {
        if (hasDialog) {
            return;
        } else {
            hasDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(PingActivity.this);
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
     * Directly print dialog to warn user to input valid packet number
     */
    private void invalidPacketNumberDialog() {
        if (hasDialog) {
            return;
        } else {
            hasDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(PingActivity.this);
            builder.setTitle("oops!");
            builder.setMessage("Please input a valid packet number!\n(The acceptable packet number is from 1 to 999)");
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
                                        Toast.makeText(PingActivity.this, "You are currently offline", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                            final String myIP = IPUtil.Get_myIp_info();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PingActivity.this, myIP, Toast.LENGTH_SHORT).show();
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
     * Execute Ping command
     *
     * @param url
     * @param count
     * @return
     */
    private String ping(String url, int count) {
        String str = "";
        try {
            String cmd = "/system/bin/ping -c " + count + " " + url;
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            int i;
            char[] buffer = new char[4096];
            final StringBuffer output = new StringBuffer();
            final LinearLayout ping_output = findViewById(R.id.ping_output_list);
            while ((i = reader.read(buffer)) > 0) {
                final int j = i;
                final char[] buffer2 = buffer;
                output.append(buffer, 0, i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View chunk = getLayoutInflater().inflate(R.layout.chunk_ping_output,
                                ping_output, false);
                        TextView message = chunk.findViewById(R.id.one_ping_message);
                        message.setText(String.valueOf(buffer2, 0, j));
                        ping_output.addView(chunk);
                    }
                });
            }
            reader.close();
            str = output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
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
