package com.example.spt.myapplication;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by SPT on 18/6/2558.
 */

public class FateIP {
    private Context mContext;
    public boolean getIPState = false;
    public String DEVICE_IP_ADDRESS;
    private String ipTrim;
    private ArrayList<String> arr_ip, arr_name;
    private ListView mListIP;
    private View mNormal, mLoad;

    public FateIP(Context context, ListView listIP, View normal, View load) {
        mContext = context;
        getDeviceIP();
        mListIP = listIP;
        mNormal = normal;
        mLoad = load;
    }

    public boolean isDiscovered() {
        return getIPState;
    }

    public String get(int index) {
        return arr_ip.get(index);
    }

    public ArrayList<String> getIPList() {
        return arr_ip;
    }

    private void getDeviceIP() {
        WifiManager wifiManager =
                (WifiManager) mContext.getSystemService
                        (Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        ipTrim = (ipAddress & 0xFF) + "."
                + ((ipAddress >> 8 ) & 0xFF) + "."
                + ((ipAddress >> 16 ) & 0xFF);
        DEVICE_IP_ADDRESS = (ipAddress & 0xFF) + "."
                + ((ipAddress >> 8 ) & 0xFF) + "."
                + ((ipAddress >> 16 ) & 0xFF) + "."
                + ((ipAddress >> 24 ) & 0xFF);
        Log.i("IP Discoverage",  "Device IP : " + DEVICE_IP_ADDRESS);
    }

    public void getConnectedDevices() {
        getIPState = false;

        Handler refresh = new Handler(Looper.getMainLooper());
        refresh.post(new Runnable() {
            public void run() {
                mNormal.setVisibility(View.INVISIBLE);
                mLoad.setVisibility(View.VISIBLE);
            }
        });

        arr_ip = new ArrayList<String>();
        arr_name = new ArrayList<String>();
        setListView();

        for (int i = 0; i <= 255; i++) {
            final int j = i;
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        InetAddress addr = InetAddress.getByName(ipTrim
                                + "." + String.valueOf(j));
                        Boolean get = addr.isReachable(500);
                        if (get && !DEVICE_IP_ADDRESS.equals
                                (addr.getHostAddress())) {
                            Log.e("InetAddress", String.valueOf(addr));
                            arr_ip.add(addr.getHostAddress());
                            if(!addr.getHostName().equals
                                    (addr.getHostAddress())) {
                                arr_name.add(addr.getHostName().replace
                                        (".mshome.net", ""));
                            } else {
                                arr_name.add("");
                            }
                        }

                        if(j >= 255) {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) { }

                            Handler refresh = new Handler(Looper.getMainLooper());
                            refresh.post(new Runnable() {
                                public void run() {
                                    setListView();
                                    mNormal.setVisibility(View.VISIBLE);
                                    mLoad.setVisibility(View.INVISIBLE);
                                }
                            });

                            getIPState = true;
                        }
                    } catch (UnknownHostException ex) {
                    } catch (IOException ex) { }
                }
            };
            new Thread(runnable).start();
        }
    }

    private void setListView() {
        ArrayList<String> arr_list = new ArrayList<String>();
        for(int i = 0 ; i < arr_ip.size() ; i++) {
            arr_list.add(arr_ip.get(i) + "\n" + arr_name.get(i));
        }
        mListIP.setAdapter(new ArrayAdapter<String>(mContext
                , android.R.layout.simple_list_item_1, arr_list));
    }
}

