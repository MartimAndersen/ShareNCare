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

//	public boolean validateData(RegisterData data) {
//
//		String[] email = data.email.split("\\.");
//		String[] mobile = data.mobile.split(" ");
//		String[] postal = data.postal.split("-");
//
//		int emailSize = email.length - 1;
//
//		if (data.email.contains("@") && (email[emailSize].length() == 2 || email[emailSize].length() == 3))
//			if (data.password.equals(data.confirmation))
//				if (data.postal.equals("") || (postal[0].length() == 4 && postal[1].length() == 3))
//					if (data.mobile.equals("")
//							|| (mobile[0].subSequence(0, 1).equals("+") && (mobile[1].substring(0, 2).equals("91")
//							|| mobile[1].substring(0, 2).equals("93") || mobile[1].substring(0, 2).equals("96"))
//							&& mobile[1].length() == 9))
//						return true;
//		return false;
//	}

	public boolean validEmail() {
		String[] splitEmail = email.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (email.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}

	public boolean validPassword() {
		return (password.length()>=5 && password.equals(confirmation));
	}

	public boolean validPostalCode() {
		String[] splitPostal = postal.split("-");
		return (postal.equals("") || (splitPostal[0].length() == 4 && splitPostal[1].length() == 3));
	}

//	public boolean validPhone() {
//		String[] splitMobile = mobile.split(" ");
//		return (mobile.equals("")
//				|| (splitMobile[0].subSequence(0, 1).equals("+") && (splitMobile[1].substring(0, 2).equals("91")
//				|| splitMobile[1].substring(0, 2).equals("93") || splitMobile[1].substring(0, 2).equals("96"))
//				&& splitMobile[1].length() == 9));
//	}
public boolean validPhone() {
	return (mobile.equals("") || mobile.length() == 9 || mobile.length() == 13 || mobile.length() == 14);
}
}
