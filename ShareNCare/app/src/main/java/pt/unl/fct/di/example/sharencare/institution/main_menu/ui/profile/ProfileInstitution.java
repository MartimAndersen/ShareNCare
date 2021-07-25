package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.profile;

import java.util.List;

public class ProfileInstitution {

    public String address;
    public String bio;
    public String email;
    public List<String> events;
    public String facebook;
    public String fax;
    public String instagram;
    public String landLine;
    public String mobile;
    public byte[] profilePic;
    public String twitter;
    public String website;
    public String youtube;
    public String zipCode;

    public ProfileInstitution(String address, String bio, String email, List<String> events, String facebook, String fax, String instagram, String landLine, String mobile, byte[] profilePic, String twitter, String website, String youtube, String zipCode) {
        this.address = address;
        this.bio = bio;
        this.email = email;
        this.events = events;
        this.facebook = facebook;
        this.fax = fax;
        this.instagram = instagram;
        this.landLine = landLine;
        this.mobile = mobile;
        this.profilePic = profilePic;
        this.twitter = twitter;
        this.website = website;
        this.youtube = youtube;
        this.zipCode = zipCode;
    }
}
