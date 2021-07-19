 
package pt.unl.fct.di.apdc.sharencare.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

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
import com.google.appengine.repackaged.com.google.gson.reflect.TypeToken;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.PointsData;
import pt.unl.fct.di.apdc.sharencare.util.ReviewData;




@Path("/ranking")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RakingUserResource {
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	public RakingUserResource() {
		
	}
	
	public void addPointsEvents(String username) {
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);
		
		
		String pointsString = user.getString("points");
		Type points = new TypeToken<PointsData>() {
		}.getType();
		PointsData userPoints = new Gson().fromJson(pointsString, points);
		
		userPoints.addEvents();

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
				.set("points", g.toJson(userPoints)).set("my_tracks", user.getString("my_tracks")).build();
		datastore.put(user);
	}
	
	public void addPointsComents(String username) {
		
		
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);
		
		String pointsString = user.getString("points");
		Type points = new TypeToken<PointsData>() {
		}.getType();
		PointsData userPoints = new Gson().fromJson(pointsString, points);
		
		userPoints.addComments();
		
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
				.set("points", g.toJson(userPoints)).set("my_tracks", user.getString("my_tracks")).build();
		datastore.put(user);
	}
	
	public void takePointsQuit(String username) {
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);
		

		String pointsString = user.getString("points");
		Type points = new TypeToken<PointsData>() {
		}.getType();
		PointsData userPoints = new Gson().fromJson(pointsString, points);
		
		userPoints.addQuitEvents();
		
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
				.set("points", g.toJson(userPoints)).set("my_tracks", user.getString("my_tracks")).build();
		datastore.put(user);
	}
	
	@GET
	@Path("/rankUsers")
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


		Query<Entity> query = Query.newEntityQueryBuilder().setKind("User").setFilter(PropertyFilter.eq("role", "USER")).setOrderBy(OrderBy.desc("points")).setLimit(5).build();
		QueryResults<Entity> eventsQuery = datastore.run(query);

		while (eventsQuery.hasNext()) {
			Entity e = eventsQuery.next();
				String event = g.toJson(e.getProperties().values());
				events.add(event);
			}
		
		
	return Response.ok(g.toJson(events)).cookie(cookie).build();
	}
	
	
	

}
