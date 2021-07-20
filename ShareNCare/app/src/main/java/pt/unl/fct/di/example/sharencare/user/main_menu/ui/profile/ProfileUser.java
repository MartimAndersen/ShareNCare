package pt.unl.fct.di.example.sharencare.user.main_menu.ui.profile;

import java.util.List;

import okhttp3.Cookie;

public class ProfileUser {

    public String address;
    public String bio;
    public String email;
    public List<String> events;
    public String landLine;
    public String mobile;
    public byte[] profilePic;
    public String profileType;
    public String secondAddress;
    public List<Integer> tags;
    public String zipCode;

    public ProfileUser(String address, String bio, String email, List<String> events, String landLine, String mobile, byte[] profilePic, String profileType, String secondAddress, List<Integer> tags, String zipCode) {
        this.address = address;
        this.bio = bio;
        this.email = email;
        this.events = events;
        this.landLine = landLine;
        this.mobile = mobile;
        this.profilePic = profilePic;
        this.profileType = profileType;
        this.secondAddress = secondAddress;
        this.tags = tags;
        this.zipCode = zipCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSecondAddress() {
        return secondAddress;
    }

    public void setSecondAddress(String secondAddress) {
        this.secondAddress = secondAddress;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLandLine() {
        return landLine;
    }

    public void setLandLine(String landLine) {
        this.landLine = landLine;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }
}
