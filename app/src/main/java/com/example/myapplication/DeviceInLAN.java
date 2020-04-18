package com.example.myapplication;

import org.json.JSONObject;

import jcifs.netbios.NbtAddress;

/**
 * last update: 2020/4/12
 * by: Minhao.Jin
 */
public class DeviceInLAN {

    public String IP;
    public String MAC;
    public String hostName = "Generic";
    public String brand = "-";
    public String type = "unknown";

    String[] phone = {"HUAWEI", "Xiaomi", "Apple", "Vivo", "Oppo", "OnePlus", "Samsung", "Nintendo"};
    String[] pc = {"Intel", "Liteon", "Hon Hai", "Samoa", "AzureWave", "Microsoft"};

    /**
     * @param IP
     * @param MAC
     */
    public DeviceInLAN(String IP, String MAC) {
        this.IP = IP;
        this.MAC = MAC;
    }

    /**
     * @param IP
     * @param MAC
     * @param hostName
     * @param brand
     */
    public DeviceInLAN(String IP, String MAC, String hostName, String brand) {
        this.IP = IP;
        this.MAC = MAC;
        this.hostName = hostName;
        this.brand = brand;
    }

    /**
     * Set device's brand according to its MAC address
     * Set device's type
     *
     * @throws Exception
     */
    public void setBrand() throws Exception {
        String res = "";
        int start = 0;
        int end = 0;

        //obtain the brand from macvendors api
        String url = "https://macvendors.co/api/" + MAC;
        try {
            JSONObject jsonObject = IPUtil.readJsonFromUrl(url);
            res = jsonObject.getString("result");
            start = res.indexOf("\":\"");
            end = res.indexOf("\",\"");
        } catch (Exception e) {
            e.printStackTrace();
            brand = "-";
            return;
        } finally {
            if (start != -1 || end != -1) {
                try {
                    brand = res.substring(start + 3, end);
                } catch (Exception e) {
                    brand = "-";
                    return;
                }
            } else {
                brand = "-";
            }
        }

        //set device's type
        String helper1 = brand.toLowerCase();
        for (String b : phone) {
            String helper2 = b.toLowerCase();
            if (helper1.contains(helper2)) {
                brand = b;
                type = "phone";
            }
        }

        for (String p : pc) {
            String helper2 = p.toLowerCase();
            if (helper1.contains(helper2)) {
                brand = p;
                type = "pc";
            }
        }

        if (IP.endsWith(".1")) {
            type = "router";
            return;
        }

    }

    /**
     * Set device's hostname
     *
     * @throws Exception
     */
    public void setHostName() throws Exception {
        String res = "";
        try {
            NbtAddress[] nbts = NbtAddress.getAllByAddress(IP);
            res = nbts[0].getHostName();
        } catch (Exception e) {
            e.printStackTrace();
            res = "Generic";
        } finally {
            hostName = res;
        }
    }

    /**
     * Set device's hostname
     *
     * @param hostName
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Return details of this device
     *
     * @return
     */
    public String toString() {
        return IP + ";" + MAC + ";" + hostName + ";" + brand + "\n";
    }

}
