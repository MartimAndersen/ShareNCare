package pt.unl.fct.di.example.sharencare.user.main_menu.ui.find_tracks;

public class LikeDislikeData {

    public boolean isLike;
    public int like;
    public String username;
    public String title;

    public LikeDislikeData(boolean isLike, int like, String username, String title) {
        this.isLike = isLike;
        this.like = like;
        this.username = username;
        this.title = title;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
