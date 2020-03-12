package mobile.android.trip.planner.app.model;

public class UserBean {
    double latitude;
    String emailId;
    String location;
    String name;
    String password;
    String phone;
double longitude;
    String username;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }



    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserBean(double latitude, String emailId, String location, String name, String password, String phone, double longitude, String username) {
        this.latitude = latitude;
        this.emailId = emailId;
        this.location = location;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.longitude = longitude;
        this.username = username;
    }
}
