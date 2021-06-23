package pt.unl.fct.di.apdc.sharencare.util;

import java.util.List;
import com.google.cloud.datastore.Value;

public class RegisterData {

	public String username;
	public String email;
	public String password;
	public String confirmation;
	public String role;
	public String state;
	public List<Value<String>> tags;

	public RegisterData() {

	}

	public RegisterData(String username, String email, String password, String confirmation, List<Value<String>> tags) {
		this.username = username;
		this.password = password;
		this.confirmation = confirmation;
		this.email = email;
		this.tags = tags;
		this.role = "USER";
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
		return (password.length()>=5);
	}
	public boolean validPasswordConfirmation() {
		return (password.equals(confirmation));
	}

}
