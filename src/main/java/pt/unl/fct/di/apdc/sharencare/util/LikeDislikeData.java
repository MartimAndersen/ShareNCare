package pt.unl.fct.di.apdc.sharencare.util;

public class LikeDislikeData {

	public boolean isLike;
	public int like;
	public String username;
	public String title;

	public LikeDislikeData() {

	}

	public LikeDislikeData(boolean isLike, int like, String username, String title) {
		this.isLike = isLike;
		this.like = like;
		this.username = username;
		this.title = title;
	}

}
