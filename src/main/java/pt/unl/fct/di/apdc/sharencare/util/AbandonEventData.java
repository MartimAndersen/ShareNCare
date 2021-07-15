package pt.unl.fct.di.apdc.sharencare.util;

import java.util.List;

public class AbandonEventData {
	
	public List<String> eventsId;
	public String username;
	
	public AbandonEventData() {
		
	}
	
	public AbandonEventData(List<String> eventsId, String username) {
		this.eventsId = eventsId;
		this.username = username;
	}

}
