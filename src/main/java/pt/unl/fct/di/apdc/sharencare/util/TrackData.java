package pt.unl.fct.di.apdc.sharencare.util;

public class TrackData {

    public String title, description, tokenId, origin, destination, distance, difficulty;



    public TrackData(){

    }

    public TrackData(String title, String description,String tokenId, String origin, String destination, String distance, String difficulty){
        this.title = title;
        this.description = description;
        this.tokenId = tokenId;
        this.origin = origin;
        this.destination = destination;
        this.distance = distance;
        this.difficulty = difficulty;
    }
}
