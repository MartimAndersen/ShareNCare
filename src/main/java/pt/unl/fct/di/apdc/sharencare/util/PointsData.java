package pt.unl.fct.di.apdc.sharencare.util;

public class PointsData {
	
	String username;
	int events;
	int tracks;
	int comments;
	int quitEvents;
	int badComments;
	
	public PointsData() {
		
	}
	
	public PointsData(	String username) {
		
		this.username = username;
		this.events = 0;
		this.tracks = 0;
		this.comments = 0;
		this.quitEvents = 0;
		this.badComments = 0;
	}
	
	public void addEvents() {
		events ++;
	}
	
	public void addTrack() {
		tracks++;
	}
	
	public void addComments() {
		comments++;
	}
	
	public void addQuitEvents() {
		quitEvents++;
	}
	
	public void addBadComents() {
		badComments++;
	}
}
