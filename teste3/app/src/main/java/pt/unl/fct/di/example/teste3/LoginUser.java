package pt.unl.fct.di.example.teste3;

import com.google.gson.annotations.SerializedName;

public class LoginUser {

    @SerializedName("username")
    private String username;
    private String password;

    public LoginUser(String username, String password){
        this.username = username;
        this.password = password;
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
}
