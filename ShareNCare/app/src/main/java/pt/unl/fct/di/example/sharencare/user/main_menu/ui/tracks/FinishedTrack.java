package pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks;

import java.util.List;

public class FinishedTrack {

    public List<String> markers;
    public List<String> media;
    public List<String> notes;
    public String routeName;
    public List<String> zones;

    public FinishedTrack(List<String> markers, List<String> media, List<String> notes, String routeName, List<String> zones) {
        this.markers = markers;
        this.media = media;
        this.notes = notes;
        this.routeName = routeName;
        this.zones = zones;
    }

    public List<String> getMarkers() {
        return markers;
    }

    public void setMarkers(List<String> markers) {
        this.markers = markers;
    }

    public List<String> getMedia() {
        return media;
    }

    public void setMedia(List<String> media) {
        this.media = media;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public List<String> getZones() {
        return zones;
    }

    public void setZones(List<String> zones) {
        this.zones = zones;
    }
}
