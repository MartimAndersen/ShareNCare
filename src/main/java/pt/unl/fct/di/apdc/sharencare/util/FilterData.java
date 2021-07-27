package pt.unl.fct.di.apdc.sharencare.util;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

public class FilterData {

	public String coordinates;
	public String date;
	public String institution;
	public String name;
	public String popularity;
	public List<Integer> tags;
	
	private Gson gson = new Gson();
	
	public FilterData() {
		
	}

	public FilterData(String coordinates, String date, String institution, String name, String popularity,
			List<Integer> tags) {
		this.coordinates = coordinates;
		this.date = date;
		this.institution = institution;
		this.name = name;
		this.popularity = popularity;
		this.tags = tags;
	}


	public List<PropertyFilter> getFilter() {

		List<PropertyFilter> filters = new ArrayList<>();

		if (!coordinates.equals(""))
			filters.add(PropertyFilter.eq("coordinates", coordinates));

		if (!date.equals(""))
			filters.add(PropertyFilter.eq("date", date));

		if (!institution.equals(""))
			filters.add(PropertyFilter.eq("institutionName", institution));

		if (!name.equals(""))
			filters.add(PropertyFilter.eq("name", name));

		if (!tags.isEmpty())
			filters.add(PropertyFilter.eq("tags", gson.toJson(tags)));

		return filters;
	}

}
