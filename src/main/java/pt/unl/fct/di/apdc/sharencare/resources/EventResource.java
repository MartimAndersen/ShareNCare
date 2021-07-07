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
import javax.ws.rs.PathParam;
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

import pt.unl.fct.di.apdc.sharencare.util.AddEventData;
import pt.unl.fct.di.apdc.sharencare.util.JoinEvent;
import pt.unl.fct.di.apdc.sharencare.util.EventData;

@Path("/event")
public class EventResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	final ObjectMapper objectMapper = new ObjectMapper();
	AuthTokenResource t = new AuthTokenResource();

	@POST
	@Path("/registerEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerEvent(@CookieParam("Token") NewCookie cookie, EventData data) {
		LOG.fine("Attempt to register event: " + data.name);

		if (cookie.getName().equals("")) {
			System.out.println("You need to be logged in to execute this operation.");
			return Response.status(Status.UNAUTHORIZED).build();
		}

		if (data.atLeastOneEmptyParameter()) {
			System.out.println("Please fill in all fields.");
			return Response.status(Status.LENGTH_REQUIRED).build();
		}

		if (Integer.parseInt(data.minParticipants) <= 0
				|| Integer.parseInt(data.maxParticipants) < Integer.parseInt(data.minParticipants)) {
			System.out.println("Number of participants is incorrect");
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}

		if (!data.verifyDate()) {
			System.out.println("Date is not valid");
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!data.isHourValid()) {
			System.out.println("Hour is not valid");
			return Response.status(Status.EXPECTATION_FAILED).build();
		}

		Transaction txn = datastore.newTransaction();
		try {
			Key mapKey = datastore.newKeyFactory().setKind("Event").newKey(data.name);
			Entity event = txn.get(mapKey);
			if (event != null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("The event with the given title already exists.")
						.build();
			} else {
				String coordinates = data.lat + " " + data.lon;
				event = Entity.newBuilder(mapKey).set("name", data.name).set("description", data.description)
						.set("minParticipants", data.minParticipants).set("maxParticipants", data.maxParticipants)
						.set("hour", data.time).set("coordinates", coordinates).set("temporary", data.durability)
						.set("initial_date", data.initialDate).set("ending_date", data.endingDate)
						.set("tags", g.toJson(data.tags)).build();

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
	@Path("/registerEventAndroid")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerEventAndroid(@QueryParam("tokenId") String tokenId, EventData data) {
		LOG.fine("Attempt to register event: " + data.name);

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + tokenId + " doesn't exist").build();

		}

		if (data.atLeastOneEmptyParameter()) {
			System.out.println("Please fill in all fields.");
			return Response.status(Status.LENGTH_REQUIRED).build();
		}

		if (Integer.parseInt(data.minParticipants) <= 0
				|| Integer.parseInt(data.maxParticipants) < Integer.parseInt(data.minParticipants)) {
			System.out.println("Number of participants is incorrect");
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}

		if (!data.verifyDate()) {
			System.out.println("Date is not valid");
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!data.isHourValid()) {
			System.out.println("Hour is not valid");
			return Response.status(Status.EXPECTATION_FAILED).build();
		}

		Transaction txn = datastore.newTransaction();
		try {
			Key mapKey = datastore.newKeyFactory().setKind("Event").newKey(data.name);
			Entity event = txn.get(mapKey);
			if (event != null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("The event with the given title already exists.")
						.build();
			} else {
				String coordinates = data.lat + " " + data.lon;
				event = Entity.newBuilder(mapKey).set("name", data.name).set("description", data.description)
						.set("minParticipants", data.minParticipants).set("maxParticipants", data.maxParticipants)
						.set("hour", data.time).set("coordinates", coordinates).set("temporary", data.durability)
						.set("initial_date", data.initialDate).set("ending_date", data.endingDate)
						.set("tags", g.toJson(data.tags)).build();

				txn.add(event);
				txn.commit();
				return Response.ok("Event " + data.name + " registered.").build();
			}
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
	public Response getAllEvents(@QueryParam("tokenId") String tokenId) {

		if (tokenId.equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + tokenId + " doesn't exist").build();
		}

		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").build();

		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();
		while (eventsQuery.hasNext()) {
			String event = g.toJson(eventsQuery.next().getProperties().values());
			events.add(event);
		}

		return Response.ok(g.toJson(events)).build();

	}

	@Produces
	@GET
	@Path("/getAllEventsWeb")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAllEventsWeb(@CookieParam("Token") NewCookie cookie) {

		if (cookie.getName().equals("")) {
			System.out.println("You need to be logged in to execute this operation.");
			return Response.status(Status.UNAUTHORIZED).build();
		}

		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").build();

		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();
		while (eventsQuery.hasNext()) {
			String event = g.toJson(eventsQuery.next().getProperties().values());
			events.add(event);
		}

		return Response.ok(g.toJson(events)).cookie(cookie).build();

	}

	@POST
	@Path("/addEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEventToUser(AddEventData data) {

		if (data.tokenId.equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + data.tokenId + " doesn't exist")
					.build();
		}

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		}

		if (user.getString("state").equals("DISABLED")) {
			System.out.println("The user with the given token is disabled.");
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("User with id: " + user.getString("username") + " is disabled.").build();
		}

		try {
			String userEvents = user.getString("events");
			String[] events = objectMapper.readValue(userEvents, String[].class);

			String[] newEvents = new String[events.length + 1];

			for (int i = 0; i < events.length; i++)
				newEvents[i] = events[i];

			newEvents[events.length] = data.eventId;

			user = Entity.newBuilder(userKey)
					.set("username", token.getString("username"))
					.set("password", user.getString("password"))
					.set("email", user.getString("email"))
					.set("profileType", user.getString("profileType"))
					.set("landLine", user.getString("landLine"))
					.set("mobile", user.getString("mobile"))
					.set("address", user.getString("address"))
					.set("secondAddress", user.getString("secondAddress"))
					.set("postal", user.getString("postal"))
					.set("profilePic", user.getString("profilePic"))
					.set("tags", user.getString("tags"))
					.set("events", g.toJson(newEvents))
					.set("role", user.getString("role"))
					.set("state", user.getString("state")).build();

			datastore.update(user);

		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Response.ok("Properties changed").build();
	}

	@POST
	@Path("/addEventInstitution")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEventToInstitution(AddEventData data) {
		if (data.tokenId.equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + data.tokenId + " doesn't exist")
					.build();
		}

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		}

		try {
			String userEvents = user.getString("events");
			String[] events = objectMapper.readValue(userEvents, String[].class);

			String[] newEvents = new String[events.length + 1];

			for (int i = 0; i < events.length; i++)
				newEvents[i] = events[i];

			newEvents[events.length] = data.eventId;

			user = Entity.newBuilder(userKey)
					.set("nif", token.getString("username"))
					.set("username", user.getString("username"))
					.set("password", user.getString("password"))
					.set("email", user.getString("email"))
					.set("landLine", user.getString("landLine"))
					.set("mobile", user.getString("mobile"))
					.set("address", user.getString("address"))
					.set("postal", user.getString("postal"))
					.set("profilePic", user.getString("profilePic"))
					.set("events", g.toJson(newEvents))
					.set("website", user.getString("website"))
					.set("instagram", user.getString("instagram"))
					.set("twitter", user.getString("twitter"))
					.set("facebook", user.getString("facebook"))
					.set("youtube", user.getString("youtube"))
					.set("fax", user.getString("fax"))
					.set("role", user.getString("role"))
					.set("state", user.getString("state")).build();

			datastore.update(user);

		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Response.ok("Properties changed").build();
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/joinEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response joinEvent(@CookieParam("Token") NewCookie cookie, JoinEvent data) {

		LOG.fine("Attempt to join event.");

		if (cookie.getName().equals("")) {
			System.out.println("You need to be logged in to execute this operation.");
			return Response.status(Status.UNAUTHORIZED).build();
		}

		if (data.atLeastOneEmptyParameter()) {
			System.out.println("Please fill in all fields.");
			return Response.status(Status.LENGTH_REQUIRED).build();
		}

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id doesn't exist").build();
		}

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		}

		if (user.getString("state").equals("DISABLED")) {
			System.out.println("The user with the given token is disabled.");
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("User with id: " + user.getString("username") + " is disabled.").build();
		}

		List<String> events = new ArrayList<String>();
		String e = user.getString("events");

		if (!e.equals(""))
			events = g.fromJson(e, List.class);

		events.add(data.eventId);

		user = Entity.newBuilder(userKey).set("username", token.getString("username"))
				.set("password", user.getString("password")).set("email", user.getString("email"))
				.set("profileType", user.getString("profileType")).set("landLine", user.getString("landLine"))
				.set("mobile", user.getString("mobile")).set("address", user.getString("address"))
				.set("secondAddress", user.getString("secondAddress")).set("postal", user.getString("postal"))
				.set("profilePic", user.getString("profilePic")).set("tags", user.getString("tags"))
				.set("events", g.toJson(events)).set("role", user.getString("role"))
				.set("state", user.getString("state")).build();

		datastore.update(user);

		return Response.ok("Joined successfully.").cookie(cookie).build();
	}

	@POST
	@Path("/addEventWeb")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEventWeb(@CookieParam("Token") NewCookie cookie, EventData data) {

		LOG.fine("Attempt to register event: " + data.name);

		if (cookie.getName().equals("")) {
			System.out.println("You need to be logged in to execute this operation.");
			return Response.status(Status.UNAUTHORIZED).build();
		}

		if (data.atLeastOneEmptyParameter()) {
			System.out.println("Please fill in all fields.");
			return Response.status(Status.LENGTH_REQUIRED).build();
		}

		if (Integer.parseInt(data.minParticipants) <= 0
				|| Integer.parseInt(data.maxParticipants) < Integer.parseInt(data.minParticipants)) {
			System.out.println("Number of participants is incorrect");
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}

		if (!data.verifyDate()) {
			System.out.println("Date is not valid");
			return Response.status(Status.FORBIDDEN).build();
		}

		if (!data.isHourValid()) {
			System.out.println("Hour is not valid");
			return Response.status(Status.EXPECTATION_FAILED).build();
		}

		Transaction txn = datastore.newTransaction();
		try {
			Key mapKey = datastore.newKeyFactory().setKind("Event").newKey(data.name);
			Entity event = txn.get(mapKey);
			if (event != null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("The event with the given title already exists.")
						.build();
			} else {
				String coordinates = data.lat + " " + data.lon;
				event = Entity.newBuilder(mapKey).set("name", data.name).set("description", data.description)
						.set("minParticipants", data.minParticipants).set("maxParticipants", data.maxParticipants)
						.set("hour", data.time).set("coordinates", coordinates).set("temporary", data.durability)
						.set("initial_date", data.initialDate).set("ending_date", data.endingDate)
						.set("tags", g.toJson(data.tags)).build();

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

	@GET
	@Path("/listUserEvents")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listUserEvents(@QueryParam("tokenId") String tokenId) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId + " doesn't exist").build();

		/*
		 * if (!t.validToken(tokenKey)) return
		 * Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId +
		 * " has expired. Please login again to continue using the application").build()
		 * ;
		 */

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		}

		/*
		 * if (currentUser.getString("state").equals("DISABLED")) return
		 * Response.status(Status.BAD_REQUEST) .entity("User with id: " +
		 * currentUser.getString("username") + " is disabled.").build();
		 */

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Response.ok(g.toJson(events)).build();

		/*ObjectMapper mapper = new ObjectMapper();
		List<String> userEventsUnprocessed = new ArrayList<String>();
		List<String> userEvents = new ArrayList<String>();
		Type type = new TypeToken<String>() {
		}.getType();
		try {
			userEventsUnprocessed = Arrays.asList(mapper.readValue(currentUser.getString("events"), String[].class));
			userEventsUnprocessed.forEach(x -> userEvents.add(g.fromJson(x, type)));
			while (eventsQuery.hasNext()) {
				Entity e = eventsQuery.next();
				if (userEvents.contains(e.getString("name"))) {
					String event = g.toJson(e.getProperties().values());
					events.add(event);
				}
			}
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Response.ok(g.toJson(events)).build();*/

	}

	@GET
	@Path("/listUserEventsWeb")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listUserEventsWeb(@CookieParam("Token") NewCookie cookie) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (cookie.getName().equals("")) {
			System.out.println("You need to be logged in to execute this operation.");
			return Response.status(Status.UNAUTHORIZED).build();
		}
		/*if (!t.validToken(tokenKey))
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId
					+ " has expired. Please login again to continue using the application").build();*/

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		}

	/*	if (currentUser.getString("state").equals("DISABLED"))
			return Response.status(Status.BAD_REQUEST)
					.entity("User with id: " + currentUser.getString("username") + " is disabled.").build();*/

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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Response.ok(g.toJson(events)).build();

		/*Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Event")
				.build();

		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
		List<String> userEventsUnprocessed = new ArrayList<String>();
		List<String> userEvents = new ArrayList<String>();
		Type type = new TypeToken<String>() {}.getType();
		try {
			userEventsUnprocessed = Arrays.asList(mapper.readValue(currentUser.getString("events"), String[].class));
			userEventsUnprocessed.forEach(x -> userEvents.add(g.fromJson(x, type)));
			while (eventsQuery.hasNext()){
				Entity e = eventsQuery.next();
				if(userEvents.contains(e.getString("name"))) {
					String event = g.toJson(e.getProperties().values());
					events.add(event);
				}
			}
		} catch (JsonProcessingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Response.ok(g.toJson(events)).build();*/

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
