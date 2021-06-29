package pt.unl.fct.di.apdc.sharencare.util;

public class LoginInstitutionData {
	
	public String nifLogin;
	public String passwordLogin;
	
	public LoginInstitutionData() {
			
	}
		
	public LoginInstitutionData(String nifLogin, String passwordLogin) {
		this.nifLogin = nifLogin;
		this.passwordLogin = passwordLogin;
	}

	public boolean emptyParameters() {
		return nifLogin.equals("") || passwordLogin.equals("");
	}
}

