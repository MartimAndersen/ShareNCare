package pt.unl.fct.di.apdc.sharencare.util;

public class ChangePropertyData {
	
	public String newEmail;
	public String newProfileType;
	public String newLandLine;
	public String newMobile;
	public String newAddress;
	public String newSecondAddress;
	public String newPostal;
	public String tokenIdChangeAttributes;

	public ChangePropertyData() {
		
	}
	
	public ChangePropertyData(String newEmail, String newProfileType, String newLandLine, String newMobile, String newAddress, String newSecondAddress, String newPostal, String tokenIdChangeAttributes) {
		this.newEmail = newEmail;
		this.newProfileType = newProfileType;
		this.newLandLine = newLandLine;
		this.newMobile = newMobile;
		this.newAddress = newAddress;
		this.newSecondAddress = newSecondAddress;
		this.newPostal = newPostal;
		this.tokenIdChangeAttributes = tokenIdChangeAttributes;
	}


	public boolean validEmail() {
		String[] splitEmail = newEmail.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (newEmail.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}

	public boolean validPostalCode() {
		String[] splitPostal = newPostal.split("-");
		return (newPostal.equals("") || (splitPostal[0].length() == 4 && splitPostal[1].length() == 3));
	}

	public boolean validPhone() {
		return (newMobile.equals("") || newMobile.length() == 9 || newMobile.length() == 13 || newMobile.length() == 14);
	}
}