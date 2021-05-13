package pt.unl.fct.di.apdc.sharencare.util;

public class RegisterData {

	public String username;
	public String email;
	public String password;
	public String confirmation;
	public String mobile;
	public String adress;
	public String postal;
	public String role;
	public String state;
	public boolean extraData;

	public RegisterData() {

	}

	public RegisterData(String username, String email, String password, String confirmation,
			String mobile, String adress, String secondAdress, String postal, String role,
			String state, String tokenId) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.mobile = mobile;
		this.adress = adress;
		this.postal = postal;
		this.role = role;
		this.state = state;
	}

}
