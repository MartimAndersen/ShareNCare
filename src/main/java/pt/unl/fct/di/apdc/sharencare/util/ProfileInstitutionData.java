package pt.unl.fct.di.apdc.sharencare.util;

import java.net.URL;
import java.util.List;

import com.google.gson.Gson;

public class ProfileInstitutionData {
	
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
	public String tokenId;
	public String twitter;
	public String website;
	public String youtube;
	public String zipCode;

	public ProfileInstitutionData() {
		
	}
	
	public ProfileInstitutionData(String address, String bio, String email, List<String> events, String facebook, String fax, String instagram, String landLine, String mobile, byte[] profilePic, String tokenId, String twitter, String website, String youtube, String zipCode) {
		this.email = email;
		this.mobile = mobile;
		this.profilePic = profilePic;
		this.address = address;
		this.zipCode = zipCode;
		this.landLine = landLine;
		this.events = events;
		this.website = website;
		this.instagram = instagram;
		this.twitter = twitter;
		this.facebook = facebook;
		this.youtube = youtube;
		this.fax = fax;
		this.tokenId = tokenId;
		this.bio = bio;
	}
	
	
	public boolean allEmptyParameters() {
		return  email.equals("") && mobile.equals("") && address.equals("") && profilePic.length == 0
				&& zipCode.equals("") && landLine.equals("") && website.equals("") && instagram.equals("")
				&& twitter.equals("") && facebook.equals("") && youtube.equals("") && fax.equals("") && bio.equals("");
	}

	
	public boolean validEmail() {
		String[] splitEmail = email.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (email.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}

	public boolean validFax() {
		return fax.matches("\"^\\+[0-9]{1,3}\\([0-9]{3}\\)[0-9]{7}$\"");
	}
	
	public boolean validPhone() {
		return (mobile.equals("") || mobile.length() == 9 || mobile.length() == 13 || mobile.length() == 14);
	}
	
	public boolean validPostalCode() {
		return (zipCode.equals("") || zipCode.matches("\\d{4}(-\\d{3})?"));
	}
	
	public boolean validWebsite()
    {
        /* Try creating a valid URL */
        try {
            new URL(website).toURI();
            return true;
        }
          
        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

}
