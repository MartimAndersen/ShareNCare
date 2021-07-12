package pt.unl.fct.di.apdc.sharencare.util;

public class RatingData {
	
	/*
	 * nome de quem comentou
    - id/nome do evento/percurso
    - comentario
    - usefulness
    -time
	 */
	
	public String username;
	public String eventName;
	public String rating;
	
	public RatingData() {
		
	}
	
	public RatingData(String username, String routeName, String rating) {
		this.username = username;
		this.eventName = routeName;
		this.rating = rating;
	}
	
	

}
