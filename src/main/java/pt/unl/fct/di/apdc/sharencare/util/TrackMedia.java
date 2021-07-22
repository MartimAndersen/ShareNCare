package pt.unl.fct.di.apdc.sharencare.util;


public class TrackMedia {

    public String imageUri;
    public String location;
    public String username;
    public int likes;

    public TrackMedia(String imageUri, String username, String location, int likes) {
        this.imageUri = imageUri;
        this.location = location;
        this.username = username;
        this.likes = likes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}