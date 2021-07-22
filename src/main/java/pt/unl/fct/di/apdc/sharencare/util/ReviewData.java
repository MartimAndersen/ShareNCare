package pt.unl.fct.di.apdc.sharencare.util;

public class ReviewData  implements Comparable<ReviewData>{
	
	/*
	 * nome de quem comentou
    - id/nome do evento/percurso
    - comentario
    - usefulness
    -time
	 */
	
	public String comment;
	public String rating;
	public String routeName;
	public String username;
	public int likes;
	
	public ReviewData() {
		
	}
	
	public ReviewData(String comment, String rating, String routeName, String username) {
		this.username = username;
		this.routeName = routeName;
		this.comment = comment;
		this.rating = rating;
	}
	
	public boolean commentIsValid() {
		return !comment.isEmpty();
	}
	
	public String getUsername() {
		return username;
	}
	
	public boolean ratingIsValid() {
		if(Float.parseFloat(rating) >= 0 && Float.parseFloat(rating) <= 5) {
			return true;
		}else {
			return false;
		}
	}
	
	public void addLike() {
		likes++;
	}
	
	public void addDislike() {
		likes--;
	}
	
	public int returnLikes() {
		return likes;
	}

	@Override
	public int compareTo(ReviewData o) {
		if (likes == o.likes)
            return 0;
        else if (likes > o.likes)
            return 1;
        else
            return -1;
	}
	
	

}
