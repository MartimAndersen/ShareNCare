package pt.unl.fct.di.example.sharencare.user.register;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RegisterUser {

    @SerializedName("username")
    private String username;
    private String password;
    private String confirmation;
    private String email;

    public RegisterUser(String username, String password, String confirmation, String email){
        this.username = username;
        this.password = password;
        this.confirmation = confirmation;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String confirmation) {
        this.confirmation = confirmation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
