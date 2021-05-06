package pt.unl.fct.di.apdc.sharencare.util;

public class LogoutUserData {
	
	public String userToLogout;
	public String tokenId;
	
	public LogoutUserData() {
		
	}
	
	public LogoutUserData(String userToLogout, String tokenId) {
		this.userToLogout = userToLogout;
		this.tokenId = tokenId;
	}

}
