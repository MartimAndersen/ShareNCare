package pt.unl.fct.di.apdc.sharencare.util;

public class InsideLoginData {
	
	public String profileType;
	public String landLine;
	public String mobile;
	public String address;
	public String secondAddress;
	public String postal;

	public InsideLoginData() {

	}

	public InsideLoginData(String profileType, String landLine, String mobile, String address, String secondAddress, String postal) {

		this.profileType = profileType;
		this.landLine = landLine;
		this.mobile = mobile;
		this.address = address;
		this.secondAddress = secondAddress;
		this.postal = postal;
	}

}
