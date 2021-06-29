package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
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
import pt.unl.fct.di.apdc.sharencare.util.EventData;
import pt.unl.fct.di.apdc.sharencare.util.ListEventsData;
import pt.unl.fct.di.apdc.sharencare.util.TokenData;

@Path("/event")
public class EventResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final Gson g = new Gson();
	AuthTokenResource t = new AuthTokenResource();
    
    @POST
    @Path("/registerEvent")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerEvent(EventData data) {
    	LOG.fine("Attempt to register event: " + data.name);
    	
    	System.out.println("estoua chegar");
    	
    	
    	if( Integer.parseInt(data.minParticipants) <= 0 || Integer.parseInt(data.maxParticipants) < Integer.parseInt(data.minParticipants)) {
    		System.out.println("Number of participants is incorrect");
    		return Response.status(Status.NOT_ACCEPTABLE).build();
    	}
    	
    	if(!data.VerifyDate()) {
    		System.out.println("Date is not valid");
    		return Response.status(Status.NOT_ACCEPTABLE).build();
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
                        .set("coordinates", coordinates)
                        .set("temporary", data.temporary)
                        .set("date", data.date)
                        .set("tags", g.toJson(data.tags))
                        .build();
                
                


                txn.add(event);
                txn.commit();
                return Response.ok("Track " + data.name + " registered.").build();
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }
	
//TODO	@Produces
	@GET
	@Path("/getAllEvents/{tokenId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getAllEvents(@PathParam("tokenId") String tokenId) {
		
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
		List<Entity> events = new ArrayList<>();	
			while (eventsQuery.hasNext())	
				events.add(eventsQuery.next());	
		
		
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
	@Path("/listUserEvents/{tokenId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUserEvents(@PathParam("tokenId") String tokenId) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId + " doesn't exist")
					.build();

		if (!t.validToken(tokenKey))
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId
					+ " has expired. Please login again to continue using the application").build();

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser.getString("state").equals("DISABLED"))
			return Response.status(Status.BAD_REQUEST)
					.entity("User with id: " + currentUser.getString("username") + " is disabled.").build();

		/*
		 * END OF VERIFICATIONS
		 */

		return Response.ok(g.toJson(currentUser.getString("events"))).build();

	}

}
