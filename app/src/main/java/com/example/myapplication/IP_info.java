package com.example.myapplication;

public class IP_info {
    private String IP;
    private String city;
    private String country;
    private String continent;
    private String latitude;
    private String longitude;

    public IP_info(String IP,
                   String city,
                   String country,
                   String continent,
                   String latitude,
                   String longitude){
        this.IP=IP;
        this.city=city;
        this.country=country;
        this.continent=continent;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String toString(){
        return "IP: " + IP + "\n" +
                "city: " + city + "\n" +
                "country: " + country + "\n" +
                "continent: " + continent + "\n" +
                "latitude: " + latitude + "\n" +
                "longitude: " + longitude;
    }

    public double getLatitude(){
        return Double.parseDouble(latitude);
    }

    public double getLongitude(){
        return Double.parseDouble(longitude);
    }

}