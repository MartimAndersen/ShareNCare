package pt.unl.fct.di.apdc.sharencare.util;

public class TrackData {

    public String description;
    public int difficulty;
    public String distance;
    public String solidarityPoints;
    public String polylines;
    public String title;

    public TrackData(){

    }

    public TrackData(String description, int difficulty,  String distance, String solidarityPoints, String polylines, String title){
        this.title = title;
        this.description = description;
        this.distance = distance;
        this.difficulty = difficulty;
        this.solidarityPoints = solidarityPoints;
        this.polylines = polylines;
    }
}
