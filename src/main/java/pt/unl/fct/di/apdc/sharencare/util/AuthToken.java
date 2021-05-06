package pt.unl.fct.di.apdc.sharencare.util;

import java.util.UUID;

import pt.unl.fct.di.apdc.sharencare.util.AuthToken;

public class AuthToken {

	public static final long EXPIRATION_TIME=1000*60*60*2;
	
	public String username;
	public String role;
	public String tokenID;
	public long creationData;
	public long expirationData;
	public String verifier;
	public boolean valid;
	
	public AuthToken() {
		
	}
	
	public AuthToken(String username, String role) {
		this.username = username;
		this.role = role;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData=System.currentTimeMillis();
		this.expirationData=this.creationData + AuthToken.EXPIRATION_TIME;
		this.verifier = username.concat(role);
		this.valid = true;
	}
	
}
