package pt.unl.fct.di.apdc.sharencare.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
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

	private final Gson g = new Gson();

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public LoginResource() {

	}

	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginUser(LoginData data) {

		if (data.emptyParameters())
			return Response.status(Response.Status.UNAUTHORIZED).build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.usernameLogin);
		Entity user = datastore.get(userKey);

		if (user != null) {
			if (!user.getString("role").equals("INSTITUTION") && !user.getString("role").equals("GA")) {
				String hashedPWD = user.getString("password");

				if (hashedPWD.equals(DigestUtils.sha512Hex(data.passwordLogin))) {
					AuthToken t = new AuthToken(data.usernameLogin, user.getString("role"), data.expirable);

					Cookie cookiee = new Cookie("Token", t.tokenID, "/", null);
					NewCookie cookie = new NewCookie(cookiee, null, -1, null, true, true);

					Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(t.tokenID);
					Entity token = Entity.newBuilder(tokenKey).set("tokenId", t.tokenID).set("username", t.username)
							.set("role", t.role).set("creationData", t.creationData)
							.set("expirable", t.expirable)
							.set("expirationData", t.expirationData).build();

					datastore.add(token);
					return Response.ok(g.toJson(user.getProperties().values())).cookie(cookie).build();
				} else
					return Response.status(Status.EXPECTATION_FAILED).build();
			} else
				return Response.status(Status.FORBIDDEN).build();
		} else
			return Response.status(Status.NOT_FOUND).build();
	}

	@POST
	@Path("/institution")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginInstitution(LoginInstitutionData data) {

		if (data.emptyParameters())
			return Response.status(Response.Status.UNAUTHORIZED).build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.nifLogin);
		Entity user = datastore.get(userKey);

		if (user != null) {
			if (user.getString("role").equals("INSTITUTION") && !user.getString("role").equals("GA")) {
				String hashedPWD = user.getString("password");

				if (hashedPWD.equals(DigestUtils.sha512Hex(data.passwordLogin))) {
					AuthToken t = new AuthToken(data.nifLogin, user.getString("role"), data.expirable);

					Cookie cookiee = new Cookie("Token", t.tokenID, "/", null);
					NewCookie cookie = new NewCookie(cookiee, null, -1, null, true, true);

					Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(t.tokenID);
					Entity token = Entity.newBuilder(tokenKey)
							.set("tokenId", t.tokenID)
							.set("username", t.username)
							.set("role", t.role)
							.set("creationData", t.creationData)
							.set("expirable", t.expirable)
							.set("expirationData", t.expirationData)
							.build();

					datastore.add(token);
					return Response.ok(g.toJson(user.getProperties().values())).cookie(cookie).build();
				} else
					return Response.status(Status.EXPECTATION_FAILED).build();
			} else
				return Response.status(Status.FORBIDDEN).build();
		} else
			return Response.status(Status.NOT_FOUND).build();
	}
	
	
	@POST
	@Path("/backOfficega")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response loginBackOffice(LoginData data) {

		if (data.emptyParameters())
			return Response.status(Response.Status.UNAUTHORIZED).build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.usernameLogin);
		Entity user = datastore.get(userKey);

		if (user != null) {
			if (!user.getString("role").equals("INSTITUTION") && !user.getString("role").equals("USER")) {
				String hashedPWD = user.getString("password");

				if (hashedPWD.equals(DigestUtils.sha512Hex(data.passwordLogin))) {
					AuthToken t = new AuthToken(data.usernameLogin, user.getString("role"), data.expirable);

					Cookie cookiee = new Cookie("Token", t.tokenID, "/", null);
					NewCookie cookie = new NewCookie(cookiee, null, -1, null, true, true);

					Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(t.tokenID);
					Entity token = Entity.newBuilder(tokenKey).set("tokenId", t.tokenID).set("username", t.username)
							.set("role", t.role).set("creationData", t.creationData)
							.set("expirable", t.expirable)
							.set("expirationData", t.expirationData).build();

					datastore.add(token);
					return Response.ok(g.toJson(user.getProperties().values())).cookie(cookie).build();
				} else
					return Response.status(Status.EXPECTATION_FAILED).build();
			} else
				return Response.status(Status.FORBIDDEN).build();
		} else
			return Response.status(Status.NOT_FOUND).build();
	}

}
