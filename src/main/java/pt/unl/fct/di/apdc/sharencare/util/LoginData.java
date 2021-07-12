package pt.unl.fct.di.apdc.sharencare.util;

public class LoginData {

	public String usernameLogin;
	public String passwordLogin;
	public boolean expirable;
	
	public LoginData() {
		
	}
	
	public LoginData(String usernameLogin, String passwordLogin, boolean expirable) {
		this.usernameLogin = usernameLogin;
		this.passwordLogin = passwordLogin;
		this.expirable = expirable;
	}

    public boolean emptyParameters() {
		return usernameLogin.equals("") || passwordLogin.equals("");
    }
    
}
