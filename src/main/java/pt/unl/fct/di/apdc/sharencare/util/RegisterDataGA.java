package pt.unl.fct.di.apdc.sharencare.util;

public class RegisterDataGA {
		public String username;
		public String email;
		public String password;
		public String confirmation;
		public String role;
		public String state;

		public RegisterDataGA() {

		}

		public RegisterDataGA(String username, String email, String password, String confirmation) {
			this.username = username;
			this.password = password;
			this.confirmation = confirmation;
			this.email = email;
			this.role = "GA";
			this.state = "ENABLED";
		}

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
