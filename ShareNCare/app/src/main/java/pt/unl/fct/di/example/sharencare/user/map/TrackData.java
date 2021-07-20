package pt.unl.fct.di.example.sharencare.user.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class TrackData {
    public String description;
    public int difficulty;
    public String distance;
    public String solidarityPoints;
    public String title;
    public String type;

    public TrackData(String description, int difficulty, String distance, String solidarityPoints, String title, String type) {
        this.description = description;
        this.difficulty = difficulty;
        this.distance = distance;
        this.solidarityPoints = solidarityPoints;
        this.title = title;
        this.type = type;
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
}
