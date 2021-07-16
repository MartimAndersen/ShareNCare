 
package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.gson.Gson;


@Path("/ranking")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RakingUserResource {
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	
	public RakingUserResource() {
		
	}
	
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
	
	@GET
	@Path("/getPic")
	@Produces(MediaType.APPLICATION_JSON)
	public Response rankUsers(@CookieParam("Token") NewCookie cookie) {
		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();
		
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist").build();

		}

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		}
		



		
		List<String> events = new ArrayList<>();


		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").setOrderBy(OrderBy.desc("points")).setLimit(5).build();
		QueryResults<Entity> eventsQuery = datastore.run(query);

		while (eventsQuery.hasNext()) {
			Entity e = eventsQuery.next();
				String event = g.toJson(e.getProperties().values());
				events.add(event);
			}
		
		
	return Response.ok(g.toJson(events)).cookie(cookie).build();
	}
	
	
	

}
