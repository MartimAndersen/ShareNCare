package pt.unl.fct.di.example.sharencare.institution.main_menu.ui.profile;

import java.util.List;

public class ProfileInstitution {

    public String email;
    public String mobile;
    public String landLine;
    public String address;
    public String zipCode;
    public String tokenId;
    public List<String> events;
    public byte[] profilePic;
    public String website;
    public String instagram;
    public String twitter;
    public String facebook;
    public String youtube;
    public String fax;

    public ProfileInstitution(String email, String mobile, String landLine, String address, String zipCode, byte[] profilePic,
                                  List<String> events, String tokenId, String website, String instagram, String twitter, String facebook, String youtube,
                                  String fax) {
        this.email = email;
        this.mobile = mobile;
        this.address = address;
        this.zipCode = zipCode;
        this.landLine = landLine;
        this.profilePic = profilePic;
        this.events = events;
        this.website = website;
        this.instagram = instagram;
        this.twitter = twitter;
        this.facebook = facebook;
        this.youtube = youtube;
        this.fax = fax;
        this.tokenId = tokenId;
    }

}
