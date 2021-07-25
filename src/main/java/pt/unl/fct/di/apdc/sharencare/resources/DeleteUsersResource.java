package pt.unl.fct.di.apdc.sharencare.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.com.google.gson.reflect.TypeToken;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.FinishEvent;

@Path("/delete")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DeleteUsersResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

	@POST
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(@CookieParam("Token") NewCookie cookie, @QueryParam("username") String username) {

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.BAD_REQUEST).entity("User with username: " + username + " doesn't exist").build();

		if(!token.getString("username").equals(username))
			return Response.status(Status.CONFLICT).build();


		String e = user.getString("events");

		Type stringList = new TypeToken<ArrayList<String>>() {
		}.getType();
		List<String> events = g.fromJson(e, stringList);

		for(String eventId: events) {
			Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(eventId);
			Entity event = datastore.get(eventKey);

			String m = event.getString("members");
			List<String> members = g.fromJson(m, stringList);

			members.remove(username);

			event = Entity.newBuilder(eventKey).set("name", event.getString("name"))
					.set("description", event.getString("description"))
					.set("minParticipants", event.getString("minParticipants"))
					.set("maxParticipants", event.getString("maxParticipants")).set("time", event.getString("time"))
					.set("coordinates", event.getString("coordinates")).set("durability", event.getString("durability"))
					.set("institutionName", event.getString("institutionName"))
					.set("initial_date", event.getString("initial_date"))
					.set("ending_date", event.getString("ending_date")).set("members", g.toJson(members))
					.set("points", event.getString("points")).set("tags", event.getString("tags"))
					.set("rating", event.getString("rating")).set("ended", event.getString("ended")).build();

			datastore.update(event);
		}


		datastore.delete(userKey);

		return Response.ok("User deleted.").build();
	}

	@POST
	@Path("/institution")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInstitution(@CookieParam("Token") NewCookie cookie, @QueryParam("nif") String nif) {

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		if(!token.getString("username").equals(nif)){
			return Response.status(Status.CONFLICT).build();
		}

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(nif);
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.BAD_REQUEST).entity("Institution with nif: " + nif + " doesn't exist").build();

		datastore.delete(userKey);

		return Response.ok("Institution deleted.").build();

	}

	@POST
	@Path("/userWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUserWeb(@CookieParam("Token") NewCookie cookie) {

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		String e = user.getString("events");

		Type stringList = new TypeToken<ArrayList<String>>() {
		}.getType();
		List<String> events = g.fromJson(e, stringList);

		for(String eventId: events) {
			Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(eventId);
			Entity event = datastore.get(eventKey);

			String m = event.getString("members");
			List<String> members = g.fromJson(m, stringList);

			members.remove(token.getString("username"));

			event = Entity.newBuilder(eventKey).set("name", event.getString("name"))
					.set("description", event.getString("description"))
					.set("minParticipants", event.getString("minParticipants"))
					.set("maxParticipants", event.getString("maxParticipants")).set("time", event.getString("time"))
					.set("coordinates", event.getString("coordinates")).set("durability", event.getString("durability"))
					.set("institutionName", event.getString("institutionName"))
					.set("initial_date", event.getString("initial_date"))
					.set("ending_date", event.getString("ending_date")).set("members", g.toJson(members))
					.set("points", event.getString("points")).set("tags", event.getString("tags"))
					.set("rating", event.getString("rating")).set("ended", event.getString("ended")).build();

			datastore.update(event);
		}


		datastore.delete(userKey);

		return Response.ok("User deleted.").build();
	}

	@POST
	@Path("/institutionWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInstitutionWeb(@CookieParam("Token") NewCookie cookie) {

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		
		datastore.delete(userKey);

		return Response.ok("Institution deleted.").build();

	}

}
