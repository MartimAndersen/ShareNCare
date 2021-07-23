package pt.unl.fct.di.apdc.sharencare.util;

import java.util.List;

import com.google.cloud.datastore.Entity;
import com.google.gson.Gson;

public class ChangePropertyData {

	public String address;
	public String bio;
	public String email;
	public List<String> events;
	public String landLine;
	public String mobile;
	public String profilePic;
	public String profileType;
	public String secondAddress;
	public List<Integer> tags;
	public String zipCode;
	
	private final Gson g = new Gson();

	public ChangePropertyData() {

	}

	public ChangePropertyData(String address, String bio, String email, List<String> events, String landLine, String mobile,
			String profilePic, String profileType, String secondAddress, List<Integer> tags, String zipCode) {
		this.email = email;
		this.mobile = mobile;
		this.profilePic = profilePic;
		this.address = address;
		this.zipCode = zipCode;
		this.profileType = profileType;
		this.secondAddress = secondAddress;
		this.landLine = landLine;
		this.tags = tags;
		this.events = events;
		this.bio = bio;
	}
	
	public boolean validEmail() {
		String[] splitEmail = email.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (email.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}

	public boolean validPhones() {
		return mobile.length() <= 15 || landLine.length() <= 15;
	}
	
	public boolean validPhone() {
		return (mobile.equals("") || mobile.length() == 9 || mobile.length() == 13 || mobile.length() == 14);
	}

	public boolean validZipCode() {
		return (zipCode.equals("") || zipCode.matches("\\d{4}(-\\d{3})?"));
	}

	public boolean validProfileType() {
		return profileType.equals("public") || profileType.equals("private");
	}
	
	public boolean noChange(Entity user) {
		return user.getString("address").equals(address) &&
				user.getString("bio").equals(bio) &&
				user.getString("email").equals(email) &&
				user.getString("landLine").equals(landLine) &&
				user.getString("mobile").equals(mobile) &&
				user.getString("profileType").equals(profileType) &&
				user.getString("secondAddress").equals(secondAddress) &&
				user.getString("tags").equals(g.toJson(tags)) &&
				user.getString("zipCode").equals(zipCode);
	}

}
