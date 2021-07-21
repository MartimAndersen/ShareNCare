package pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class TrackMedia {

    public Uri imageUri;
    public LatLng location;

    public TrackMedia(Uri imageUri, LatLng location) {
        this.imageUri = imageUri;
        this.location = location;
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
}
