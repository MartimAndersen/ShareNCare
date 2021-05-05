package pt.unl.fct.di.apdc.sharencare.util;

public class UpdateState{
	  
	  public String username;
	  public String tokenId;
	  public String state;
	    
	  public UpdateState() {}

	  public UpdateState(String username, String tokenId, String state) {
	    this.username= username;
	    this.tokenId= tokenId;
	    this.state = state;
	  }
} 
