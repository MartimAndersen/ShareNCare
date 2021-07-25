package pt.unl.fct.di.example.sharencare.user.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class TrackData {
    public String description;
    public int difficulty;
    public String distance;
    public String time;
    public String solidarityPoints;
    public String title;
    public String type;
    public String username;

    public TrackData(String description, int difficulty, String distance, String time, String solidarityPoints, String title, String type, String username) {
        this.description = description;
        this.difficulty = difficulty;
        this.distance = distance;
        this.time = time;
        this.solidarityPoints = solidarityPoints;
        this.title = title;
        this.type = type;
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

    public String getSolidarityPoints() {
        return solidarityPoints;
    }

    public void setSolidarityPoints(String solidarityPoints) {
        this.solidarityPoints = solidarityPoints;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
