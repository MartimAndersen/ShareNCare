package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.*;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import org.apache.commons.codec.digest.DigestUtils;

import pt.unl.fct.di.apdc.sharencare.util.RegisterInstitutionData;
import pt.unl.fct.di.apdc.sharencare.util.PointsData;
import pt.unl.fct.di.apdc.sharencare.util.RegisterData;
import pt.unl.fct.di.apdc.sharencare.util.RegisterDataGA;


@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
    private final Gson gson = new Gson();

    public RegisterResource() {
        Key userKey = datastore.newKeyFactory().setKind("User").newKey("superUser");
        Entity user = datastore.get(userKey);
        
        
        if(user == null) {
            user = Entity.newBuilder(userKey)
                    .set("username", "superUser")
                    .set("email", "superUser@gmail.com")
                    .set("password", DigestUtils.sha512Hex("password"))
                    .set("profileType", "private")
                    .set("landLine", "")
                    .set("mobile", "")
                    .set("address", "")
                    .set("secondAddress", "")
                    .set("zipCode", "")
                    .set("bio", "")
                    .set("tags", gson.toJson(new ArrayList<Integer>()))
                    .set("events", gson.toJson(new ArrayList<String>()))
                    .set("role", "SU")
                    .set("state", "ENABLED")
                    .set("points", "")
                    .build();
            datastore.add(user);
        } else {}

    }
    
    
    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterData data) {

        if(data.emptyParameters())
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if(!data.validEmail())
            return Response.status(Response.Status.FORBIDDEN).build();

        if(!data.validPasswordLenght())
            return Response.status(Response.Status.LENGTH_REQUIRED).build();

        if(!data.validPasswordConfirmation())
            return Response.status(Response.Status.EXPECTATION_FAILED).build();

        Transaction txn = datastore.newTransaction();
        
        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
            Entity user = txn.get(userKey);
            if (user != null) {
                txn.rollback();
                return Response.status(Response.Status.CONFLICT)
                        .entity("User " + data.username + " already exists.").build();
            } else {
            	
            	Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
    					.setFilter(PropertyFilter.eq("email", data.email)).build();
    			QueryResults<Entity> results = datastore.run(query);
    			if(results.hasNext()) {
    				txn.rollback();
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("Email " + data.email + " already exists.").build();
    			}else {
    				
    				PointsData points = new PointsData(data.username);
    				user = Entity.newBuilder(userKey)
    						.set("username", data.username)
    						.set("email", data.email)
    						.set("password", DigestUtils.sha512Hex(data.password))
    						.set("profileType", "private")
    						.set("landLine", "")
    						.set("mobile", "")
    						.set("address", "")
    						.set("secondAddress", "")
    						.set("zipCode", "")
    						.set("bio", "")
    						.set("tags", gson.toJson(new ArrayList<Integer>()))
    						.set("events", gson.toJson(new ArrayList<String>()))
    						.set("role", "USER")
    						.set("state", "ENABLED")
    						.set("points", gson.toJson(points))
    						.set("my_tracks", gson.toJson(new ArrayList<String>()))
    						.build();
    				
    				txn.add(user);
    				txn.commit();
    				return Response.ok("User " + data.username + " registered.").build();
    			}   			    			
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
    
    @POST
    @Path("/institution")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerInstitution(RegisterInstitutionData data) {

        if(data.emptyParameters())
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if(!data.validNif())
        	return Response.status(Response.Status.NOT_ACCEPTABLE).build();

        if(!data.validEmail())
            return Response.status(Response.Status.FORBIDDEN).build();

        if(!data.validPasswordLenght())
            return Response.status(Response.Status.LENGTH_REQUIRED).build();

        if(!data.validPasswordConfirmation())
            return Response.status(Response.Status.EXPECTATION_FAILED).build();

        Transaction txn = datastore.newTransaction();
        String coordinates = data.lat + " " + data.lon;

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.nif);
            Entity user = txn.get(userKey);
            if (user != null) {
                txn.rollback();
                return Response.status(Response.Status.CONFLICT)
                        .entity("Institution " + data.username + " already exists.").build();
            } else {
            	
            	Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
    					.setFilter(PropertyFilter.eq("email", data.email)).build();
    			QueryResults<Entity> results = datastore.run(query);
    			if(results.hasNext()) {
    				txn.rollback();
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("Email " + data.email + " already exists.").build();
    			}else {		    				
    				user = Entity.newBuilder(userKey)
    						.set("nif", data.nif)
    						.set("username", data.username)
    						.set("email", data.email)
    						.set("password", DigestUtils.sha512Hex(data.password))
    						.set("landLine", "")
    						.set("mobile", "")
    						.set("address", "")
    						.set("zipCode", "")
    						.set("website", "")
    						.set("twitter", "")
    						.set("instagram", "")
    						.set("youtube", "")
    						.set("facebook", "")
    						.set("fax", "")
    						.set("bio", "")
    						.set("events", gson.toJson(new ArrayList<String>()))
    						.set("role", "INSTITUTION")
    						.set("state", "ENABLED")
    						.set("coordinates", coordinates)
    						.build();
    				
    				txn.add(user);
    				txn.commit();
    				return Response.ok("Institution " + data.username + " registered.").build();
    			}
            	
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
    
    @POST
    @Path("/backofficega")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerBackOfficeGA(RegisterDataGA data) {

        if(data.emptyParameters())
            return Response.status(Response.Status.UNAUTHORIZED).build();

        if(!data.validEmail())
            return Response.status(Response.Status.FORBIDDEN).build();

        if(!data.validPasswordLenght())
            return Response.status(Response.Status.LENGTH_REQUIRED).build();

        if(!data.validPasswordConfirmation())
            return Response.status(Response.Status.EXPECTATION_FAILED).build();

        Transaction txn = datastore.newTransaction();
        
        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
            Entity user = txn.get(userKey);
            if (user != null) {
                txn.rollback();
                return Response.status(Response.Status.CONFLICT)
                        .entity("User " + data.username + " already exists.").build();
            } else {
            	
            	Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
    					.setFilter(PropertyFilter.eq("email", data.email)).build();
    			QueryResults<Entity> results = datastore.run(query);
    			if(results.hasNext()) {
    				txn.rollback();
                    return Response.status(Response.Status.FORBIDDEN)
                            .entity("Email " + data.email + " already exists.").build();
    			}else {		
    				user = Entity.newBuilder(userKey)
    						.set("username", data.username)
    						.set("email", data.email)
    						.set("password", DigestUtils.sha512Hex(data.password))
    						.set("profileType", "private")
    						.set("role", "GA")
    						.set("state", "ENABLED")
    						.build();
    				
    				txn.add(user);
    				txn.commit();
    				return Response.ok("BackOffice GA: " + data.username + " registered.").build();
    			}   			    			
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }
    
}
