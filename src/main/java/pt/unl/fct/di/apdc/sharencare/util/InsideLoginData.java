package pt.unl.fct.di.apdc.sharencare.util;

public class InsideLoginData {
	
	public String profileType;
	public String landLine;
	public String mobile;
	public String adress;
	public String secondAdress;
	public String postal;

	public InsideLoginData() {

	}

	public InsideLoginData(String profileType, String landLine, String mobile, String adress, String secondAdress, String postal) {

		this.profileType = profileType;
		this.landLine = landLine;
		this.mobile = mobile;
		this.adress = adress;
		this.secondAdress = secondAdress;
		this.postal = postal;
	}

}
