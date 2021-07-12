package pt.unl.fct.di.apdc.sharencare.util;

import java.util.UUID;

import pt.unl.fct.di.apdc.sharencare.util.AuthToken;

public class AuthToken {

	public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2;

	public long creationData;
	public boolean expirable;
	public long expirationData;
	public String role;
	public String tokenID;
	public String username;
	public String verifier;

	public AuthToken() {

	}

	public AuthToken(String username, String role, boolean expirable) {
		this.username = username;
		this.role = role;
		this.expirable = expirable;
		this.tokenID = UUID.randomUUID().toString();
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
		this.verifier = username.concat(role);
	}

}
