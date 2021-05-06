package pt.unl.fct.di.apdc.sharencare.util;

public class RegisterData {

	public String username;
	public String email;
	public String password;
	public String confirmation;
	public String profileType;
	public String landLine;
	public String mobile;
	public String adress;
	public String secondAdress;
	public String postal;
	public String role;
	public String state;
	public boolean extraData;

	public RegisterData() {

	}

	public RegisterData(String username, String email, String password, String confirmation, String profileType,
			String landLine, String mobile, String adress, String secondAdress, String postal, String role,
			String state, String tokenId) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.profileType = profileType;
		this.landLine = landLine;
		this.mobile = mobile;
		this.adress = adress;
		this.secondAdress = secondAdress;
		this.postal = postal;
		this.role = role;
		this.state = state;
	}

}
