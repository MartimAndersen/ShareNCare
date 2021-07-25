package pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks;

public class ReviewData {

    public String comment;
    public String rating;
    public String routeName;
    public String username;

    public ReviewData(String comment, String rating, String routeName, String username) {
        this.comment = comment;
        this.rating = rating;
        this.routeName = routeName;
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
