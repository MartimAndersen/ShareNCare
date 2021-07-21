package pt.unl.fct.di.apdc.sharencare.util;

public class MarkerData {

    public String coordinates;
    public Long lat;
    public Long lon;
    public String description;

    public MarkerData() {

    }
    
    public MarkerData(Long lat, Long lon, String description){
        this.coordinates = lat + " " + lon;
        this.description = description;
    }
}
