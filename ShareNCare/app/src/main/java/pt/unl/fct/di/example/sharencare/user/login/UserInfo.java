package pt.unl.fct.di.example.sharencare.user.login;

import android.graphics.Bitmap;

import java.util.List;

import okhttp3.Cookie;

public class UserInfo {

    public String address;
    public String bio;
    public String email;
    public List<String> events;
    public String landLine;
    public String mobile;
    public List<String> myTracks;
    public PointsData points;
    public String profileType;
    public String secondAddress;
    public List<Integer> tags;
    public String username;
    public String zipCode;
    public List<String> token;
    public Bitmap profilePic;

    public UserInfo(String address, String bio, String email, List<String> events, String landLine, String mobile, List<String> myTracks, PointsData points, String profileType, String secondAddress, List<Integer> tags, List<String> token, String username, String zipCode) {
        this.address = address;
        this.bio = bio;
        this.email = email;
        this.events = events;
        this.landLine = landLine;
        this.mobile = mobile;
        this.myTracks  = myTracks;
        this.points = points;
        this.profileType = profileType;
        this.secondAddress = secondAddress;
        this.tags = tags;
        this.username = username;
        this.token = token;
        this.zipCode = zipCode;
        this.profileType = null;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public String getLandLine() {
        return landLine;
    }

    public void setLandLine(String landLine) {
        this.landLine = landLine;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getSecondAddress() {
        return secondAddress;
    }

    public void setSecondAddress(String secondAddress) {
        this.secondAddress = secondAddress;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public List<String> getToken() {
        return token;
    }

    public void setToken(List<String> token) {
        this.token = token;
    }

    public Bitmap getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(Bitmap profilePic) {
        this.profilePic = profilePic;
    }

    public List<String> getMyTracks() {
        return myTracks;
    }

    public void setMyTracks(List<String> myTracks) {
        this.myTracks = myTracks;
    }

    public PointsData getPoints() {
        return points;
    }

    public void setPoints(PointsData points) {
        this.points = points;
    }
}
