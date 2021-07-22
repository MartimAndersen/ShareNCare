package pt.unl.fct.di.apdc.sharencare.util;


public class TrackNotes {

	public String username;
	public String note;
	public String location;
	public int likes;

	public TrackNotes(String username, String note, String location, int likes) {
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

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}
}