package pt.unl.fct.di.apdc.sharencare.util;

public class UpdateRole{
	  
	  public String username;
	  public String tokenId;
	  public String role;
	    
	  public UpdateRole() {}

	  public UpdateRole(String username, String tokenId, String role) {
	    this.username= username;
	    this.tokenId= tokenId;
	    this.role = role;
	  }
} 
