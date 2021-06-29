package pt.unl.fct.di.apdc.sharencare.util;

import java.util.List;

import com.google.gson.Gson;

public class ProfileInstitutionData {
	
	public String username;
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
	public List<String> members;

	public ProfileInstitutionData() {
		
	}
	
	public ProfileInstitutionData(String username, String email, String mobile, String landLine, String address, String zipCode, byte[] profilePic, 
			List<String> events, String tokenId, String website, String instagram, String twitter, String facebook, String youtube,
			String fax, List<String> members) {
		this.username = username;
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
		this.members = members;
		this.tokenId = tokenId;
	}
	
	
	public boolean validEmail() {
		String[] splitEmail = email.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (email.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}

	
	public boolean validPostalCode() {
		String[] splitPostal = zipCode.split("-");
		return (zipCode.equals("") || (splitPostal[0].length() == 4 && splitPostal[1].length() == 3));
	}

	public boolean validPhone() {
		return (mobile.equals("") || mobile.length() == 9 || mobile.length() == 13 || mobile.length() == 14);
	}
	
	public boolean allEmptyParameters() {
		return  email.equals("") && profilePic.length == 0 &&
				landLine.equals("") && mobile.equals("")
				&& address.equals("") && zipCode.equals("");
	}

}
