package pt.unl.fct.di.apdc.sharencare.util;

public class PointsData {
	
	public String username;
	public int events;
	public int tracks;
	public int comments;
	public int quitEvents;
	public int badComments;
	public int likedComents;
	public int dislikedComents;
	public int commentsRank;
	public int total;
	public int rank;
	public byte[] pic;
	
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
		this.rank = -1;
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
