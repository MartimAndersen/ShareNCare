package pt.unl.fct.di.apdc.sharencare.util;

public class LoginData {

	public String usernameLogin;
	public String passwordLogin;
	
	public LoginData() {
		
	}
	
	public LoginData(String usernameLogin, String passwordLogin) {
		this.usernameLogin = usernameLogin;
		this.passwordLogin = passwordLogin;
	}

    public boolean emptyParameters() {
		return usernameLogin.equals("") || passwordLogin.equals("");
    }
}
