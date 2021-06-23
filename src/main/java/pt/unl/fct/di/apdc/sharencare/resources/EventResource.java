package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

import pt.unl.fct.di.apdc.sharencare.util.EventData;

@Path("/event")
public class EventResource {
	
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    
    @POST
    @Path("/registerEvent")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerEvent(EventData data) {
    	LOG.fine("Attempt to register event: " + data.name);
    	
    	
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
                        .set("tag", data.tag)
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

}
