package pt.unl.fct.di.apdc.sharencare.util;

public class TrackData {

	public String description;
	public int difficulty;
	public String distance;
	public String time;
	public String solidarityPoints;
	public String title;
	public String type;
	public String username;

	public TrackData() {

	}

	public TrackData(String description, int difficulty, String time, String distance, String solidarityPoints, String title, String type, String username) {
		this.title = title;
		this.description = description;
		this.distance = distance;
		this.time = time;
		this.difficulty = difficulty;
		this.solidarityPoints = solidarityPoints;
		this.type = type;
		this.username = username;
	}
}
