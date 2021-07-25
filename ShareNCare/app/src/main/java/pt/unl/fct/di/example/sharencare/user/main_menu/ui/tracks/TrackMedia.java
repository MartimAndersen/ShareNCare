package pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;


public class TrackMedia {

    public String imageName;
    public byte[] image;
    public String location;
    public String username;
    public int likes;

    public TrackMedia(String imageName, byte[] image, String username, String location, int likes) {
        this.imageName = imageName;
        this.image = image;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
