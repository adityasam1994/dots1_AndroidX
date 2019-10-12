package aditya.cyfoes.com.dots1;

public class PlaceOrder {
    String service, servicetype, time, Service_Date,
            eaddress, username, latitude, longitude, ecomment, cost, code, qrcode, date;

    public PlaceOrder(String service, String servicetype, String time, String service_date, String eaddress, String username,
                      String latitude, String longitude, String ecomment, String cost, String code, String qrcode, String date) {
        this.service = service;
        this.servicetype = servicetype;
        this.time = time;
        Service_Date = service_date;
        this.eaddress = eaddress;
        this.username = username;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ecomment = ecomment;
        this.cost = cost;
        this.code = code;
        this.qrcode = qrcode;
        this.date = date;
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

    public String getService_Date() {
        return Service_Date;
    }

    public void setService_Date(String service_date) {
        Service_Date = service_date;
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
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
