package pt.unl.fct.di.apdc.sharencare.util;

public class ChangeRoleData {

	public String userToChange;
	public String roleToChange;
	public String tokenId;
	
	public ChangeRoleData() {
		
	}
	
	public ChangeRoleData(String userToChange, String roleToChange, String tokenId) {
		this.userToChange = userToChange;
		this.roleToChange = roleToChange;
		this.tokenId = tokenId;
	}
}
