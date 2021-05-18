package pt.unl.fct.di.example.teste3;

import com.google.gson.annotations.SerializedName;

public class RegisterUser {

    @SerializedName("username")
    private String username;
    private String password;
    private String confirmation;
    private String email;
    private String firstName;
    private String lastName;
    private String address;
    private String mobile;
    private String role;
    private String state;
    private String postal;
    private String landLine;
    private String secondAddress;
    private String profileType;

    public RegisterUser(String username, String password, String confirmation, String email
    , String address, String mobile, String postal, String landLine, String secondAddress, String profileType){
        this.username = username;
        this.password = password;
        this.confirmation = confirmation;
        this.email = email;
        this.address = address;
        this.mobile = mobile;
        this.postal = postal;
        this.secondAddress = secondAddress;
        this.landLine = landLine;
        this.profileType = profileType;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal() {
        return postal;
    }

    public void setPostal(String postal) {
        this.postal = postal;
    }

    public String getLandLine() {
        return landLine;
    }

    public void setLandLine(String landLine) {
        this.landLine = landLine;
    }

    public String getSecondAddress() {
        return secondAddress;
    }

    public void setSecondAddress(String secondAddress) {
        this.secondAddress = secondAddress;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }
}
