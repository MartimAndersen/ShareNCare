package pt.unl.fct.di.apdc.sharencare.util;

public class ChangePasswordData {
	
	public String oldPassword;
	public String newPassword;
	public String confirmation;
	public String tokenId;
	
	public ChangePasswordData() {
		
	}
	
	public ChangePasswordData(String oldPassword, String newPassword, String confirmation, String tokenId) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.confirmation = confirmation;
		this.tokenId = tokenId;
	}

}
