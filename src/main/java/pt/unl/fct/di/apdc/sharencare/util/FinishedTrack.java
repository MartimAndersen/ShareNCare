package pt.unl.fct.di.apdc.sharencare.util;

import java.util.List;

public class FinishedTrack {

	public List<String> markers;
    public List<String> media;
    public List<String> notes;
    public String routeName;
    public List<String> zones;
    
    public FinishedTrack() {
    	
    }
    
    
    public FinishedTrack(List<String> markers, List<String> media, List<String> notes, String routeName, List<String> zones) {
    	this.markers = markers;
        this.media = media;
        this.notes = notes;
        this.routeName = routeName;
        this.zones = zones;
    }

   
}
