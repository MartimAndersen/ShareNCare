package pt.unl.fct.di.apdc.sharencare.util;

import java.net.URL;
import java.util.List;

import com.google.gson.Gson;

public class ProfileInstitutionData {
	
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

	public ProfileInstitutionData() {
		
	}
	
	public ProfileInstitutionData(String email, String mobile, String landLine, String address, String zipCode, byte[] profilePic, 
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
	
	
	public boolean validEmail() {
		String[] splitEmail = email.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (email.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}

	
	public boolean validPostalCode() {
		return (zipCode.equals("") || zipCode.matches("\\d{4}(-\\d{3})?"));
	}

	public boolean validPhone() {
		return (mobile.equals("") || mobile.length() == 9 || mobile.length() == 13 || mobile.length() == 14);
	}
	
	public boolean allEmptyParameters() {
		return  email.equals("") && mobile.equals("") && address.equals("")
				&& zipCode.equals("") && landLine.equals("") && profilePic.length == 0
				&& events == null && website.equals("") && instagram.equals("")
				&& twitter.equals("") && facebook.equals("") && youtube.equals("") && fax.equals("");
	}
	
	public boolean validFax() {
		return fax.matches("\"^\\+[0-9]{1,3}\\([0-9]{3}\\)[0-9]{7}$\"");
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
