package pt.unl.fct.di.apdc.sharencare.util;

public class ChangePropertyData {
	
	public String newEmail;
	public String newProfileType;
	public String newLandLine;
	public String newMobile;
	public String newAddress;
	public String newSecondAddress;
	public String newPostal;
	public byte[] profilePic;

	public ChangePropertyData() {
		
	}
	
	public ChangePropertyData(String newEmail, String newProfileType, String newLandLine, String newMobile, String newAddress, String newSecondAddress, String newPostal, byte[] profilePic) {
		this.newEmail = newEmail;
		this.newProfileType = newProfileType;
		this.newLandLine = newLandLine;
		this.newMobile = newMobile;
		this.newAddress = newAddress;
		this.newSecondAddress = newSecondAddress;
		this.newPostal = newPostal;
		this.profilePic = profilePic;
	}


	public boolean validEmail() {
		String[] splitEmail = newEmail.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (newEmail.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}

	public boolean validPostalCode() {
		boolean isValid = false;
		if(newPostal.contains("-")) {
			String[] splitPostal = newPostal.split("-");
			isValid = (newPostal.equals("") || (splitPostal[0].length() == 4 && splitPostal[1].length() == 3));
		}
		return isValid;
	}

	public boolean validPhone() {
		return (newMobile.equals("") || newMobile.length() == 9 || newMobile.length() == 13 || newMobile.length() == 14);
	}

	public boolean allEmptyParameters() {
		return newEmail.equals("") && newLandLine.equals("") && newMobile.equals("")
				&& newAddress.equals("") && newSecondAddress.equals("") && newPostal.equals("") && newProfileType.equals("");
	}
}
