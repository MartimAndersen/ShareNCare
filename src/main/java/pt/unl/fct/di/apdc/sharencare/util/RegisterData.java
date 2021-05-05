package pt.unl.fct.di.apdc.sharencare.util;

import java.util.regex.Pattern;

public class RegisterData {
	
	public String username;
	public String email;
	public String password;
	public String passwordCom;
	public String role;
	public String state;
	public String perfil;
	public String phone1;
	public String phone2;
	public String morada;
	public String codigoPostal;
	

	
	public RegisterData() {
	}
	
	public RegisterData(String username, String email, String password, String passwordCom, String role, String state,
			String perfil, String phone1, String phone2, String morada, String codigoPostal) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.passwordCom = passwordCom;
		this.role = role;			
		this.state = state;
		this.perfil = perfil;
		this.phone1 = phone1;
		this.phone2 = phone2;
		this.morada = morada;
		this.codigoPostal = codigoPostal;
	}
	
	//Funtion to validate info
	public boolean validRegistration() {
		boolean check = false;
		boolean emailBol = false;
		boolean passBol = false;
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
	            "[a-zA-Z0-9_+&*-]+)*@" +
	            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
	            "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if(email != null) {
			emailBol = pat.matcher(email).matches();
		}
		if(password != null) {
			passBol = password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])[a-zA-Z0-9]+$");
		}
		if(emailBol == true && passBol == true) {
			check = true;
		}
		return check;
	}


}