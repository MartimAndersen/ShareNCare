package pt.unl.fct.di.apdc.sharencare.util;

public class TrackData {

    public String description, destination, difficulty, distance, origin, title;



    public TrackData(){

    }

    public TrackData(String description, String destination, String difficulty,  String distance, String origin, String title){
        this.title = title;
        this.description = description;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.difficulty = difficulty;
    }
}
