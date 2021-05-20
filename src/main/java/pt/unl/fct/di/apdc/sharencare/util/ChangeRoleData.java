package pt.unl.fct.di.apdc.sharencare.util;

public class ChangeRoleData {

	public String userToBeChanged;
	public String roleToChange;
	public String tokenIdChangeRole;
	
	public ChangeRoleData() {
		
	}
	
	public ChangeRoleData(String userToBeChanged, String roleToChange, String tokenIdChangeRole) {
		this.userToBeChanged = userToBeChanged;
		this.roleToChange = roleToChange;
		this.tokenIdChangeRole = tokenIdChangeRole;
	}

	public boolean emptyParameters() {
		return userToBeChanged.equals("") || roleToChange.equals("") || tokenIdChangeRole.equals("");
	}
}
