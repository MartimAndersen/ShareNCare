package pt.unl.fct.di.apdc.sharencare.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.repackaged.com.google.gson.reflect.TypeToken;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.JoinEventData;
import pt.unl.fct.di.apdc.sharencare.util.RatingData;
import pt.unl.fct.di.apdc.sharencare.util.ReviewData;
import pt.unl.fct.di.apdc.sharencare.util.EventData;

@Path("/event")
public class EventResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	final ObjectMapper objectMapper = new ObjectMapper();
	
	@POST
	@Path("/registerEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerEvent(@CookieParam("Token") NewCookie cookie, EventData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		/*
		 * END OF VERIFICATIONS
		 */

		if (data.atLeastOneEmptyParameter())
			return Response.status(Status.LENGTH_REQUIRED).build();

		if (!data.validParticipants())
			return Response.status(Status.NOT_ACCEPTABLE).build();

		if (!data.verifyDate())
			return Response.status(Status.FORBIDDEN).build();

		if (!data.isHourValid())
			return Response.status(Status.EXPECTATION_FAILED).build();

		Transaction txn = datastore.newTransaction();

		try {
			Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(data.name);
			Entity event = txn.get(eventKey);

			if (event != null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("The event with the given title already exists.")
						.build();
			} else {
				
				String points = g.toJson(0);
				String coordinates = data.lat + " " + data.lon;
				List<Integer> l = new ArrayList<Integer>();
				event = Entity.newBuilder(eventKey).set("name", data.name).set("description", data.description)
						.set("minParticipants", data.minParticipants).set("maxParticipants", data.maxParticipants)
						.set("time", data.time).set("coordinates", coordinates).set("durability", data.durability)
						.set("institutionName", data.institutionName).set("initial_date", data.initialDate)
						.set("ending_date", data.endingDate).set("members", g.toJson(new ArrayList<String>()))
						.set("points", points).set("tags", g.toJson(data.tags)).build();

				txn.add(event);
				txn.commit();
				return Response.ok("Event " + data.name + " registered.").cookie(cookie).build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/deleteEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEvent(@QueryParam("eventId") String eventId) {
		Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(eventId);
		Entity event = datastore.get(eventKey);

		if (event == null)
			return Response.status(Status.BAD_REQUEST).entity("Event with id: " + eventId + " doesn't exist").build();
		
		datastore.delete(eventKey);

		return Response.ok("Event deleted.").build();

	}
	
	@SuppressWarnings("unchecked")
	@POST
	@Path("/joinEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response joinEvent(@CookieParam("Token") NewCookie cookie, JoinEventData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		if (data.atLeastOneEmptyParameter())
			return Response.status(Status.LENGTH_REQUIRED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id doesn't exist").build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();

		if (user.getString("state").equals("DISABLED")) {
			System.out.println("The user with the given token is disabled.");
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("User with id: " + user.getString("username") + " is disabled.").build();
		}

		/*
		 * END OF VERIFICATIONS
		 */

		List<String> events = new ArrayList<String>();
		String e = user.getString("events");

		if (!e.equals(""))
			events = g.fromJson(e, List.class);

		events.add(data.eventId);

		user = Entity.newBuilder(userKey).set("username", token.getString("username"))
				.set("password", user.getString("password")).set("email", user.getString("email"))
				.set("profileType", user.getString("profileType")).set("landLine", user.getString("landLine"))
				.set("mobile", user.getString("mobile")).set("address", user.getString("address"))
				.set("secondAddress", user.getString("secondAddress")).set("zipCode", user.getString("zipCode"))
				.set("tags", user.getString("tags")).set("bio", user.getString("bio")).set("events", g.toJson(events))
				.set("role", user.getString("role")).set("state", user.getString("state")).build();

		datastore.update(user);

		return Response.ok("Joined successfully.").cookie(cookie).build();
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/addEventInstitution")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEventToInstitution(@CookieParam("Token") NewCookie cookie, JoinEventData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */
		
		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();

		/*
		 * END OF VERIFICATIONS
		 */

		List<String> events = new ArrayList<String>();
		String e = user.getString("events");

		if (!e.equals(""))
			events = g.fromJson(e, List.class);

		events.add(data.eventId);

		user = Entity.newBuilder(userKey).set("nif", token.getString("username"))
				.set("username", user.getString("username")).set("password", user.getString("password"))
				.set("email", user.getString("email")).set("landLine", user.getString("landLine"))
				.set("mobile", user.getString("mobile")).set("address", user.getString("address"))
				.set("zipCode", user.getString("zipCode")).set("events", g.toJson(events))
				.set("website", user.getString("website")).set("instagram", user.getString("instagram"))
				.set("twitter", user.getString("twitter")).set("facebook", user.getString("facebook"))
				.set("youtube", user.getString("youtube")).set("bio", user.getString("bio"))
				.set("fax", user.getString("fax")).set("role", user.getString("role"))
				.set("state", user.getString("state")).build();

		datastore.update(user);

		return Response.ok("Properties changed").cookie(cookie).build();
	}
	
	@POST
	@Path("/rating")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerRating(@CookieParam("Token") NewCookie cookie, RatingData data) {
		
		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();
		
		/*
		 * END OF VERIFICATIONS
		 */
		
		if(data.ratingIsValid()) {
			return Response.status(Status.FORBIDDEN).build();
		}


		Transaction txn = datastore.newTransaction();

		try {
			Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(data.eventName);
			Entity event = txn.get(eventKey);
			
			String commentList = event.getString("rating");
			
			Type rating = new TypeToken<ArrayList<RatingData>>(){}.getType();
			List<RatingData> ratings = new Gson().fromJson(commentList, rating);
			

			List<RatingData> newRatings = new ArrayList<RatingData>();

			for (int i = 0; i < ratings.size(); i++)
				newRatings.add(ratings.get(i));

			newRatings.add(data);

			event = Entity.newBuilder(eventKey).set("name", event.getString("name")).set("description", event.getString("description"))
					.set("minParticipants", event.getString("minParticipants")).set("maxParticipants", event.getString("maxParticipants"))
					.set("time", event.getString("time")).set("coordinates", event.getString("coordinates")).set("durability", event.getString("durability"))
					.set("institutionName", event.getString("institutionName")).set("initial_date", event.getString("initialDate"))
					.set("ending_date", event.getString("endingDate")).set("members", event.getString("members"))
					.set("points", event.getString("points")).set("tags", event.getString("tags")).set("rating", g.toJson(newRatings)).build();

			txn.add(event);
			txn.commit();
			
			return Response.ok("Comment from " + data.username + " registered.").cookie(cookie).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	
	@GET
	@Path("/getAllEvents")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllEvents(@CookieParam("Token") NewCookie cookie) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */
		
		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist").build();

		/*
		 * END OF VERIFICATIONS
		 */

		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").build();

		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();
		while (eventsQuery.hasNext()) {
			String event = g.toJson(eventsQuery.next().getProperties().values());
			events.add(event);
		}

		return Response.ok(g.toJson(events)).cookie(cookie).build();
	}
	
	@GET
	@Path("/listUserEvents")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listUserEvents(@CookieParam("Token") NewCookie cookie) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */
		
		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + cookie.getName() + " doesn't exist").build();

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser == null)
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();

		/*
		 * END OF VERIFICATIONS
		 */

		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").build();

		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
		List<String> userEvents = new ArrayList<String>();

		try {
			userEvents = Arrays.asList(mapper.readValue(currentUser.getString("events"), String[].class));
			while (eventsQuery.hasNext()) {
				Entity e = eventsQuery.next();
				if (userEvents.contains(e.getString("name"))) {
					String event = g.toJson(e.getProperties().values());
					events.add(event);
				}
			}
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}

		return Response.ok(g.toJson(events)).cookie(cookie).build();
	}

	@GET
	@Path("/getEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEvent(@QueryParam("eventId") String eventId) {
		Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(eventId);
		Entity event = datastore.get(eventKey);

		if (event == null)
			return Response.status(Status.BAD_REQUEST).entity("Event with id: " + eventId + " doesn't exist").build();

		return Response.ok(g.toJson(event.getProperties().values())).build();

	}

}
