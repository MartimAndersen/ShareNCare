package pt.unl.fct.di.apdc.sharencare.util;

public class ChangePasswordData {

	public String oldPassword;
	public String newPassword;
	public String confirmation;

	public ChangePasswordData(){

	}
	
	public ChangePasswordData(String oldPassword, String newPassword, String confirmation) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
		this.confirmation = confirmation;
	}

    public boolean emptyParameters() {
		return oldPassword.equals("") || newPassword.equals("") || confirmation.equals("");
    }

    public boolean validPasswordLength() {
		return (newPassword.length()>=5);
    }
    
    public boolean cantBeSamePassword() {
    	return oldPassword.equals(newPassword)
;    }

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}
}
