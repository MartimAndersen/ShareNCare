package pt.unl.fct.di.apdc.sharencare.util;

public class TrackMarkers {

    public int likes;
    public String location;
    public String type;
    public String username;

    public TrackMarkers(int likes, String location, String type, String username) {
        this.likes = likes;
        this.location = location;
        this.type = type;
        this.username = username;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}