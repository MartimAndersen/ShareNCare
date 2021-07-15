 
package pt.unl.fct.di.apdc.sharencare.resources;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;

public class RakingUserResource {
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	
	public void addPointsEvents(String username) {
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey("username");
		Entity user = datastore.get(userKey);
		
		int points =  Integer.parseInt( user.getString("points"));
		points += 2;
		
		user = Entity.newBuilder(userKey).set("username",username)
				.set("password",user.getString("password"))
				.set("email", user.getString("email")).set("bio", user.getString("bio"))
				.set("profileType", user.getString("profileType"))
				.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
				.set("address", user.getString("address"))
				.set("secondAddress", user.getString("secondAddress"))
				.set("zipCode", user.getString("zipCode")).set("role", user.getString("role"))
				.set("state", user.getString("state"))
				.set("tags", user.getString("tags")).set("events", user.getString("events"))
				.set("points", g.toJson(points)).build();
		datastore.put(user);
	}
	
	public void addPointsComents(String username) {
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey("username");
		Entity user = datastore.get(userKey);
		
		int points =  Integer.parseInt( user.getString("points"));
		points += 1;
		
		user = Entity.newBuilder(userKey).set("username",username)
				.set("password",user.getString("password"))
				.set("email", user.getString("email")).set("bio", user.getString("bio"))
				.set("profileType", user.getString("profileType"))
				.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
				.set("address", user.getString("address"))
				.set("secondAddress", user.getString("secondAddress"))
				.set("zipCode", user.getString("zipCode")).set("role", user.getString("role"))
				.set("state", user.getString("state"))
				.set("tags", user.getString("tags")).set("events", user.getString("events"))
				.set("points", g.toJson(points)).build();
		datastore.put(user);
	}
	
	public void takePointsQuit(String username) {
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);
		
		int points =  Integer.parseInt( user.getString("points"));
		points -= 1;
		
		user = Entity.newBuilder(userKey).set("username",username)
				.set("password",user.getString("password"))
				.set("email", user.getString("email")).set("bio", user.getString("bio"))
				.set("profileType", user.getString("profileType"))
				.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
				.set("address", user.getString("address"))
				.set("secondAddress", user.getString("secondAddress"))
				.set("zipCode", user.getString("zipCode")).set("role", user.getString("role"))
				.set("state", user.getString("state"))
				.set("tags", user.getString("tags")).set("events", user.getString("events"))
				.set("points", g.toJson(points)).build();
		datastore.put(user);
	}
	

}
