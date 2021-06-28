package pt.unl.fct.di.example.sharencare.ui.profile;

import java.util.List;

public class ProfileUser {

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
    public String profilePic;

    public ProfileUser(String email, String mobile, String landLine, String address, String zipCode, String profileType, String secondAddress, List<Integer> tags,// String profilePic,
                       List<String> events,
                       String tokenId){
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
      //  this.profilePic = profilePic;
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
