package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.ArrayList;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import org.apache.commons.codec.digest.DigestUtils;

import pt.unl.fct.di.apdc.sharencare.util.RegisterInstitutionData;
import pt.unl.fct.di.apdc.sharencare.util.RegisterData;

//import pt.unl.fct.di.apdc.APDC56253.util.LoginData;
//import pt.unl.fct.di.apdc.APDC56253.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
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
                    .set("postal", "")
                    .set("tags", "")
                    .set("events", "")
                    .set("role", "SU")
                    .set("state", "ENABLED")
                    .set("profilePic", "")
                    .set("tags", gson.toJson(new ArrayList<Integer>()))
                    .set("events", gson.toJson(new ArrayList<String>()))
                    .build();
            datastore.add(user);
        } else {}

    }

    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterData data) {

        LOG.fine("Attempt to register user: " + data.username);

        if(data.emptyParameters()){
            System.out.println("Please fill in all non-optional fields.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(!data.validEmail()){
            System.out.println("Invalid email.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if(!data.validPasswordLenght()){
            System.out.println("Invalid password. Please enter 5 or more characters.");
            return Response.status(Response.Status.LENGTH_REQUIRED).build();
        }
        if(!data.validPasswordConfirmation()){
            System.out.println("The passwords are not the same.");
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }

//        if (!data.validData()){
//            System.out.println("data inserida invalida " + data.username);
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data").build();
//        }

        Transaction txn = datastore.newTransaction();
        
        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
            Entity user = txn.get(userKey);
            if (user != null) {
                txn.rollback();
                return Response.status(Response.Status.CONFLICT)
                        .entity("User " + data.username + " already exists.").build();
            } else {
                user = Entity.newBuilder(userKey)
                        .set("username", data.username)
                        .set("email", data.email)
                        .set("password", DigestUtils.sha512Hex(data.password))
        				.set("profileType", "private")
        				.set("landLine", "")
        				.set("mobile", "")
        				.set("address", "")
        				.set("secondAddress", "")
        				.set("postal", "")
                        .set("role", "USER")
                        .set("state", "ENABLED")
        				.set("profilePic", "")
        		        .set("tags", gson.toJson(new ArrayList<Integer>()))
                        .set("events", gson.toJson(new ArrayList<String>()))
                        .build();

                txn.add(user);
                txn.commit();
                return Response.ok("User " + data.username + " registered.").build();
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

        System.out.println("ENTREI O REGISTER");

        LOG.fine("Attempt to register institution: " + data.username);

        if(data.emptyParameters()){
            System.out.println("Please fill in all non-optional fields.");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(!data.validNif()) {
        	System.out.println("Invalid Nif.");//406
        	return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
        if(!data.validEmail()){
            System.out.println("Invalid email.");
            return Response.status(Response.Status.FORBIDDEN).build();
        }
        if(!data.validPasswordLenght()){
            System.out.println("Invalid password. Please enter 5 or more characters.");
            return Response.status(Response.Status.LENGTH_REQUIRED).build();
        }
        if(!data.validPasswordConfirmation()){
            System.out.println("The passwords are not the same.");
            return Response.status(Response.Status.EXPECTATION_FAILED).build();
        }

//        if (!data.validData()){
//            System.out.println("data inserida invalida " + data.username);
//            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data").build();
//        }

        Transaction txn = datastore.newTransaction();

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.nif);
            Entity user = txn.get(userKey);
            if (user != null) {
                txn.rollback();
                return Response.status(Response.Status.CONFLICT)
                        .entity("Institution " + data.username + " already exists.").build();
            } else {
                user = Entity.newBuilder(userKey)
                		.set("nif", data.nif)
                        .set("username", data.username)
                        .set("email", data.email)
                        .set("password", DigestUtils.sha512Hex(data.password))
        				.set("landLine", "")
        				.set("mobile", "")
        				.set("address", "")
        				.set("postal", "")
        				.set("website", "")
        				.set("twitter", "")
        				.set("instagram", "")
        				.set("youtube", "")
        				.set("facebook", "")
        				.set("fax", "")
        				.set("members", "")
        				.set("events", "")
                        .build();

                txn.add(user);
                txn.commit();
                return Response.ok("Institution " + data.username + " registered.").build();
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }
    
    

}
