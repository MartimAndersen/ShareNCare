package pt.unl.fct.di.apdc.sharencare.util;

public class ChangePropertyData {
	
	public String email;
	public String profileType;
	public String landLine;
	public String mobile;
	public String adress;
	public String secondAdress;
	public String postal;
	public String tokenId;

	public ChangePropertyData() {
		
	}
	
	public ChangePropertyData(String email, String profileType, String landLine, String mobile, String adress, String secondAdress, String postal, String tokenId) {
		this.email = email;
		this.profileType = profileType;
		this.landLine = landLine;
		this.mobile = mobile;
		this.adress = adress;
		this.secondAdress = secondAdress;
		this.postal = postal;
		this.tokenId = tokenId;
	}
}
