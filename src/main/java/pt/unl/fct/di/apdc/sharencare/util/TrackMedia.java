package pt.unl.fct.di.apdc.sharencare.util;

import org.glassfish.jersey.server.Uri;

import com.google.cloud.datastore.LatLng;

public class TrackMedia {

    public Uri imageUri;
    public LatLng location;
    public String username;
    public int likes;

    public TrackMedia(Uri imageUri, String username, LatLng location, int likes) {
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

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}