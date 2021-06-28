package pt.unl.fct.di.apdc.sharencare.util;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.datastore.Blob;
import com.google.gson.Gson;

public class ProfileData {

	public String[] TAGS =  {"animals", "environment", "children", "elderly", "supplies", "homeless"};//, sports, summer, holidays, turism};
	
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
	public Blob profilePic;
	
	private Gson gson;
	
	public ProfileData() {
		
	}
	
	public ProfileData(String email, String mobile, String landLine, String address, String postal, String profileType, String secondAddress, List<Integer> tags, //Blob profilePic, 
			List<String> events, String tokenId) {
		gson = new Gson();
		
		this.email = email;
		this.mobile = mobile;
		this.address = address;
		this.zipCode = postal;
		this.profileType = profileType;
		this.secondAddress = secondAddress;
		this.landLine = landLine;
		this.tags = tags;
		//this.profilePic = profilePic;
		this.events = events;
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

//	public boolean validPhone() {
//		String[] splitMobile = mobile.split(" ");
//		return (mobile.equals("")
//				|| (splitMobile[0].subSequence(0, 1).equals("+") && (splitMobile[1].substring(0, 2).equals("91")
//				|| splitMobile[1].substring(0, 2).equals("93") || splitMobile[1].substring(0, 2).equals("96"))
//				&& splitMobile[1].length() == 9));
//	}
	public boolean validPhone() {
		return (mobile.equals("") || mobile.length() == 9 || mobile.length() == 13 || mobile.length() == 14);
	}
	
	public boolean allEmptyParameters() {
		return// newEmail.equals("") && 
				landLine.equals("") && mobile.equals("")
				&& address.equals("") && secondAddress.equals("") && zipCode.equals("");
	}



}
