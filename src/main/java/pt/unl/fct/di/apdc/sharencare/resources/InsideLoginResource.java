package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.api.gax.paging.Page;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.filters.Secured;
import pt.unl.fct.di.apdc.sharencare.util.ChangePasswordData;
import pt.unl.fct.di.apdc.sharencare.util.ChangeRoleData;
import pt.unl.fct.di.apdc.sharencare.util.ChangeStateData;
import pt.unl.fct.di.apdc.sharencare.util.ListRolesData;
import pt.unl.fct.di.apdc.sharencare.util.ProfileData;
import pt.unl.fct.di.apdc.sharencare.util.ProfileDataTags;
import pt.unl.fct.di.apdc.sharencare.util.RemoveUserData;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

@Path("/loggedIn")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class InsideLoginResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Storage storage = StorageOptions.newBuilder().setProjectId("capable-sphinx-312419").build()
			.getService();
	private final Bucket bucket = storage.get("capable-sphinx-312419-sharencare-apdc-2021",
			Storage.BucketGetOption.fields(Storage.BucketField.values()));

	private final Gson g = new Gson();

	@POST
	@Path("/removeUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeUser(@QueryParam("tokenId") String tokenId, RemoveUserData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userToDelete);
		Entity user = datastore.get(userKey);

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId + " doesn't exist")
					.build();

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser.getString("state").equals("DISABLED"))
			return Response.status(Status.BAD_REQUEST)
					.entity("User with id: " + currentUser.getString("username") + " is disabled.").build();

		if (user == null)
			return Response.status(Status.BAD_REQUEST)
					.entity("User with username: " + data.userToDelete + " doesn't exist").build();

		if (!token.getString("username").equals(data.userToDelete))
			if (!checkRole(user.getString("role"), token.getString("role")))
				return Response.status(Status.FORBIDDEN)
						.entity("You do not have permissions to remove user:  " + data.userToDelete).build();

		/*
		 * END OF VERIFICATIONS
		 */

		datastore.delete(userKey);
		return Response.ok(data.userToDelete + " was successfully removed").build();

	}

	@POST
	@Path("/changeAttributesTags")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeTags(ProfileDataTags data) {
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user = datastore.get(userKey);

		if (user == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN).entity("User with username: " + data.username + " doesn't exist")
					.build();
		}

		user = Entity.newBuilder(userKey).set("username", data.username).set("password", user.getString("password"))
				.set("email", user.getString("email")).set("bio", user.getString("bio"))
				.set("profileType", user.getString("profileType")).set("landLine", user.getString("landLine"))
				.set("mobile", user.getString("mobile")).set("address", user.getString("address"))
				.set("secondAddress", user.getString("secondAddress")).set("zipCode", user.getString("zipCode"))
				.set("tags", g.toJson(data.tags)).set("events", user.getString("events"))
				.set("role", user.getString("role")).set("state", user.getString("state")).build();

		datastore.update(user);

		return Response.ok("Tags changed").build();
	}

	@POST
	@Path("/changeAttributes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeProperty(@QueryParam("tokenId") String tokenId, ProfileData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (tokenId.equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + tokenId + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();

		if (user.getString("state").equals("DISABLED"))
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("User with id: " + user.getString("username") + " is disabled.").build();

		/*
		 * END OF VERIFICATIONS
		 */

		String email = data.email;
		String mobile = data.mobile;
		String landLine = data.landLine;
		String address = data.address;
		String secondAddress = data.secondAddress;
		String zipCode = data.zipCode;
		String profileType = data.profileType;
		String tags = g.toJson(data.tags);
		String bio = data.bio;
		byte[] profilePic = data.profilePic;

		if (data.noChange(user) && getProfilePic(user.getString("username")) == profilePic)
			return Response.status(Status.LENGTH_REQUIRED).build();

		if (!data.validEmail())
			return Response.status(Status.PRECONDITION_FAILED).build();

		if (!data.validPhones())
			return Response.status(Status.EXPECTATION_FAILED).build();

		if (!data.validZipCode())
			return Response.status(Status.METHOD_NOT_ALLOWED).build();

		if (!data.validProfileType())
			return Response.status(Status.REQUESTED_RANGE_NOT_SATISFIABLE).build();

		bucket.create(user.getString("username"), profilePic);

		user = Entity.newBuilder(userKey).set("username", token.getString("username"))
				.set("password", user.getString("password")).set("bio", bio).set("email", email)
				.set("profileType", profileType).set("landLine", landLine).set("mobile", mobile).set("address", address)
				.set("secondAddress", secondAddress).set("zipCode", zipCode).set("tags", g.toJson(tags))
				.set("events", user.getString("events")).set("role", user.getString("role"))
				.set("state", user.getString("state")).build();

		datastore.update(user);

		return Response.ok("Properties changed").build();
	}

	@GET
	@Path("/getPic")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPic(@QueryParam("tokenId") String tokenId) {
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + tokenId + " doesn't exist").build();

		}

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		}
		byte[] pic = null;
		Page<Blob> blobs = bucket.list();
		for (Blob blob : blobs.getValues()) {
			if (token.getString("username").equals(blob.getName())) {
				pic = blob.getContent();
			}
		}

		return Response.ok(g.toJson(pic)).build();
	}

	@GET
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@QueryParam("username") String username, @QueryParam("tokenId") String tokenId) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN).entity("User with username: " + username + " doesn't exist")
					.build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + tokenId + " doesn't exist").build();

		/*
		 * END OF VERIFICATIONS
		 */

		byte[] pic = null;
		Page<Blob> blobs = bucket.list();
		for (Blob blob : blobs.getValues()) {
			if (token.getString("username").equals(blob.getName())) {
				pic = blob.getContent();
			}
		}

		return Response.ok(g.toJson(user.getProperties().values()) + g.toJson(pic)).build();
	}

	@POST
	@Path("/changeAttributesWeb")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeProperty(@CookieParam("Token") NewCookie cookie, ProfileData data) {

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

		if (user.getString("state").equals("DISABLED"))
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("User with id: " + user.getString("username") + " is disabled.").build();

		/*
		 * END OF VERIFICATIONS
		 */

		String email = data.email;
		String mobile = data.mobile;
		String landLine = data.landLine;
		String address = data.address;
		String secondAddress = data.secondAddress;
		String zipCode = data.zipCode;
		String profileType = data.profileType;
		String tags = g.toJson(data.tags);
		String bio = data.bio;
		byte[] profilePic = data.profilePic;

		if (data.noChange(user) && getProfilePic(user.getString("username")) == profilePic)
			return Response.status(Status.LENGTH_REQUIRED).build();

		if (!data.validEmail())
			return Response.status(Status.PRECONDITION_FAILED).build();

		if (!data.validPhones())
			return Response.status(Status.EXPECTATION_FAILED).build();

		if (!data.validZipCode())
			return Response.status(Status.METHOD_NOT_ALLOWED).build();

		if (!data.validProfileType())
			return Response.status(Status.REQUESTED_RANGE_NOT_SATISFIABLE).build();

		bucket.create(user.getString("username"), profilePic);

		user = Entity.newBuilder(userKey).set("username", token.getString("username"))
				.set("password", user.getString("password")).set("email", email).set("bio", bio)
				.set("profileType", profileType).set("landLine", landLine).set("mobile", mobile).set("address", address)
				.set("secondAddress", secondAddress).set("zipCode", zipCode).set("tags", g.toJson(tags))
				.set("events", user.getString("events")).set("role", user.getString("role"))
				.set("state", user.getString("state")).build();

		datastore.update(user);
		return Response.ok("Properties changed.").cookie(cookie).build();
	}

	@POST
	@Path("/changeRole")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeRole(@CookieParam("Token") NewCookie cookie, ChangeRoleData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		if (data.emptyParameters())
			return Response.status(Response.Status.UNAUTHORIZED).build();

		Key userToChangeKey = datastore.newKeyFactory().setKind("User").newKey(data.userToBeChanged);
		Entity userToBeChanged = datastore.get(userToChangeKey);

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id doesn't exist").build();

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser.getString("state").equals("DISABLED"))
			return Response.status(Status.BAD_REQUEST)
					.entity("User with id: " + currentUser.getString("username") + " is disabled.").build();

		/*
		 * END OF VERIFICATIONS
		 */

		if (userToBeChanged == null)
			return Response.status(Status.FORBIDDEN).entity(data.userToBeChanged + " does not exist.").build();

		if (!checkRoleChange(userToBeChanged.getString("role"), data.roleToChange, token.getString("role")))
			return Response.status(Status.NOT_ACCEPTABLE).build();

		userToBeChanged = Entity.newBuilder(userToChangeKey).set("username", data.userToBeChanged)
				.set("password", userToBeChanged.getString("password"))
				.set("confirmation", userToBeChanged.getString("password"))
				.set("email", userToBeChanged.getString("email")).set("bio", userToBeChanged.getString("bio"))
				.set("profileType", userToBeChanged.getString("profileType"))
				.set("landLine", userToBeChanged.getString("landLine"))
				.set("mobile", userToBeChanged.getString("mobile")).set("address", userToBeChanged.getString("address"))
				.set("secondAddress", userToBeChanged.getString("secondAddress"))
				.set("zipCode", userToBeChanged.getString("zipCode")).set("role", data.roleToChange)
				.set("state", userToBeChanged.getString("state")).build();

		datastore.update(userToBeChanged);
		return Response.ok(data.userToBeChanged + " role was changed to: " + data.roleToChange).cookie(cookie).build();

	}

	@POST
	@Path("/changeState")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeState(@CookieParam("Token") NewCookie cookie, ChangeStateData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key userToChangeKey = datastore.newKeyFactory().setKind("User").newKey(data.userToChange);
		Entity userToChange = datastore.get(userToChangeKey);

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id doesn't exist").build();

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser.getString("state").equals("DISABLED"))
			return Response.status(Status.BAD_REQUEST)
					.entity("User with id: " + currentUser.getString("username") + " is disabled.").build();

		if (userToChange == null)
			return Response.status(Status.FORBIDDEN).entity(data.userToChange + " does not exist").build();

		if (!(data.state.equals("ENABLED") || data.state.equals("DISABLED")))
			return Response.status(Status.FORBIDDEN)
					.entity("The option " + data.state + " for STATE of user does not exist").build();

		/*
		 * END OF VERIFICATIONS
		 */

		if (!checkRole(userToChange.getString("role"), token.getString("role")))
			return Response.status(Status.FORBIDDEN)
					.entity("You do not have permissions to change " + data.userToChange + " state").build();

		userToChange = Entity.newBuilder(userToChangeKey).set("username", data.userToChange)
				.set("password", userToChange.getString("password"))
				.set("confirmation", userToChange.getString("password")).set("email", userToChange.getString("email"))
				.set("bio", userToChange.getString("bio")).set("profileType", userToChange.getString("profileType"))
				.set("landLine", userToChange.getString("landLine")).set("mobile", userToChange.getString("mobile"))
				.set("address", userToChange.getString("address"))
				.set("secondAddress", userToChange.getString("secondAddress"))
				.set("zipCode", userToChange.getString("zipCode")).set("role", userToChange.getString("role"))
				.set("state", data.state).build();

		datastore.put(userToChange);
		return Response.ok(data.userToChange + " state was changed to: " + data.state).build();

	}

	@POST
	@Path("/logout")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(@CookieParam("Token") NewCookie cookie) {
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token doesn't exist").build();

		String user = token.getString("username");
		datastore.delete(tokenKey);
		Cookie cookiee = new Cookie("Token", null, "/", null);
		NewCookie cookieAux = new NewCookie(cookiee, null, -1, null, true, true);

		return Response.ok(user + " is now logged out.").cookie(cookieAux).build();
	}

	@POST
	@Path("/changePassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePassword(@CookieParam("Token") NewCookie cookie, ChangePasswordData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (data.emptyParameters() || data.cantBeSamePassword())
			return Response.status(Response.Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN).entity(token.getString("username") + " does not exist").build();

		if (user.getString("state").equals("DISABLED"))
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("User with id: " + user.getString("username") + " is disabled.").build();

		/*
		 * END OF VERIFICATIONS
		 */

		String hashedPWD = user.getString("password");
		if (hashedPWD.equals(DigestUtils.sha512Hex(data.oldPassword))) {
			if (data.validPasswordLength()) {
				if (data.newPassword.equals(data.confirmation)) {
					user = Entity.newBuilder(userKey).set("username", token.getString("username"))
							.set("password", DigestUtils.sha512Hex(data.newPassword))
							.set("email", user.getString("email")).set("bio", user.getString("bio"))
							.set("profileType", user.getString("profileType"))
							.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
							.set("address", user.getString("address"))
							.set("secondAddress", user.getString("secondAddress"))
							.set("zipCode", user.getString("zipCode")).set("role", user.getString("role"))
							.set("state", user.getString("state")).set("profilePic", user.getString("profilePic"))
							.set("tags", user.getString("tags")).set("events", user.getString("events")).build();
					datastore.put(user);
					return Response.ok("Password was changed").cookie(cookie).build();

				}
				return Response.status(Status.EXPECTATION_FAILED).entity("Passwords don't match.").build();
			}
			return Response.status(Response.Status.LENGTH_REQUIRED).build();
		}
		return Response.status(Status.CONFLICT).entity("Old password is incorrect.").build();
	}

	@POST
	@Path("/listRole")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response listUsersWithRole(@QueryParam("tokenId") String tokenId, ListRolesData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.BAD_REQUEST).entity("User doesn't exist").build();

		if (user.getString("state").equals("DISABLED"))
			return Response.status(Status.BAD_REQUEST)
					.entity("User with id: " + user.getString("username") + " is disabled.").build();

		/*
		 * END OF VERIFICATIONS
		 */

		String userRole = token.getString("role");

		if (userRole.equals("GBO") || userRole.equals("GA")) {
			Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
					.setFilter(PropertyFilter.eq("role", data.role)).build();
			QueryResults<Entity> results = datastore.run(query);
			List<String> r = new ArrayList<>();
			while (results.hasNext())
				r.add(results.next().getKey().getName());
			return Response.ok(g.toJson(r)).build();
		}

		return Response.status(Status.FORBIDDEN).entity("You do not have permissions").build();
	}

	private boolean checkRole(String userRole, String removerRole) {
		if (userRole.equals("USER"))
			if (removerRole.equals("GBO") || removerRole.equals("GA") || removerRole.equals("SU"))
				return true;
		if (userRole.equals("GBO"))
			if (removerRole.equals("GA") || removerRole.equals("SU"))
				return true;
		if (userRole.equals("GA"))
			if (removerRole.equals("SU"))
				return true;
		return false;
	}

	private boolean checkRoleChange(String userToBeChangedRole, String nextRole, String masterRole) {
		boolean isValid = false;
		if (userToBeChangedRole.equals("USER")) {
			if (masterRole.equals("SU") && (nextRole.equals("GBO") || nextRole.equals("GA"))) {
				isValid = true;
			} else if (masterRole.equals("GA") && nextRole.equals("GBO")) {
				isValid = true;
			}
		}
		return isValid;
	}

	private byte[] getProfilePic(String username) {
		byte[] pic = null;
		Page<Blob> blobs = bucket.list();
		for (Blob blob : blobs.getValues()) {
			if (username.equals(blob.getName())) {
				pic = blob.getContent();
			}
		}

		return pic;
	}

}
