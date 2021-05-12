package pt.unl.fct.di.apdc.sharencare.util;

import java.io.Serializable;

public class Credentials implements Serializable{
	
	private String username;
	private String password;
	
	public Credentials() {
		
	}
	
	public Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}

}
