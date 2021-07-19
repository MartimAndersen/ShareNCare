package pt.unl.fct.di.apdc.sharencare.util;

import java.util.List;

import com.google.cloud.datastore.LatLng;

public class TrackData {

    public String description;
    public String destination;
    public String difficulty;
    public String distance;
    public String origin;
    public String points;
    public String title;

    public TrackData(){

    }

    public TrackData(String description, String destination, String difficulty,  String distance, String origin, String points, String title){
        this.title = title;
        this.description = description;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.difficulty = difficulty;
        this.points = points;
    }
}
