package aditya.cyfoes.com.dots1;

public class newuser {
    String fname, lname, address, ph;
    double lati, longi;

    public newuser(String first_name, String last_name, String address, String phone, Double latitude, Double longitude) {
        this.fname = first_name;
        this.lname = last_name;
        this.address = address;
        this.ph = phone;
        this.lati = latitude;
        this.longi = longitude;
    }

    public String getFirst_name() {
        return fname;
    }

    public void setFirst_name(String first_name) {
        this.fname = first_name;
    }

    public String getLast_name() {
        return lname;
    }

    public void setLast_name(String last_name) {
        this.lname = last_name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return ph;
    }

    public void setPhone(String phone) {
        this.ph = phone;
    }

    public double getLatitude() {
        return lati;
    }

    public void setLatitude(double latitude) {
        this.lati = latitude;
    }

    public double getLongitude() { return longi; }

    public void setLongitude(double longitude) {
        this.longi = longitude;
    }
}
