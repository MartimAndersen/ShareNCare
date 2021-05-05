package pt.unl.fct.di.apdc.sharencare.util;

public class UpdatePassword {
	
	public String username;
	public String tokenId;
	public String passwordOld;
	public String password;
	public String passwordCom;
	
	public UpdatePassword() {
		
	}
	
	public UpdatePassword(String username, String tokenId,String passwordOld, String password, String passwordCom) {
		this.username = username;
		this.tokenId = tokenId;
		this.password = password;
		this.passwordCom = password;
	}

}
