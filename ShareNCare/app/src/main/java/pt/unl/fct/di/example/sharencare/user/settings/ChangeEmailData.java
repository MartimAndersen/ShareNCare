package pt.unl.fct.di.example.sharencare.user.settings;

public class ChangeEmailData {

    private String oldEmail;
    private String newEmail;
    private String password;

    public ChangeEmailData(String oldEmail, String newEmail, String password) {
        this.oldEmail = oldEmail;
        this.newEmail = newEmail;
        this.password = password;
    }


}
