package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.RegisterData;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public RegisterResource() {
		Key userKey = datastore.newKeyFactory().setKind("User").newKey("joaovargues55185SU");
		Entity user = datastore.get(userKey);
		if(user == null) {
			user = Entity.newBuilder(userKey)
					.set("email", "jpvar@gmail.com")
					.set("password", DigestUtils.sha512Hex("1234v"))
					.set("role", "SU")
					.set("state", "ENABLED")
					.set("perfil", "")
					.set("phone1", "")
					.set("phone2", "")
					.set("morada", "")
					.set("codigoPostal", "").build();
			datastore.add(user);
		}
	}

	@POST
	@Path("/registration")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser2(RegisterData data) {
		LOG.fine("Starting to execute computation taks");

		if (!data.validRegistration()) {
			return Response.status(Status.BAD_REQUEST).entity("Missing or wrong parameter.").build();
		}

		Transaction txn = datastore.newTransaction();
		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			if(user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("User already exists.").build();
			}else if(!data.password.equals(data.passwordCom)){
				txn.rollback();
				return Response.status(Status.BAD_REQUEST).entity("Password confirmation dont match password.").build();
			}else {							
				user = Entity.newBuilder(userKey)
						.set("email", data.email)
						.set("password", DigestUtils.sha512Hex(data.password))
						.set("role", "USER")
						.set("state", "ENABLED")
						.set("perfil", "")
						.set("phone1", "")
						.set("phone2", "")
						.set("morada", "")
						.set("codigoPostal", "").build();
				txn.add(user);
				LOG.info("User registered " + data.username);
				txn.commit();
				return Response.ok(g.toJson(data)).build();
			}
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	
	
	
}
