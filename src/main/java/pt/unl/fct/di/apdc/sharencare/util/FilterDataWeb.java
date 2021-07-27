package pt.unl.fct.di.apdc.sharencare.util;

import java.lang.reflect.Type;
import java.util.List;

import com.google.appengine.repackaged.com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

public class FilterDataWeb {


	public String tags;
	
	private final Gson g = new Gson();
	
	public FilterDataWeb() {
		
	}

	public FilterDataWeb(String tags) {

		this.tags = tags;
	}
	
	public boolean containsTag(String tagEvent) {
		
		Type list = new TypeToken<List<Integer>>() {
		}.getType();
		List<Integer> tagsEv = new Gson().fromJson(tagEvent, list);
		List<Integer> tagsList = new Gson().fromJson(tags, list);
		
		for(Integer t : tagsList) {
			if(tagsEv.contains(t)) {
				return true;
			}
		}
		return false;
	}

	
}
