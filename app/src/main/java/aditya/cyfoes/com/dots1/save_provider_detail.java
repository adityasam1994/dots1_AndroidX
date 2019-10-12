package aditya.cyfoes.com.dots1;

public class save_provider_detail {
    String eservice, eage, eavailable, ecomment, eaddress;
    double lati, longi;

    public save_provider_detail(String service, String age, String servicetime, String comment, String address, double latitude, double longitude) {
        this.eservice = service;
        this.eage = age;
        this.eavailable = servicetime;
        this.ecomment = comment;
        this.eaddress = address;
        this.lati = latitude;
        this.longi = longitude;
    }

    public String getService() {
        return eservice;
    }

    public void setService(String service) {
        this.eservice = service;
    }

    public String getAge() {
        return eage;
    }

    public void setAge(String age) {
        this.eage = age;
    }

    public String getServicetime() {
        return eavailable;
    }

    public void setServicetime(String servicetime) {
        this.eavailable = servicetime;
    }

    public String getComment() {
        return ecomment;
    }

    public void setComment(String comment) {
        this.ecomment = comment;
    }

    public String getAddress() {
        return eaddress;
    }

    public void setAddress(String address) {
        this.eaddress = address;
    }

    public double getLatitude() {
        return lati;
    }

    public void setLatitude(double latitude) {
        this.lati = latitude;
    }

    public double getLongitude() {
        return longi;
    }

    public void setLongitude(double longitude) {
        this.longi = longitude;
    }
}
