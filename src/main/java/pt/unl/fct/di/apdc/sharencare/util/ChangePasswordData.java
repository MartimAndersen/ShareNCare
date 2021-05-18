package pt.unl.fct.di.apdc.sharencare.util;

public class ChangePasswordData {
	
	public String oldPassword;
	public String newPassword;
	public String confirmation;
	public String tokenIdChangePassword;
	
	public ChangePasswordData() {
		
	}
	
	public ChangePasswordData(String oldPassword, String newPassword, String confirmation, String tokenIdChangePassword) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.confirmation = confirmation;
		this.tokenIdChangePassword = tokenIdChangePassword;
	}

    public boolean emptyParameters() {
		return oldPassword.equals("") || newPassword.equals("") || confirmation.equals("") || tokenIdChangePassword.equals("");
    }

    public boolean validPasswordLenght() {
		return (newPassword.length()>=5);
    }
}
