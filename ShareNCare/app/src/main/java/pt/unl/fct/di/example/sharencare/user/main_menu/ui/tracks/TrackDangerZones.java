package pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks;

import com.google.android.gms.maps.model.LatLng;

public class TrackDangerZones {

    public String username;
    public String location;
    public String note;
    public int likes;

    public TrackDangerZones(String username, String note, String location, int likes) {
        this.username = username;
        this.location = location;
        this.note = note;
        this.likes = likes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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
}
