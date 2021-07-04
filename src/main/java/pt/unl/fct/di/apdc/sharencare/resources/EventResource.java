package pt.unl.fct.di.apdc.sharencare.resources;

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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;


import pt.unl.fct.di.apdc.sharencare.util.AddEventData;
import pt.unl.fct.di.apdc.sharencare.util.AddEventDataWeb;
import pt.unl.fct.di.apdc.sharencare.util.EventData;

@Path("/event")
public class EventResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final Gson g = new Gson();
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
    	
    	
    	if( Integer.parseInt(data.minParticipants) <= 0 || Integer.parseInt(data.maxParticipants) < Integer.parseInt(data.minParticipants)) {
    		System.out.println("Number of participants is incorrect");
    		return Response.status(Status.NOT_ACCEPTABLE).build();
    	}
    	
    	if(!data.verifyDate()) {
    		System.out.println("Date is not valid");
    		return Response.status(Status.FORBIDDEN).build();
    	}
    	
    	if(!data.isHourValid()) {
    		System.out.println("Hour is not valid");
    		return Response.status(Status.EXPECTATION_FAILED).build();
    	}


        Transaction txn = datastore.newTransaction();
        try {
            Key mapKey = datastore.newKeyFactory().setKind("Event").newKey(data.name);
            Entity event = txn.get(mapKey);
            if (event != null) {
                txn.rollback();
                return Response.status(Status.CONFLICT).entity("The event with the given title already exists.").build();
            } else {
            	String coordinates = data.lat + " " + data.lon;
                event = Entity.newBuilder(mapKey)
                		.set("name", data.name)
                        .set("description", data.description)
                        .set("minParticipants", data.minParticipants)
                        .set("maxParticipants", data.maxParticipants)
                        .set("hour", data.time)
                        .set("coordinates", coordinates)
                        .set("temporary", data.temporary)
                        .set("initial_date", data.initialDate)
                        .set("ending_date", data.endingDate)
                        .set("tags", g.toJson(data.tags))
                        .build();


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
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + tokenId + " doesn't exist")
					.build();
		}
		
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Event")
				.build();
		
		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();
			while (eventsQuery.hasNext())	{
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
		

		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Event")
				.build();
		
		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();
			while (eventsQuery.hasNext())	{
				String event = g.toJson(eventsQuery.next().getProperties().values());
				events.add(event);	
			}
		
		
		return Response.ok(g.toJson(events)).build();
		
	}
		
	@POST
	@Path("/addEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEvent(AddEventData data) {
		
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
		
		List<String> events = new ArrayList<String>();
		String e = user.getString("events");
		
		if(!e.equals(""))
			events = Arrays.asList(g.fromJson(e, String[].class));
			
		events.add(data.eventId);
		
		user = Entity.newBuilder(userKey)
				.set("username", token.getString("username"))
				.set("password", user.getString("password"))
				.set("confirmation", user.getString("password"))
				.set("email", user.getString("email"))
				.set("publicProfile", user.getBoolean("publicProfile"))
				.set("landLine", user.getString("landLine"))
				.set("mobile", user.getString("mobile"))
				.set("address", user.getString("address"))
				.set("secondAddress", user.getString("secondAddress"))
				.set("postal", user.getString("postal"))
				.set("tags",user.getString("tags"))
				//.set("profilePic", profilePic)
				.set("role", user.getString("role"))
				.set("state", user.getString("state"))
				.set("events", g.toJson(events))
				.build();

		datastore.update(user);

		return Response.ok("Properties changed").build();
	}
	
	@POST
	@Path("/addEventWeb")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEventWeb(@CookieParam("Token") NewCookie cookie,EventData data) {

		LOG.fine("Attempt to register event: " + data.name);

		if (cookie.getName().equals("")) {
			System.out.println("You need to be logged in to execute this operation.");
			return Response.status(Status.UNAUTHORIZED).build();
		}

		if (data.atLeastOneEmptyParameter()) {
			System.out.println("Please fill in all fields.");
			return Response.status(Status.LENGTH_REQUIRED).build();
		}


		if( Integer.parseInt(data.minParticipants) <= 0 || Integer.parseInt(data.maxParticipants) < Integer.parseInt(data.minParticipants)) {
			System.out.println("Number of participants is incorrect");
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}

		if(!data.verifyDate()) {
			System.out.println("Date is not valid");
			return Response.status(Status.FORBIDDEN).build();
		}

		if(!data.isHourValid()) {
			System.out.println("Hour is not valid");
			return Response.status(Status.EXPECTATION_FAILED).build();
		}


		Transaction txn = datastore.newTransaction();
		try {
			Key mapKey = datastore.newKeyFactory().setKind("Event").newKey(data.name);
			Entity event = txn.get(mapKey);
			if (event != null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("The event with the given title already exists.").build();
			} else {
				String coordinates = data.lat + " " + data.lon;
				event = Entity.newBuilder(mapKey)
						.set("name", data.name)
						.set("description", data.description)
						.set("minParticipants", data.minParticipants)
						.set("maxParticipants", data.maxParticipants)
						.set("hour", data.time)
						.set("coordinates", coordinates)
						.set("temporary", data.temporary)
						.set("initial_date", data.initialDate)
						.set("ending_date", data.endingDate)
						.set("tags", g.toJson(data.tags))
						.build();


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
    
    @SuppressWarnings({ "unchecked" })
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
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId + " doesn't exist")
					.build();

		/*if (!t.validToken(tokenKey))
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId
					+ " has expired. Please login again to continue using the application").build();*/

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser.getString("state").equals("DISABLED"))
			return Response.status(Status.BAD_REQUEST)
					.entity("User with id: " + currentUser.getString("username") + " is disabled.").build();

		/*
		 * END OF VERIFICATIONS
		 */
		
		Query<Entity> query = Query.newEntityQueryBuilder()
				.setKind("Event")
				.build();
		
		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> events = new ArrayList<>();
		List<String> userEvents = g.fromJson(currentUser.getString("events"), List.class);
			while (eventsQuery.hasNext()){
				Entity e = eventsQuery.next();
				if(userEvents.contains(e.getString("name"))) {
					String event = g.toJson(e.getProperties().values());
					events.add(event);
				}
			}
		
		/*com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query("Event");

		DatastoreService data = DatastoreServiceFactory.getDatastoreService();		
		PreparedQuery pq = data.prepare(query);
		
		List<com.google.appengine.api.datastore.Entity> u = pq.asList(FetchOptions.Builder.withDefaults());
		List<String> events = new ArrayList<>();
		List<String> userEvents = g.fromJson(currentUser.getString("events"), List.class);
			for(int i = 0; i < u.size(); i++){
				com.google.appengine.api.datastore.Entity e;
				try {
					e = data.get(u.get(i).getKey());
					if(userEvents.contains(e.getProperty("name").toString()))
						events.add(e.toString());
				//}
				} catch (EntityNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}*/

		return Response.ok(g.toJson(events)).build();

	}

}
