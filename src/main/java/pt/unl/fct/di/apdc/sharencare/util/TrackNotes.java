package pt.unl.fct.di.apdc.sharencare.util;

import com.google.cloud.datastore.LatLng;

public class TrackNotes {

	public String username;
	public String note;
	public LatLng location;
	public int likes;

	public TrackNotes(String username, String note, LatLng location, int likes) {
		this.username = username;
		this.note = note;
		this.location = location;
		this.likes = likes;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LatLng getLocation() {
		return location;
	}

	public void setLocation(LatLng location) {
		this.location = location;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}
}