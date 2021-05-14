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

import com.google.cloud.datastore.Key;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.gson.Gson;


import pt.unl.fct.di.apdc.sharencare.util.*;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	public LoginResource() {
		
	}
	
	//op6 - logs in a user
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginUser(LoginData data) {
		LOG.fine("Attempt to login user: " + data.usernameLogin);
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.usernameLogin);
		Entity user = datastore.get(userKey);
		
		if (user != null) {
			String hashedPWD = user.getString("password");
			//outra forma de obter a password??
			if(hashedPWD.equals(DigestUtils.sha512Hex(data.passwordLogin))) {
				AuthToken t = new AuthToken(data.usernameLogin, user.getString("role"));
				
				Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(t.tokenID);
				Entity token = Entity.newBuilder(tokenKey)
								.set("tokenId", t.tokenID)
								.set("username", t.username)
								.set("role", t.role)
								.set("creationData", t.creationData)
								.set("expirationData", t.expirationData)
								.set("valid", t.valid)
								.build();
						
				LOG.info("User " + data.usernameLogin + " logged in successfully.");
				datastore.add(token);
				return Response.ok("User " + data.usernameLogin + " is now logged in.").build();
			} else {
				LOG.warning("Wrong password for username: " + data.usernameLogin);
				return Response.status(Status.FORBIDDEN).build();
			}
		}
		else {
			LOG.warning("Failed login attempt for username: " + data.usernameLogin);
			return Response.status(Status.FORBIDDEN).build();
		}

	}
}
