package pt.unl.fct.di.apdc.sharencare.util;

public class ChangeStateData {

	public String userToChange;
	public String state;
	public String tokenId;
	
	public ChangeStateData() {
		
	}
	
	public ChangeStateData(String userToChange, String state, String tokenId) {
		this.userToChange = userToChange;
		this.state = state;
		this.tokenId = tokenId;
	}
	
	
}
