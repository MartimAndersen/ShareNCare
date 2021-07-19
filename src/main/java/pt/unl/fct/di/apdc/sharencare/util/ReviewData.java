package pt.unl.fct.di.apdc.sharencare.util;

public class ReviewData {
	
	/*
	 * nome de quem comentou
    - id/nome do evento/percurso
    - comentario
    - usefulness
    -time
	 */
	
	public String username;
	public String routeName;
	public String comment;
	public String rating;
	
	public ReviewData() {
		
	}
	
	public ReviewData(String username, String routeName, String comment, String rating) {
		this.username = username;
		this.routeName = routeName;
		this.comment = comment;
		this.rating = rating;
	}
	
	public boolean commentIsValid() {
		return !comment.isEmpty();
	}
	
	public boolean ratingIsValid() {
		if(Integer.parseInt(rating) >= 1 && Integer.parseInt(rating) <= 5) {
			return true;
		}else {
			return false;
		}
	}
	
	public String getUsername() {
		return username;
	}
	
	

}
