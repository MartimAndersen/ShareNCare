package pt.unl.fct.di.apdc.sharencare.util;

public class RemoveUserData{
	  public String userToDelete;
	  public String tokenId;
	    
	  public RemoveUserData() {}

	  public RemoveUserData(String userToDelete, String tokenId) {
	    this.userToDelete = userToDelete;     
	    this.tokenId= tokenId;
	  }
	}