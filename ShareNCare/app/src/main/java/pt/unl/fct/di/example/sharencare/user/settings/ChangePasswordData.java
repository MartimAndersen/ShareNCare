package pt.unl.fct.di.example.sharencare.user.settings;

public class ChangePasswordData {

    private String oldPassword;
    private String newPassword;
    private String confirmation;

    public ChangePasswordData(String oldPassword, String newPassword, String confirmation) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmation = confirmation;
    }
}
