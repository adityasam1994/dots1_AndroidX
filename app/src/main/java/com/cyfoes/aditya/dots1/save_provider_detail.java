package com.cyfoes.aditya.dots1;

public class save_provider_detail {
    String eservice, eage, eavailable, ecomment, eaddress;
    double lati, longi;

    public save_provider_detail(String eservice, String eage, String eavilable, String ecomment, String eaddress, double lati, double longi) {
        this.eservice = eservice;
        this.eage = eage;
        this.eavailable = eavilable;
        this.ecomment = ecomment;
        this.eaddress = eaddress;
        this.lati = lati;
        this.longi = longi;
    }

    public String getEservice() {
        return eservice;
    }

    public void setEservice(String eservice) {
        this.eservice = eservice;
    }

    public String getEage() {
        return eage;
    }

    public void setEage(String eage) {
        this.eage = eage;
    }

    public String getEavailable() {
        return eavailable;
    }

    public void setEavailable(String eavailable) {
        this.eavailable = eavailable;
    }

    public String getEcomment() {
        return ecomment;
    }

    public void setEcomment(String ecomment) {
        this.ecomment = ecomment;
    }

    public String getEaddress() {
        return eaddress;
    }

    public void setEaddress(String eaddress) {
        this.eaddress = eaddress;
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
