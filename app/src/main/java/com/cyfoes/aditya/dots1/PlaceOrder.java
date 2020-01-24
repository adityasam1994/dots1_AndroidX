package com.cyfoes.aditya.dots1;

public class PlaceOrder {
    String service, servicetype, time, Service_date,
            eaddress, username, latitude, longitude, ecomment, cost, code, qrcode, Date, format, status;

    public PlaceOrder(String service, String servicetype, String time, String Service_date, String eaddress, String username,
                      String latitude, String longitude, String ecomment, String cost, String code, String qrcode, String Date, String format) {
        this.service = service;
        this.servicetype = servicetype;
        this.time = time;
        this.Service_date = Service_date;
        this.eaddress = eaddress;
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ecomment = ecomment;
        this.cost = cost;
        this.code = code;
        this.qrcode = qrcode;
        this.Date = Date;
        this.format = format;
        this.status = "pending";
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getServicetype() {
        return servicetype;
    }

    public void setServicetype(String servicetype) {
        this.servicetype = servicetype;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getService_date() {
        return Service_date;
    }

    public void setService_date(String service_date) {
        Service_date = service_date;
    }

    public String getEaddress() {
        return eaddress;
    }

    public void setEaddress(String eaddress) {
        this.eaddress = eaddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getEcomment() {
        return ecomment;
    }

    public void setEcomment(String ecomment) {
        this.ecomment = ecomment;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
