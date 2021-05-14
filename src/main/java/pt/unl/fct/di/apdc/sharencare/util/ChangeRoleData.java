package pt.unl.fct.di.apdc.sharencare.util;

public class ChangeRoleData {

	public String userToBeChanged;
	public String roleToChange;
	public String tokenIdChangeRole;
	
	public ChangeRoleData() {
		
	}
	
	public ChangeRoleData(String userToBeChanged, String roleToChange, String tokenId) {
		this.userToBeChanged = userToBeChanged;
		this.roleToChange = roleToChange;
		this.tokenIdChangeRole = tokenIdChangeRole;
	}
}
