package pt.unl.fct.di.apdc.sharencare.util;

public class ChangeEmailData {

	public String oldEmail;
	public String newEmail;
	public String password;
	
	public ChangeEmailData() {
	
	}

	public ChangeEmailData(String oldEmail, String newEmail, String password) {
		this.oldEmail = oldEmail;
		this.newEmail = newEmail;
		this.password = password;
	}
	
	public boolean validEmail() {
		String[] splitEmail = newEmail.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (newEmail.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}
	
    
    public boolean cantBeSameEmail() {
    	return oldEmail.equals(newEmail);
    }
    
    public boolean emptyParameters() {
		return oldEmail.equals("") || newEmail.equals("") || password.equals("");
    }


}
