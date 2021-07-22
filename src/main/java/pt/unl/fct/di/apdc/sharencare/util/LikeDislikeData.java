package pt.unl.fct.di.apdc.sharencare.util;

public class LikeDislikeData {

	public boolean isLike;
	public String username;
	public String title;

	public LikeDislikeData() {

	}

	public LikeDislikeData(boolean isLike, String username, String title) {
		this.isLike = isLike;
		this.username = username;
		this.title = title;
	}

}
