package pt.unl.fct.di.example.sharencare.user.login;

import com.google.gson.annotations.SerializedName;

public class LoginUser {

    @SerializedName("usernameLogin")
    private String usernameLogin;
    private String passwordLogin;
    private boolean expirable;

    public LoginUser(String usernameLogin, String passwordLogin, boolean expirable){
        this.usernameLogin = usernameLogin;
        this.passwordLogin = passwordLogin;
        this.expirable = expirable;
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
