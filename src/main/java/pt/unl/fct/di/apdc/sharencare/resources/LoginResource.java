package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.AuthToken;
import pt.unl.fct.di.apdc.sharencare.util.LoginData;
import pt.unl.fct.di.apdc.sharencare.util.LogoutData;

import java.sql.Timestamp;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

	/**
	 * A Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	Timestamp timestamp = new Timestamp(System.currentTimeMillis());

	public LoginResource() {
	} // Nothing to be done here

	@POST
	@Path("/logging")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.username);

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = datastore.get(userKey);
		if (user != null) {
			String hashedPWD = user.getString("password");
			 if (hashedPWD.equals(DigestUtils.sha512Hex(data.password))) {
				AuthToken authTokenID = new AuthToken(data.username, user.getString("role"));
				Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(authTokenID.tokenID);
				Entity token = Entity.newBuilder(tokenKey).set("tokenId", authTokenID.tokenID)
						.set("role", authTokenID.role).set("username", authTokenID.username)
						.set("creationData", authTokenID.creationData).set("expirationData", authTokenID.expirationData)
						.build();

				datastore.put(token);
				LOG.info("User '" + data.username + "' logged in sucessfully");
				return Response.ok(g.toJson(token)).build();
			} else {
				LOG.warning("Wrong password for username: " + data.username);
				return Response.status(Status.BAD_REQUEST).entity("Wrong password.").build();
			}
		} else {
			LOG.warning("Failed login attemp for username: " + data.username);
			return Response.status(Status.FORBIDDEN).entity("User doesnt exist.").build();
		}
	}

	@PUT
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doLogout(LogoutData data) {

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity userByToken = datastore.get(tokenKey);
		String user = userByToken.getString("username");
		if (userByToken != null) {
			datastore.delete(tokenKey);
			LOG.info("User '" + user + "' logged out sucessfully");
			return Response.ok(g.toJson(userByToken.getString("username"))).build();
		} else {
			return Response.status(Status.FORBIDDEN).entity("Invalid token.").build();
		}
	}

}
