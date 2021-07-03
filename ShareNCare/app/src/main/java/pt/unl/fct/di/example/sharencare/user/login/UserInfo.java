package pt.unl.fct.di.example.sharencare.user.login;

import java.util.List;

public class UserInfo {

    public String username;
    public String email;
    public String mobile;
    public String landLine;
    public String address;
    public String secondAddress;
    public String zipCode;
    public String profileType;
    public String tokenId;
    public List<Integer> tags;
    public List<String> events;
    public byte[] profilePic;

    public UserInfo(String username, String email, String mobile, String landLine, String address, String zipCode, String profileType, String secondAddress, List<Integer> tags, byte[] profilePic, List<String> events, String tokenId){
        this.username = username;
        this.email = email;
        this.address = address;
        this.secondAddress = secondAddress;
        this.mobile = mobile;
        this.landLine = landLine;
        this.zipCode = zipCode;
        this.tags = tags;
        this.profileType = profileType;
        this.events = events;
        this.tokenId = tokenId;
        this.profilePic = profilePic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getProfileType() {
        return profileType;
    }

    public void setProfileType(String profileType) {
        this.profileType = profileType;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    public List<String> getEvents() {
        return events;
    }

    public void setEvents(List<String> events) {
        this.events = events;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }
}
