package pt.unl.fct.di.apdc.sharencare.util;

public class RegisterData {

	public String username;
	public String email;
	public String password;
	public String confirmation;
	public String mobile;
	public String address;
	public String postal;
	public String role;
	public String state;

	public RegisterData() {

	}

	public RegisterData(String username, String email, String password, String confirmation, String mobile, String address, String postal, String role,
			String state) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.mobile = mobile;
		this.address = address;
		this.postal = postal;
		this.role = role;
		this.state = state;
	}

	public boolean validateData(RegisterData data) {

		String[] email = data.email.split("\\.");
		String[] mobile = data.mobile.split(" ");
		String[] postal = data.postal.split("-");

		int emailSize = email.length - 1;

		if (data.email.contains("@") && (email[emailSize].length() == 2 || email[emailSize].length() == 3))
			if (data.password.equals(data.confirmation))
				if (data.postal.equals("") || (postal[0].length() == 4 && postal[1].length() == 3))
					if (data.mobile.equals("")
							|| (mobile[0].subSequence(0, 1).equals("+") && (mobile[1].substring(0, 2).equals("91")
							|| mobile[1].substring(0, 2).equals("93") || mobile[1].substring(0, 2).equals("96"))
							&& mobile[1].length() == 9))
						return true;
		return false;
	}

}
