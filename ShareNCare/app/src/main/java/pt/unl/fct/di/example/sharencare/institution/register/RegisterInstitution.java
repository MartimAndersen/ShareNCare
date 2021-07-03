package pt.unl.fct.di.example.sharencare.institution.register;

import com.google.gson.annotations.SerializedName;

public class RegisterInstitution {

    public String username;
    @SerializedName("nif")
    public String nif;
    public String email;
    public String password;
    public String confirmation;

    public RegisterInstitution(String username, String nif, String email, String password, String confirmation) {
        this.username = username;
        this.nif = nif;
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

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
}
