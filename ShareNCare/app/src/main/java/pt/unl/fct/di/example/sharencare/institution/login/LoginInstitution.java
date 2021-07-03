package pt.unl.fct.di.example.sharencare.institution.login;

import com.google.gson.annotations.SerializedName;

public class LoginInstitution {

    @SerializedName("nifLogin")
    private String nifLogin;
    private String passwordLogin;

    public LoginInstitution(String nifLogin, String passwordLogin){
        this.nifLogin = nifLogin;
        this.passwordLogin = passwordLogin;
    }

    public String getNifLogin() {
        return nifLogin;
    }

    public void setNifLogin(String nifLogin) {
        this.nifLogin = nifLogin;
    }

    public String getPasswordLogin() {
        return passwordLogin;
    }

    public void setPasswordLogin(String passwordLogin) {
        this.passwordLogin = passwordLogin;
    }

}
