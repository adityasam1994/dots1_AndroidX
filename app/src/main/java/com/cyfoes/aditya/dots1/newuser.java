package com.cyfoes.aditya.dots1;

public class newuser {
    String fname, lname, address, ph, signup_time;
    double lati, longi;

    public newuser(String fname, String lname, String address, String ph, String signup_time ,Double lati, Double longi) {
        this.fname = fname;
        this.lname = lname;
        this.address = address;
        this.ph = ph;
        this.lati = lati;
        this.longi = longi;
        this.signup_time = signup_time;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getSignup_time() {
        return signup_time;
    }

    public void setSignup_time(String signup_time) {
        this.signup_time = signup_time;
    }

    public double getLati() {
        return lati;
    }

    public void setLati(double lati) {
        this.lati = lati;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }
}
