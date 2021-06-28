package pt.unl.fct.di.apdc.sharencare.util;

public class ChangeRoleData {

	public String userToBeChanged;
	public String roleToChange;
	
	public ChangeRoleData() {
		
	}
	
	public ChangeRoleData(String userToBeChanged, String roleToChange) {
		this.userToBeChanged = userToBeChanged;
		this.roleToChange = roleToChange;
	}

	public boolean emptyParameters() {
		return userToBeChanged.equals("") || roleToChange.equals("");
	}
}
