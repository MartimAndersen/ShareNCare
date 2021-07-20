package pt.unl.fct.di.apdc.sharencare.resources;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.OrderBy;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.repackaged.com.google.gson.reflect.TypeToken;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.EntityQuery.Builder;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.JoinEventData;
import pt.unl.fct.di.apdc.sharencare.util.LeaveEventData;
import pt.unl.fct.di.apdc.sharencare.util.RatingData;
import pt.unl.fct.di.apdc.sharencare.util.ReviewData;
import pt.unl.fct.di.apdc.sharencare.util.AbandonEventData;
import pt.unl.fct.di.apdc.sharencare.util.EditEventData;
import pt.unl.fct.di.apdc.sharencare.util.EventData;
import pt.unl.fct.di.apdc.sharencare.util.FilterData;
import pt.unl.fct.di.apdc.sharencare.util.FinishEvent;
import pt.unl.fct.di.apdc.sharencare.util.GetEventsByLocationData;
import pt.unl.fct.di.apdc.sharencare.resources.RakingUserResource;

@Path("/event")
public class EventResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	final ObjectMapper objectMapper = new ObjectMapper();
	private final RakingUserResource raking = new RakingUserResource();

	public String[] TAGS = { "animals", "environment", "children", "elderly", "supplies", "homeless" };// , sports,
																										// summer,
																										// holidays,
																										// turism};

	@SuppressWarnings("unchecked")
	@POST
	@Path("/registerEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerEvent(@CookieParam("Token") NewCookie cookie, EventData data) throws ParseException {

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

		String username = token.getString("username");
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN).entity("User with username: " + username + " doesn't exist")
					.build();
		
		if(!user.getString("role").equals("INSTITUTION")) {
			return Response.status(Status.METHOD_NOT_ALLOWED).build();
		}

		/*
		 * END OF VERIFICATIONS
		 */

		if (data.atLeastOneEmptyParameter())
			return Response.status(Status.LENGTH_REQUIRED).build();

		if (!data.validParticipants())
			return Response.status(Status.NOT_ACCEPTABLE).build();

		if (!data.verifyDate())
			return Response.status(Status.FORBIDDEN).build();

		if (!data.futureDate())
			return Response.status(Status.BAD_REQUEST).build();

		if (!data.dateOrder())
			return Response.status(Status.PRECONDITION_FAILED).build();

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
						.set("points", points).set("tags", g.toJson(data.tags)).set("rating", g.toJson(l))
						.set("ended", "false").build();

				txn.add(event);

				List<String> events = new ArrayList<String>();
				String e = user.getString("events");

				if (!e.equals(""))
					events = g.fromJson(e, List.class);

				events.add(data.name);

				user = Entity.newBuilder(userKey).set("nif", token.getString("username"))
						.set("username", user.getString("username")).set("password", user.getString("password"))
						.set("email", user.getString("email")).set("landLine", user.getString("landLine"))
						.set("mobile", user.getString("mobile")).set("address", user.getString("address"))
						.set("zipCode", user.getString("zipCode")).set("events", g.toJson(events))
						.set("website", user.getString("website")).set("instagram", user.getString("instagram"))
						.set("twitter", user.getString("twitter")).set("facebook", user.getString("facebook"))
						.set("youtube", user.getString("youtube")).set("bio", user.getString("bio"))
						.set("fax", user.getString("fax")).set("role", user.getString("role"))
						.set("state", user.getString("state")).set("coordinates", user.getString("coordinates")).build();

				txn.update(user);

				txn.commit();
				return Response.ok("Event " + data.name + " registered.").cookie(cookie).build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@DELETE
	@Path("/deleteEvent")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteEvent(@CookieParam("Token") NewCookie cookie, FinishEvent data) {

		String eventId = data.name;

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();
		
		

		Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(eventId);
		Entity event = datastore.get(eventKey);

		if (event == null)
			return Response.status(Status.BAD_REQUEST).entity("Event with id: " + eventId + " doesn't exist").build();
		
		String username = token.getString("username");
		Key institutionKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity institution = datastore.get(institutionKey);

		if (institution == null)
			return Response.status(Status.FORBIDDEN).entity("User with username: " + username + " doesn't exist")
					.build();
		
		if(institution.getString("role").equals("INSTITUTION")) {
			return Response.status(Status.CONFLICT).build();
		}


		String m = event.getString("members");

		Type stringList = new TypeToken<ArrayList<String>>() {
		}.getType();
		List<String> members = g.fromJson(m, stringList);

		datastore.delete(eventKey);
		
		String eventsInstitution = institution.getString("events");

		List<String> institutionEvents = g.fromJson(eventsInstitution, stringList);
		institutionEvents.remove(eventId);
		institution = Entity.newBuilder(institutionKey).set("nif", token.getString("username"))
				.set("username", institution.getString("username")).set("password", institution.getString("password"))
				.set("email", institution.getString("email")).set("landLine", institution.getString("landLine"))
				.set("mobile", institution.getString("mobile")).set("address", institution.getString("address"))
				.set("zipCode", institution.getString("zipCode")).set("events", g.toJson(institutionEvents))
				.set("website", institution.getString("website")).set("instagram", institution.getString("instagram"))
				.set("twitter", institution.getString("twitter")).set("facebook", institution.getString("facebook"))
				.set("youtube", institution.getString("youtube")).set("bio", institution.getString("bio"))
				.set("fax", institution.getString("fax")).set("role", institution.getString("role"))
				.set("state", institution.getString("state")).set("coordinates", institution.getString("coordinates")).build();
		
		datastore.update(institution);
		
		for (String member : members) {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(member);
			Entity user = datastore.get(userKey);
			String ev = user.getString("events");

			List<String> userEvents = g.fromJson(ev, stringList);
			userEvents.remove(eventId);
			user = Entity.newBuilder(userKey).set("username", member).set("password", user.getString("password"))
					.set("email", user.getString("email")).set("bio", user.getString("bio"))
					.set("profileType", user.getString("profileType")).set("landLine", user.getString("landLine"))
					.set("mobile", user.getString("mobile")).set("address", user.getString("address"))
					.set("secondAddress", user.getString("secondAddress")).set("zipCode", user.getString("zipCode"))
					.set("tags", user.getString("tags")).set("events", g.toJson(userEvents))
					.set("points", user.getString("points")).set("role", user.getString("role"))
					.set("state", user.getString("state")).set("my_tracks", user.getString("my_tracks")).build(); 

			datastore.update(user);

		}

		return Response.ok("Event deleted.").build();

	}

	@POST
	@Path("/removeUserFromEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteUserFromEvent(@CookieParam("Token") NewCookie cookie, AbandonEventData data) {

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN).entity("User with username: " + data.username + " doesn't exist")
					.build();
		
		Key backOfficeUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity backofficeUser = datastore.get(backOfficeUserKey);
		
		if (backofficeUser == null)
			return Response.status(Status.BAD_REQUEST).entity("BackOffice given doesn't exist").build();
		
		if(!backofficeUser.getString("role").equals("GA")) {
			return Response.status(Status.FORBIDDEN).build();
		}
		

		for (String eventId : data.eventsId) {

			Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(eventId);
			Entity event = datastore.get(eventKey);

			if (event == null)
				return Response.status(Status.EXPECTATION_FAILED).entity("Event with id: " + eventId + " doesn't exist")
						.build();

			String m = event.getString("members");
			String e = user.getString("events");

			Type stringList = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> members = g.fromJson(m, stringList);
			List<String> events = g.fromJson(e, stringList);

			if (!members.contains(data.username) || !events.contains(eventId))
				return Response.status(Status.CONFLICT).entity("User is not a member of the event").build();

			members.remove(data.username);
			events.remove(eventId);

			user = Entity.newBuilder(userKey).set("username", data.username).set("password", user.getString("password"))
					.set("email", user.getString("email")).set("bio", user.getString("bio"))
					.set("profileType", user.getString("profileType")).set("landLine", user.getString("landLine"))
					.set("mobile", user.getString("mobile")).set("address", user.getString("address"))
					.set("secondAddress", user.getString("secondAddress")).set("zipCode", user.getString("zipCode"))
					.set("tags", user.getString("tags")).set("events", g.toJson(events))
					.set("points", user.getString("points")).set("role", user.getString("role"))
					.set("state", user.getString("state")).set("my_tracks", user.getString("my_tracks")).build();

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
			raking.takePointsQuit(data.username);
		}
		datastore.update(user);
		return Response.ok("User removed").build();

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
		
		if(!user.getString("role").equals("USER")) {
			return Response.status(Status.METHOD_NOT_ALLOWED).build();
		}
		
		

		/*
		 * END OF VERIFICATIONS
		 */

		Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(data.eventId);
		Entity event = datastore.get(eventKey);

		if (event == null)
			return Response.status(Status.BAD_REQUEST).entity("Event with id: " + data.eventId + " doesn't exist")
					.build();

		String m = event.getString("members");
		String e = user.getString("events");

		Type stringList = new TypeToken<ArrayList<String>>() {
		}.getType();
		List<String> members = g.fromJson(m, stringList);
		List<String> events = g.fromJson(e, stringList);

		if (members.size() == Integer.parseInt(event.getString("maxParticipants")))
			return Response.status(Status.EXPECTATION_FAILED).entity("Event has max participants").build();

		if (members.contains(user.getString("username")) || events.contains(data.eventId))
			return Response.status(Status.CONFLICT).entity("User is already a member of the event").build();

		members.add(user.getString("username"));
		events.add(data.eventId);

		user = Entity.newBuilder(userKey).set("username", user.getString("username"))
				.set("password", user.getString("password")).set("email", user.getString("email"))
				.set("bio", user.getString("bio")).set("profileType", user.getString("profileType"))
				.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
				.set("address", user.getString("address")).set("secondAddress", user.getString("secondAddress"))
				.set("zipCode", user.getString("zipCode")).set("tags", user.getString("tags"))
				.set("events", g.toJson(events)).set("points", user.getString("points"))
				.set("role", user.getString("role")).set("state", user.getString("state")).set("my_tracks", user.getString("my_tracks")).build();

		event = Entity.newBuilder(eventKey).set("name", event.getString("name"))
				.set("description", event.getString("description"))
				.set("minParticipants", event.getString("minParticipants"))
				.set("maxParticipants", event.getString("maxParticipants")).set("time", event.getString("time"))
				.set("coordinates", event.getString("coordinates")).set("durability", event.getString("durability"))
				.set("institutionName", event.getString("institutionName"))
				.set("initial_date", event.getString("initial_date")).set("ending_date", event.getString("ending_date"))
				.set("members", g.toJson(members)).set("points", event.getString("points"))
				.set("tags", event.getString("tags")).set("rating", event.getString("rating"))
				.set("ended", event.getString("ended")).build();

		datastore.update(user);
		datastore.update(event);

		return Response.ok("Joined successfully.").cookie(cookie).build();
	}
	
	@POST
	@Path("/leaveEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response leaveEvent(@CookieParam("Token") NewCookie cookie, LeaveEventData data) {

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
		
		if(user.getString("role").equals("USER")) {
			return Response.status(Status.CONFLICT).build();
		}
		
		

		/*
		 * END OF VERIFICATIONS
		 */

		Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(data.eventId);
		Entity event = datastore.get(eventKey);

		if (event == null)
			return Response.status(Status.BAD_REQUEST).entity("Event with id: " + data.eventId + " doesn't exist")
					.build();

		String m = event.getString("members");
		String e = user.getString("events");

		Type stringList = new TypeToken<ArrayList<String>>() {
		}.getType();
		List<String> members = g.fromJson(m, stringList);
		List<String> events = g.fromJson(e, stringList);

		if (members.size() == Integer.parseInt(event.getString("maxParticipants")))
			return Response.status(Status.EXPECTATION_FAILED).entity("Event has max participants").build();

		if (members.contains(user.getString("username")) || events.contains(data.eventId))
			return Response.status(Status.CONFLICT).entity("User is already a member of the event").build();
		
		if(is3DaysBefore(event.getString("initial_date"))) {
			RakingUserResource r = new RakingUserResource();
			r.takePointsQuit(token.getString("username"));
		}
		

		members.remove(user.getString("username"));
		events.remove(data.eventId);

		user = Entity.newBuilder(userKey).set("username", user.getString("username"))
				.set("password", user.getString("password")).set("email", user.getString("email"))
				.set("bio", user.getString("bio")).set("profileType", user.getString("profileType"))
				.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
				.set("address", user.getString("address")).set("secondAddress", user.getString("secondAddress"))
				.set("zipCode", user.getString("zipCode")).set("tags", user.getString("tags"))
				.set("events", g.toJson(events)).set("points", user.getString("points"))
				.set("role", user.getString("role")).set("state", user.getString("state")).set("my_tracks", user.getString("my_tracks")).build();

		event = Entity.newBuilder(eventKey).set("name", event.getString("name"))
				.set("description", event.getString("description"))
				.set("minParticipants", event.getString("minParticipants"))
				.set("maxParticipants", event.getString("maxParticipants")).set("time", event.getString("time"))
				.set("coordinates", event.getString("coordinates")).set("durability", event.getString("durability"))
				.set("institutionName", event.getString("institutionName"))
				.set("initial_date", event.getString("initial_date")).set("ending_date", event.getString("ending_date"))
				.set("members", g.toJson(members)).set("points", event.getString("points"))
				.set("tags", event.getString("tags")).set("rating", event.getString("rating"))
				.set("ended", event.getString("ended")).build();

		datastore.update(user);
		datastore.update(event);

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
				.set("state", user.getString("state")).set("coordinates", user.getString("coordinates")).build();

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

		if (data.ratingIsValid()) {
			return Response.status(Status.FORBIDDEN).build();
		}

		Transaction txn = datastore.newTransaction();

		try {
			Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(data.eventName);
			Entity event = txn.get(eventKey);

			String commentList = event.getString("rating");

			Type rating = new TypeToken<ArrayList<RatingData>>() {
			}.getType();
			List<RatingData> ratings = new Gson().fromJson(commentList, rating);

			List<RatingData> newRatings = new ArrayList<RatingData>();

			for (int i = 0; i < ratings.size(); i++)
				newRatings.add(ratings.get(i));

			newRatings.add(data);

			event = Entity.newBuilder(eventKey).set("name", event.getString("name"))
					.set("description", event.getString("description"))
					.set("minParticipants", event.getString("minParticipants"))
					.set("maxParticipants", event.getString("maxParticipants")).set("time", event.getString("time"))
					.set("coordinates", event.getString("coordinates")).set("durability", event.getString("durability"))
					.set("institutionName", event.getString("institutionName"))
					.set("initial_date", event.getString("initial_date"))
					.set("ending_date", event.getString("ending_date")).set("members", event.getString("members"))
					.set("points", event.getString("points")).set("tags", event.getString("tags"))
					.set("rating", g.toJson(newRatings)).set("ended", event.getString("ended")).build();

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
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist") 
					.build();

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
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

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
	@Path("/listInstitutionEvents")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listInstitutionEvents(@CookieParam("Token") NewCookie cookie) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser == null)
			return Response.status(Status.FORBIDDEN)
					.entity("Institution with username: " + token.getString("username") + " doesn't exist").build();

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
	@Path("/listEventPreferences")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listEventPreferences(@CookieParam("Token") NewCookie cookie) {
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

		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").build();

		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
		List<Integer> userTags = new ArrayList<Integer>();
		List<Integer> eventTags = new ArrayList<Integer>();

		try {
			userTags = Arrays.asList(mapper.readValue(user.getString("tags"), Integer[].class));
			while (eventsQuery.hasNext()) {
				Entity e = eventsQuery.next();
				eventTags = Arrays.asList(mapper.readValue(e.getString("tags"), Integer[].class));
				if (containsAny(userTags, eventTags)) {
					String event = g.toJson(e.getProperties().values());
					events.add(event);
				}
			}
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}

		return Response.ok(g.toJson(events)).cookie(cookie).build();
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/finishEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response finishEvent(@CookieParam("Token") NewCookie cookie, FinishEvent data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id doesn't exist").build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN)
					.entity("Institution with username: " + token.getString("username") + " doesn't exist").build();

		if (user.getString("state").equals("DISABLED")) {
			System.out.println("The Institution with the given token is disabled.");
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("Institution with id: " + user.getString("username") + " is disabled.").build();
		}

		/*
		 * END OF VERIFICATIONS
		 */

		Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(data.name);
		Entity event = datastore.get(eventKey);

		if (event == null)
			return Response.status(Status.BAD_REQUEST).entity("Event with id: " + data.name + " doesn't exist").build();

		String m = event.getString("members");

		Type stringList = new TypeToken<ArrayList<String>>() {
		}.getType();
		List<String> members = g.fromJson(m, stringList);

		for (String member : members) {

			raking.addPointsEvents(member);
		}
		return Response.ok("Event finished").cookie(cookie).build();

	}

	@GET
	@Path("/filterEvents")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response filterEvents(@QueryParam("filterString") String filterString) {

		FilterData data = g.fromJson(filterString, FilterData.class);
		List<PropertyFilter> filters = data.getFilter();
		PropertyFilter[] subFilter = new PropertyFilter[filters.size()];
		PropertyFilter first = filters.get(0);

		Builder query = Query.newEntityQueryBuilder().setKind("Event");

		if (filters.size() == 1)
			query.setFilter(first);

		else {
			for (int i = 1; i < filters.size(); i++)
				subFilter[i] = filters.get(i);

			query.setFilter(CompositeFilter.and(first, subFilter));
		}

		if (data.popularity.equals("Most Popular"))
			query = query.setOrderBy(OrderBy.desc("points"));
		if (data.popularity.equals("Least Popular"))
			query = query.setOrderBy(OrderBy.asc("points"));

		Query<Entity> q = query.build();

		QueryResults<Entity> eventsQuery = datastore.run(q);
		List<String> events = new ArrayList<>();

		while (eventsQuery.hasNext()) {
			Entity e = eventsQuery.next();
			String event = g.toJson(e.getProperties().values());
			events.add(event);
		}
		return Response.ok(g.toJson(events)).build();
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

	
	@POST
	@Path("/getEventsByLocation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getEventByLocation(GetEventsByLocationData data) {
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").build();

		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();
		while (eventsQuery.hasNext()) {
			Entity e = eventsQuery.next();
			if(data.coordinates.contains(e.getString("coordinates"))) {
				String event = g.toJson(eventsQuery.next().getProperties().values());
				events.add(event);
			}
		}

		return Response.ok(g.toJson(events)).build();

	}

	private boolean containsAny(List<Integer> list1, List<Integer> list2) {
		for (int i = 0; i < list2.size(); i++)
			if (list1.contains(list2.get(i)))
				return true;
		return false;
	}

	@GET
	@Path("/checkEventDate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkEventDate() {

		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").build();

		QueryResults<Entity> eventsQuery = datastore.run(query);

		while (eventsQuery.hasNext()) {
			Entity e = eventsQuery.next();
			if (e.getString("ended").equals("false")) {
				String date = e.getString("ending_date");
				if (hasEnded(date)) {
					Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(e.getString("name"));
					Entity event = datastore.get(eventKey);
					event = Entity.newBuilder(eventKey).set("name", event.getString("name"))
							.set("description", event.getString("description"))
							.set("minParticipants", event.getString("minParticipants"))
							.set("maxParticipants", event.getString("maxParticipants"))
							.set("time", event.getString("time")).set("coordinates", event.getString("coordinates"))
							.set("durability", event.getString("durability"))
							.set("institutionName", event.getString("institutionName"))
							.set("initial_date", event.getString("initial_date"))
							.set("ending_date", event.getString("ending_date"))
							.set("members", event.getString("members")).set("points", event.getString("points"))
							.set("tags", event.getString("tags")).set("rating", event.getString("rating"))
							.set("ended", "true").build();

					datastore.update(event);

				}
			}
		}
		return Response.status(Status.OK).build();

	}

	private boolean hasEnded(String date) {
		String currDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		String[] curr = currDate.split("/");
		String[] datefinal = date.split("/");

		if (Integer.parseInt(curr[2]) >= Integer.parseInt(datefinal[2])) {

			if (Integer.parseInt(curr[1]) >= Integer.parseInt(datefinal[1])) {

				if (Integer.parseInt(curr[0]) >= Integer.parseInt(datefinal[0])) {

					return true;

				}
			}
		}

		return false;
	}
	
	private boolean is3DaysBefore(String date) {
		String localDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		String[] curr = localDate.split("/");
		String[] datefinal = date.split("/");
		
		Date currDate = new GregorianCalendar(Integer.parseInt(curr[2]), Integer.parseInt(curr[1]) - 1, Integer.parseInt(curr[0])).getTime();
		Date initialDate = new GregorianCalendar(Integer.parseInt(datefinal[2]), Integer.parseInt(datefinal[1]) - 1, Integer.parseInt(datefinal[0])).getTime();
		
		long currMillis = currDate.getTime();
		long initialMillis = initialDate.getTime();
		
		long dateDifference = Math.abs(initialMillis - currMillis);
		long days = 259200000;
		
		if(dateDifference <= days) {
			return true;
		}

		return false;
	}
	


	@PUT
	@Path("/editEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editEvent(@CookieParam("Token") NewCookie cookie, EditEventData data  ) throws ParseException {
		
		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id doesn't exist").build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN)
					.entity("Institution with username: " + token.getString("username") + " doesn't exist").build();

		if (user.getString("state").equals("DISABLED")) {
			System.out.println("The Institution with the given token is disabled.");
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("Institution with id: " + user.getString("username") + " is disabled.").build();
		}

		/*
		 * END OF VERIFICATIONS
		 */
		String description = data.description;
		String durability = data.durability;
		String endingDate = data.endingDate;
		String initialDate = data.initialDate;

		String maxParticipants = data.maxParticipants;
		String minParticipants = data.minParticipants;
		List<Integer> tags = data.tags;
		String time = data.time;
		


		Key eventKey = datastore.newKeyFactory().setKind("Event").newKey(data.name);
		Entity event = datastore.get(eventKey);

		if (data.description.equals("")) {
			description = event.getString("description");
		} 
		
		if (data.durability.equals("")) {
			durability= event.getString("durability");
		}
		if (data.endingDate.equals("")) {
			endingDate = event.getString("ending_date");
		}else {
			if (!data.verifyDate(endingDate)) {
				return Response.status(Status.FORBIDDEN).build();
			}
		}
		if (data.initialDate.equals("")) {
			initialDate = event.getString("initial_date");
		} else {
			if (!data.verifyDate(initialDate)) {
				return Response.status(Status.FORBIDDEN).build();
			}
		}
		String coordinates = data.lat + " " + data.lon;
		
		if (data.lat == null || data.lat == null) {
			coordinates =event.getString("coordinates");
		}
		if (data.maxParticipants.equals("")) {
			maxParticipants = event.getString("maxParticipants");
		}
		if (data.minParticipants.equals("")) {
			minParticipants = event.getString("minParticipants");
		}
		if (!data.validParticipants(minParticipants, maxParticipants)) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
			}
		
		String tags1 = g.toJson(tags);
		if (data.tags.isEmpty()) {
			tags1 = event.getString("tags");
		}
		if (data.time.equals("")) {
			time = event.getString("time");
		}



		if (!data.futureDate(initialDate))
			return Response.status(Status.PRECONDITION_FAILED).build();

		if (!data.dateOrder(initialDate, endingDate))
			return Response.status(Status.METHOD_NOT_ALLOWED).build();

		if (!data.isHourValid(time))
			return Response.status(Status.EXPECTATION_FAILED).build();




		event = Entity.newBuilder(eventKey).set("name", event.getString("name"))
				.set("description", description)
				.set("minParticipants", minParticipants)
				.set("maxParticipants", maxParticipants)
				.set("time", time).set("coordinates", coordinates)
				.set("durability", durability)
				.set("institutionName", event.getString("institutionName"))
				.set("initial_date", initialDate)
				.set("ending_date", endingDate)
				.set("members", event.getString("members")).set("points", event.getString("points"))
				.set("tags", tags1).set("rating", event.getString("rating"))
				.set("ended", event.getString("ended")).build();


		datastore.update(event);

		return Response.ok("Properties changed").cookie(cookie).build();
	}
		
		
	

}
