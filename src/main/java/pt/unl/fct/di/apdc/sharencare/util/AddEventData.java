package pt.unl.fct.di.apdc.sharencare.util;

import java.util.List;

import com.google.gson.Gson;

public class AddEventData {
	public String tokenId;
	public String events;
	
	private Gson gson;
	
	public AddEventData() {
		
	}
	
	public AddEventData(String tokenId, List<String> events) {
		
		gson = new Gson();
		this.tokenId = tokenId;
		this.events = gson.toJson(events);
	}
}
