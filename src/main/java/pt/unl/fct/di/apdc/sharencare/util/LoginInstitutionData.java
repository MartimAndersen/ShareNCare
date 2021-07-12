package pt.unl.fct.di.apdc.sharencare.util;

public class LoginInstitutionData {
	
	public String nifLogin;
	public String passwordLogin;
	public boolean expirable;
	
	public LoginInstitutionData() {
			
	}
		
	public LoginInstitutionData(String nifLogin, String passwordLogin, boolean expirable) {
		this.nifLogin = nifLogin;
		this.passwordLogin = passwordLogin;
		this.expirable = expirable;
	}

	public boolean emptyParameters() {
		return nifLogin.equals("") || passwordLogin.equals("");
	}
}

