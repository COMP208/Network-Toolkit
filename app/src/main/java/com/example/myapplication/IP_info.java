package com.example.myapplication;

/**
 * last update: 2020/4/12
 * by: Minhao.Jin
 */
public class IP_info {
    private String IP;
    private String city;
    private String country;
    private String continent;
    public String latitude;
    public String longitude;

    /**
     * @param IP
     * @param city
     * @param country
     * @param continent
     * @param latitude
     * @param longitude
     */
    public IP_info(String IP,
                   String city,
                   String country,
                   String continent,
                   String latitude,
                   String longitude) {
        this.IP = IP;
        this.city = city;
        this.country = country;
        this.continent = continent;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * return details of this IP address
     *
     * @return
     */
    public String toString() {
        return "IP: " + IP + "\n" +
                "city: " + city + "\n" +
                "country: " + country + "\n" +
                "continent: " + continent + "\n" +
                "latitude: " + latitude + "\n" +
                "longitude: " + longitude;
    }


    /**
     * return latitude
     *
     * @return
     */
    public double getLatitude() {
        double res = 0;
        try {
            res = Double.parseDouble(latitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * return longitude
     *
     * @return
     */
    public double getLongitude() {
        double res = 0;
        try {
            res = Double.parseDouble(longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}