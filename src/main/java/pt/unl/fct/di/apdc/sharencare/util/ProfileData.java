package pt.unl.fct.di.apdc.sharencare.util;

public class ProfileData {

	public String email;
	public String mobile;
	public String landLine;
	public String address;
	public String secondAddress;
	public String postal;
	public boolean profileType;
	public String tokenId;
	
	public ProfileData() {
		
	}
	
	public ProfileData(String email, String mobile, String landLine, String address, String postal, boolean profileType, String secondAddress, String tokenId) {
		this.email = email;
		this.mobile = mobile;
		this.address = address;
		this.postal = postal;
		this.profileType = profileType;
		this.secondAddress = secondAddress;
		this.landLine = landLine;
		this.tokenId = tokenId;
	}

	public boolean validEmail() {
		String[] splitEmail = email.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (email.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}

	
	public boolean validPostalCode() {
		String[] splitPostal = postal.split("-");
		return (postal.equals("") || (splitPostal[0].length() == 4 && splitPostal[1].length() == 3));
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
				&& address.equals("") && secondAddress.equals("") && postal.equals("");
	}



}
