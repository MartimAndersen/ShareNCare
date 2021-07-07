package pt.unl.fct.di.example.sharencare.institution.login;

import java.util.List;

public class InstitutionInfo {

    public String username;
    public String nif;
    public String email;
    public String mobile;
    public String landLine;
    public String address;
    public String zipCode;
    public List<String> events;
    public byte[] profilePic;
    public String website;
    public String instagram;
    public String twitter;
    public String facebook;
    public String youtube;
    public String fax;
    public String tokenId;

    public InstitutionInfo(String username, String nif, String email, String mobile, String landLine, String address, String zipCode, List<String> events, byte[] profilePic, String website, String instagram, String twitter, String facebook, String youtube, String fax, String tokenId) {
        this.username = username;
        this.nif = nif;
        this.email = email;
        this.mobile = mobile;
        this.landLine = landLine;
        this.address = address;
        this.zipCode = zipCode;
        this.tokenId = tokenId;
        this.events = events;
        this.profilePic = profilePic;
        this.website = website;
        this.instagram = instagram;
        this.twitter = twitter;
        this.facebook = facebook;
        this.youtube = youtube;
        this.fax = fax;
        this.tokenId = tokenId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

}
