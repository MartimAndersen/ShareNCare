package pt.unl.fct.di.apdc.sharencare.util;

public class PointsData {
	
	String username;
	public int events;
	int tracks;
	int comments;
	int quitEvents;
	int badComments;
	int likedComents;
	int dislikedComents;
	int commentsRank;
	int total;
	byte[] pic;
	
	public PointsData() {
		
	}
	
	public PointsData(	String username) {
		
		this.username = username;
		this.events = 0;
		this.tracks = 0;
		this.comments = 0;
		this.quitEvents = 0;
		this.badComments = 0;
		this.likedComents = 0;
		this.dislikedComents = 0;
		this.commentsRank = 0;
		this.total = 0;
		this.pic = null;
	}
	
	public void addEvents() {
		events ++;
	}
	
	public void addTrack() {
		tracks++;
	}
	
	public void addComments() {
		comments++;
		commentsRank = 2*comments + likedComents - 2*badComments -dislikedComents;
	}
	
	public void addQuitEvents() {
		quitEvents++;
	}
	
	public void addBadComents() {
		badComments++;
		commentsRank = 2*comments + likedComents - 2*badComments -dislikedComents;
	}
	
	public void addLikedComents() {
		likedComents++;
		commentsRank = 2*comments + likedComents - 2*badComments -dislikedComents;
	}
	
	public void addDislikedComents() {
		dislikedComents++;
		commentsRank = 2*comments + likedComents - 2*badComments -dislikedComents;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPic(byte[] pic) {
		this.pic = pic;
	}
	
	public int getEvents() {
		return events;
	}
	
	public int getTracks() {
		return tracks;
	}
	
	public int getComments() {
		return comments;
	}
	
	public int getCommentsRank() {
		return commentsRank;
	}
	
	public int getLeaderBoard() {
		this.total = events + tracks + commentsRank - quitEvents ;
		return total;
	}
}
