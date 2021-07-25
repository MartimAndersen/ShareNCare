package pt.unl.fct.di.example.sharencare.common.tracks;

import java.util.List;

import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackMarkers;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackDangerZones;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackMedia;
import pt.unl.fct.di.example.sharencare.user.main_menu.ui.tracks.TrackNotes;

public class TrackInfo {

    public String comments;
    public String description;
    public int difficulty;
    public String distance;
    public List<TrackMarkers> markers;
    public String points;
    public String time;
    public String title;
    public List<TrackDangerZones> zones;
    public List<TrackMedia> media;
    public List<TrackNotes> notes;
    public float averageRating;
    public String type;
    public String username;

    public TrackInfo(float averageRating, String comments, String description, int difficulty, String distance, List<TrackMarkers> markers, String points, String time, String title, List<TrackDangerZones> zones, List<TrackMedia> media, List<TrackNotes> notes, String type, String username) {
        this.comments = comments;
        this.description = description;
        this.difficulty = difficulty;
        this.distance = distance;
        this.markers = markers;
        this.points = points;
        this.time = time;
        this.title = title;
        this.type = type;
        this.zones = zones;
        this.media = media;
        this.notes = notes;
        this.averageRating = averageRating;
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TrackMarkers> getMarkers() {
        return markers;
    }

    public void setMarkers(List<TrackMarkers> markers) {
        this.markers = markers;
    }

    public List<TrackDangerZones> getZones() {
        return zones;
    }

    public void setZones(List<TrackDangerZones> zones) {
        this.zones = zones;
    }

    public List<TrackMedia> getMedia() {
        return media;
    }

    public void setMedia(List<TrackMedia> media) {
        this.media = media;
    }

    public List<TrackNotes> getNotes() {
        return notes;
    }

    public void setNotes(List<TrackNotes> notes) {
        this.notes = notes;
    }

    public float getRating() {
        return averageRating;
    }

    public void setRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
