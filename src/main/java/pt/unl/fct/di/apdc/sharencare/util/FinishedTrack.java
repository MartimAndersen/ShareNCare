package pt.unl.fct.di.apdc.sharencare.util;

import java.util.List;

public class FinishedTrack {

	public List<MarkerData> markers;
    public List<TrackMedia> media;
    public List<TrackNotes> notes;
    public String routeName;
    public List<TrackDangerZones> zones;
    
    public FinishedTrack() {
    	
    }
    
    
    public FinishedTrack(List<MarkerData> markers, List<TrackMedia> media, List<TrackNotes> notes, String routeName, List<TrackDangerZones> zones) {
    	this.markers = markers;
        this.media = media;
        this.notes = notes;
        this.routeName = routeName;
        this.zones = zones;
    }

   
}
