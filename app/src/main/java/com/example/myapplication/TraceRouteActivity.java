package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * last update: 2020/4/12
 * by: Minhao.Jin
 *
 * last update: 2020/4/12
 * by: Weihao.Jin
 */
public class TraceRouteActivity extends AppCompatActivity {

    //if current activity already has a dialog showing or to show
    private boolean hasDialog = false;
    private String tracertLog = "";
    public boolean isCTrace = true;
    public ArrayList<String> IP_container = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traceroute);
        UI_control.setCustomActionBar(TraceRouteActivity.this, R.layout.actionbar_traceroute);

        //Initialize controls by id
        final LinearLayout traceroute_output = findViewById(R.id.traceroute_output_list);
        final LinearLayout loading = findViewById(R.id.loading_traceroute);
        final EditText input_ip = findViewById(R.id.input_ip_for_traceroute);
        final ImageButton clearInput = findViewById(R.id.clear_traceroute_ip);
        final ImageButton goMenu = findViewById(R.id.back_to_menu_from_traceroute);
        final ImageButton showMap = findViewById(R.id.show_traceroute_map);
        final ImageButton enter_ip = findViewById(R.id.enter_traceroute_ip);
        final ScrollView scroll_for_output = findViewById(R.id.traceroute_scroll);

        //set outputInfo, clearText and loading animation to INVISIBLE
        clearInput.setVisibility(View.INVISIBLE);
        traceroute_output.setVisibility(View.INVISIBLE);
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
                scroll_for_output.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                traceroute_output.setVisibility(View.INVISIBLE);
                UI_control.hideSoftKeyboard(TraceRouteActivity.this);
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
                                        traceroute_output.setVisibility(View.INVISIBLE);
                                        invalidIPDialog();
                                    }
                                });


                            } else {
                                IPUtil.IP_info_container = new ArrayList<>();
                                IP_container = new ArrayList<>();
                                startTraceRoute(queryIP);
                                IPUtil.IP2IP_info(IP_container);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setVisibility(View.INVISIBLE);
                                        traceroute_output.setVisibility(View.INVISIBLE);
                                    }
                                });

                                if (IPUtil.IP_info_container.size() > 0) {
                                    Intent intent = new Intent(TraceRouteActivity.this, MapsActivity.class);
                                    intent.putExtra("prevActivity", "TraceRoute");
                                    startActivity(intent);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    exceptionDialog();
                                    loading.setVisibility(View.INVISIBLE);
                                    traceroute_output.setVisibility(View.INVISIBLE);
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
                scroll_for_output.setVisibility(View.VISIBLE);
                loading.setVisibility(View.VISIBLE);
                traceroute_output.setVisibility(View.VISIBLE);
                traceroute_output.removeAllViews();
                UI_control.hideSoftKeyboard(TraceRouteActivity.this);
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
                                        traceroute_output.setVisibility(View.VISIBLE);
                                        invalidIPDialog();
                                    }
                                });

                            } else {
                                tracertLog = "";
                                startTraceRoute(queryIP);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.setVisibility(View.INVISIBLE);
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
                                    traceroute_output.setVisibility(View.INVISIBLE);
                                }
                            });

                        }
                    }
                }).start();
            }
        });
    }

    /**
     * Emulate TraceRoute process
     *
     * @param host
     */
    public void startTraceRoute(String host) {
        if (isCTrace && loaded) {
            try {
                startJNICTraceRoute(host);
            } catch (UnsatisfiedLinkError e) {
                e.printStackTrace();
                TraceRouteActivity.TraceTask trace = new TraceRouteActivity.TraceTask(host, 1);
                execTrace(trace);
            }
        } else {
            TraceRouteActivity.TraceTask trace = new TraceRouteActivity.TraceTask(host, 1);
            execTrace(trace);
        }
    }

    public native void startJNICTraceRoute(String traceCommand);

    static boolean loaded;
    private static final String MATCH_TRACE_IP = "(?<=From )(?:[0-9]{1,3}\\.){3}[0-9]{1,3}";
    private static final String MATCH_PING_IP = "(?<=from ).*(?=: icmp_seq=1 ttl=)";
    private static final String MATCH_PING_TIME = "(?<=time=).*?ms";

    /**
     * Execute ping command
     *
     * @param ping
     * @return
     */
    private String execPing(TraceRouteActivity.PingTask ping) {
        Process process = null;
        String str = "";
        BufferedReader reader = null;
        try {
            process = Runtime.getRuntime().exec("ping -c 1 " + ping.getHost());
            reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                str += line;
            }
            reader.close();
            process.waitFor();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return str;
    }

    /**
     * execute ping to simulate TraceRoute
     *
     * @param trace
     * @return
     */
    private void execTrace(TraceRouteActivity.TraceTask trace) {
        Pattern patternTrace = Pattern.compile(MATCH_TRACE_IP);
        Pattern patternIp = Pattern.compile(MATCH_PING_IP);
        Pattern patternTime = Pattern.compile(MATCH_PING_TIME);

        Process process = null;
        BufferedReader reader = null;
        boolean finish = false;
        try {
            while (!finish && trace.getHop() < 30) {
                String str = "";
                String command = "ping -c 1 -t " + trace.getHop() + " "
                        + trace.getHost();

                process = Runtime.getRuntime().exec(command);
                reader = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    str += line;
                }
                reader.close();
                process.waitFor();

                Matcher m = patternTrace.matcher(str);

                StringBuilder log = new StringBuilder(256);
                if (m.find()) {
                    String pingIp = m.group();
                    TraceRouteActivity.PingTask pingTask = new TraceRouteActivity.PingTask(pingIp);

                    String status = execPing(pingTask);
                    if (status.length() == 0) {
                        log.append("unknown host or network error\n");
                        finish = true;
                    } else {
                        Matcher matcherTime = patternTime.matcher(status);
                        if (matcherTime.find()) {
                            String time = matcherTime.group();
                            log.append(trace.getHop());
                            log.append("\t\t");
                            log.append(pingIp);
                            IP_container.add(pingIp);
                            log.append("\t\t");
                            log.append(time);
                            log.append("\t");
                        } else {
                            log.append(trace.getHop());
                            log.append("\t\t");
                            log.append(pingIp);
                            IP_container.add(pingIp);
                            log.append("\t\t timeout \t");
                        }

                        trace.setHop(trace.getHop() + 1);
                    }

                } else {
                    Matcher matchPingIp = patternIp.matcher(str);
                    if (matchPingIp.find()) {
                        String pingIp = matchPingIp.group();
                        Matcher matcherTime = patternTime.matcher(str);
                        if (matcherTime.find()) {
                            String time = matcherTime.group();
                            log.append(trace.getHop());
                            log.append("\t\t");
                            log.append(pingIp);
                            IP_container.add(pingIp);
                            log.append("\t\t");
                            log.append(time);
                            log.append("\t");
                        }
                        finish = true;
                    } else {
                        if (str.length() == 0) {
                            log.append("unknown host or network error\t");
                            finish = true;
                        } else {
                            log.append(trace.getHop());
                            log.append("\t\t timeout \t");
                            trace.setHop(trace.getHop() + 1);
                        }
                    }

                }


                System.out.println(log.toString());
                final String single_message = log.toString();
                tracertLog = tracertLog + single_message + "\n";
                final LinearLayout traceroute_output = findViewById(R.id.traceroute_output_list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        View chunk = getLayoutInflater().inflate(R.layout.chunk_traceroute_output,
                                traceroute_output, false);
                        TextView message = chunk.findViewById(R.id.one_gateway_message);
                        message.setText(single_message);
                        traceroute_output.addView(chunk);
                    }
                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }

    }

    private class PingTask {

        private String host;
        private static final String MATCH_PING_HOST_IP = "(?<=\\().*?(?=\\))";

        /**
         * Get host IP
         *
         * @return
         */
        public String getHost() {
            return host;
        }

        /**
         * @param host
         */
        public PingTask(String host) {
            super();
            this.host = host;
            Pattern p = Pattern.compile(MATCH_PING_HOST_IP);
            Matcher m = p.matcher(host);
            if (m.find()) {
                this.host = m.group();
            }

        }
    }

    private class TraceTask {
        private final String host;
        private int hop;

        /**
         * @param host
         * @param hop
         */
        public TraceTask(String host, int hop) {
            super();
            this.host = host;
            this.hop = hop;
        }

        /**
         * Get Host IP
         *
         * @return
         */
        public String getHost() {
            return host;
        }

        /**
         * Get hop counts
         *
         * @return
         */
        public int getHop() {
            return hop;
        }

        /**
         * Set hop counts
         *
         * @param hop
         */
        public void setHop(int hop) {
            this.hop = hop;
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
            AlertDialog.Builder builder = new AlertDialog.Builder(TraceRouteActivity.this);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(TraceRouteActivity.this);
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
     *  Directly print dialog when an exception is caught
     */
    private void exceptionDialog() {
        if (hasDialog) {
            return;
        } else {
            hasDialog = true;
            AlertDialog.Builder builder = new AlertDialog.Builder(TraceRouteActivity.this);
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
                                        Toast.makeText(TraceRouteActivity.this, "You are currently offline", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }
                            final String myIP = IPUtil.Get_myIp_info();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TraceRouteActivity.this, myIP, Toast.LENGTH_SHORT).show();
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
}
