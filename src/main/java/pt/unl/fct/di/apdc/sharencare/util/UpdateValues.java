package pt.unl.fct.di.apdc.sharencare.util;

import java.util.regex.Pattern;

public class UpdateValues {
	
	public String username;
	public String tokenId;
	public String email;
	public String perfil;
	public String phone1;
	public String phone2;
	public String morada;
	public String codigoPostal;
	

	
	public UpdateValues() {
	}
	
	public UpdateValues(String email, String tokenId, String perfil, 
			String phone1, String phone2, String morada, String codigoPostal) {
		this.email = email;
		this.perfil = perfil;
		this.phone1 = phone1;
		this.phone2 = phone2;
		this.morada = morada;
		this.codigoPostal = codigoPostal;
	}
	
	public boolean emailVerification() {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
	            "[a-zA-Z0-9_+&*-]+)*@" +
	            "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
	            "A-Z]{2,7}$";
		Pattern pat = Pattern.compile(emailRegex);
		if(email != null) {
			return pat.matcher(email).matches();
		}else {
			return false;
		}
	}
	
	public boolean perfilVerification() {
		if(perfil.equals("Publico") || perfil.equals("Privado")) {
			return true;
		}else {
			return false;
		}
	}
	
	public boolean phone1Verification() {
		String regexStr = "^[0-9]{9}$";
		Pattern pat = Pattern.compile(regexStr);
		if(phone1 != null) {
			return pat.matcher(phone1).matches();
		}else {
			return false;
		}
	}
	
	public boolean phone2Verification() {
		String regexStr = "^[0-9]{9}$";
		Pattern pat = Pattern.compile(regexStr);
		if(phone2 != null) {
			return pat.matcher(phone2).matches();
		}else {
			return false;
		}
	}
	
	public boolean codigoPostalVerification() {
		if(codigoPostal.matches("((?<=\\s|^)\\d{4}(?=\\s|$|-\\d{4}(?=\\s|$)))(?:-\\d{3}(?=\\s|$))?")) {
			return true;
		}else {
			return false;
		}
	}
}
