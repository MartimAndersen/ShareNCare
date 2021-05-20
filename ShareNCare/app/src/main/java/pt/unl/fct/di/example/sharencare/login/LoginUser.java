package pt.unl.fct.di.example.sharencare.login;

import com.google.gson.annotations.SerializedName;

public class LoginUser {

    @SerializedName("usernameLogin")
    private String usernameLogin;
    private String passwordLogin;

    public LoginUser(String usernameLogin, String passwordLogin){
        this.usernameLogin = usernameLogin;
        this.passwordLogin = passwordLogin;
    }

    public String getUsername() {
        return usernameLogin;
    }

    public void setUsername(String usernameLogin) {
        this.usernameLogin = usernameLogin;
    }

    public String getPassword() {
        return passwordLogin;
    }

    public void setPassword(String passwordLogin) {
        this.passwordLogin = passwordLogin;
    }
}
