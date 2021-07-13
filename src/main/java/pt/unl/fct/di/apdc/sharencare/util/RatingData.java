package pt.unl.fct.di.apdc.sharencare.util;

public class RatingData {
	
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
	
	public boolean ratingIsValid() {
		if(Integer.parseInt(rating) >= 1 && Integer.parseInt(rating) <= 5) {
			return true;
		}else {
			return false;
		}
	}
	
	

}
