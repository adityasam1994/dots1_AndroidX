package com.cyfoes.aditya.dots1;

public class gsignin {

    String fname;
    String lname;
    String profilepic;

    public gsignin(String fname, String lname, String profilepic)
    {
        this.fname = fname;
        this.lname = lname;
        this.profilepic = profilepic;
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

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }
}
