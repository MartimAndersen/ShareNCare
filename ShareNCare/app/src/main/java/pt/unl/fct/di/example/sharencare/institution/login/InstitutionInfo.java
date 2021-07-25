package pt.unl.fct.di.example.sharencare.institution.login;

import java.util.List;

public class InstitutionInfo {

    public String address;
    public String bio;
    public String coordinates;
    public String email;
    public List<String> events;
    public String facebook;
    public String fax;
    public String instagram;
    public String landLine;
    public String mobile;
    public String nif;
    public String twitter;
    public String username;
    public String website;
    public String youtube;
    public String zipCode;
    public List<String> token;

    public InstitutionInfo(String address, String bio, String coordinates, String email, List<String> events, String facebook, String fax, String instagram, String landLine, String mobile, String nif,String twitter, String username, String website, String youtube, String zipCode, List<String> token) {
        this.address = address;
        this.bio = bio;
        this.coordinates = coordinates;
        this.email = email;
        this.events = events;
        this.facebook = facebook;
        this.fax = fax;
        this.instagram = instagram;
        this.landLine = landLine;
        this.mobile = mobile;
        this.nif = nif;
        this.twitter = twitter;
        this.username = username;
        this.website = website;
        this.youtube = youtube;
        this.zipCode = zipCode;
        this.token = token;
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

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
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

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
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
}
