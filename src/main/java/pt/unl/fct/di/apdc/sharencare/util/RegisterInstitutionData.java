package pt.unl.fct.di.apdc.sharencare.util;

public class RegisterInstitutionData {

	public String username;
	public String nif;
	public String email;
	public String password;
	public String confirmation;
	public String role;
	public String state;

	public RegisterInstitutionData() {

	}

	public RegisterInstitutionData(String username, String nif, String email, String password, String confirmation) {
		this.username = username;
		this.nif = nif;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.role = "INSTITUTION";
		this.state = "ENABLED";
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

	public boolean emptyParameters() {
		return username.equals("") || email.equals("") || password.equals("") || confirmation.equals("");
	}

	public boolean validEmail() {
		String[] splitEmail = email.split("\\.");
		int emailSize = splitEmail.length - 1;
		return (email.contains("@") && (splitEmail[emailSize].length() == 2 || splitEmail[emailSize].length() == 3));
	}

	public boolean validPasswordLenght() {
		return (password.length() >= 5);
	}

	public boolean validPasswordConfirmation() {
		return (password.equals(confirmation));
	}

	public boolean validNif() {
		try {
			final int max = 9;
			if (!nif.matches("[0-9]+") || nif.length() != max)
				return false;
			int checkSum = 0;
			// calcula a soma de controlo
			for (int i = 0; i < max - 1; i++) {
				checkSum += (nif.charAt(i) - '0') * (max - i);
			}
			int checkDigit = 11 - (checkSum % 11);
			if (checkDigit >= 10)
				checkDigit = 0;
			return checkDigit == nif.charAt(max - 1) - '0';
		} catch (Exception e) {
			return false;
		} finally {
		}
	}

}
