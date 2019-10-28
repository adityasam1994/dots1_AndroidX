package aditya.cyfoes.com.dots1;

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

    public String getService() {
        return eservice;
    }

    public void setService(String eservice) {
        this.eservice = eservice;
    }

    public String getAge() {
        return eage;
    }

    public void setAge(String eage) {
        this.eage = eage;
    }

    public String getServicetime() {
        return eavailable;
    }

    public void setServicetime(String eavilable) {
        this.eavailable = eavilable;
    }

    public String getComment() {
        return ecomment;
    }

    public void setComment(String ecomment) {
        this.ecomment = ecomment;
    }

    public String getAddress() {
        return eaddress;
    }

    public void setAddress(String eaddress) {
        this.eaddress = eaddress;
    }

    public double getLatitude() {
        return lati;
    }

    public void setLatitude(double lati) {
        this.lati = lati;
    }

    public double getLongitude() {
        return longi;
    }

    public void setLongitude(double longi) {
        this.longi = longi;
    }
}
