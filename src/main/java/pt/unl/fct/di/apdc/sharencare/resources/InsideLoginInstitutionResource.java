package pt.unl.fct.di.apdc.sharencare.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.api.gax.paging.Page;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.filters.Secured;
import pt.unl.fct.di.apdc.sharencare.util.ChangePasswordData;
import pt.unl.fct.di.apdc.sharencare.util.ProfileInstitutionData;

@Path("/loggedInInstitution")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class InsideLoginInstitutionResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Storage storage = StorageOptions.newBuilder().setProjectId("capable-sphinx-312419").build()
			.getService();
	private final Bucket bucket = storage.get("capable-sphinx-312419-sharencare-apdc-2021",
			Storage.BucketGetOption.fields(Storage.BucketField.values()));

	@POST
	@Path("/changeAttributes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeAttributes(ProfileInstitutionData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (data.tokenId.equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + data.tokenId + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN)
					.entity("Institution with username: " + token.getString("username") + " doesn't exist").build();

		if (user.getString("state").equals("DISABLED"))
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("Institution with id: " + user.getString("username") + " is disabled.").build();

		/*
		 * END OF VERIFICATIONS
		 */

		String email = data.email;
		String mobile = data.mobile;
		String landLine = data.landLine;
		String address = data.address;
		String zipCode = data.zipCode;
		String website = data.website;
		String instagram = data.instagram;
		String twitter = data.twitter;
		String facebook = data.facebook;
		String youtube = data.youtube;
		String fax = data.fax;
		String bio = data.bio;
		byte[] profilePic = data.profilePic;

		if (data.noChange(user) && getProfilePic(user.getString("username")) == profilePic)
			return Response.status(Status.LENGTH_REQUIRED).build();

		if (!data.validEmail())
			return Response.status(Status.PRECONDITION_FAILED).build();

		if (!data.validPhone())
			return Response.status(Status.EXPECTATION_FAILED).build();

		if (!data.validPostalCode())
			return Response.status(Status.METHOD_NOT_ALLOWED).build();

		if (!data.validWebsite())
			return Response.status(Status.BAD_REQUEST).build();

		if (!data.validFax())
			return Response.status(Status.CONFLICT).build();

		bucket.create(token.getString("username"), profilePic);

		user = Entity.newBuilder(userKey).set("username", user.getString("username"))
				.set("nif", token.getString("username")).set("password", user.getString("password")).set("email", email)
				.set("bio", bio).set("landLine", landLine).set("mobile", mobile).set("address", address)
				.set("zipCode", zipCode).set("website", website).set("twitter", twitter).set("instagram", instagram)
				.set("youtube", youtube).set("facebook", facebook).set("fax", fax)
				.set("events", user.getString("events")).set("role", user.getString("role"))
				.set("state", user.getString("state")).build();

		datastore.update(user);

		return Response.ok("Properties changed").build();
	}

	@POST
	@Path("/changeAttributesWeb")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeAttributesWeb(@CookieParam("Token") NewCookie cookie, ProfileInstitutionData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id doesn't exist").build();

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
		String zipCode = data.zipCode;
		String website = data.website;
		String instagram = data.instagram;
		String twitter = data.twitter;
		String facebook = data.facebook;
		String youtube = data.youtube;
		String fax = data.fax;
		String bio = data.bio;
		byte[] profilePic = data.profilePic;

		if (data.noChange(user) && getProfilePic(user.getString("username")) == profilePic)
			return Response.status(Status.LENGTH_REQUIRED).build();

		if (!data.validEmail())
			return Response.status(Status.PRECONDITION_FAILED).build();

		if (!data.validPhone())
			return Response.status(Status.EXPECTATION_FAILED).build();

		if (!data.validPostalCode())
			return Response.status(Status.METHOD_NOT_ALLOWED).build();

		if (!data.validWebsite())
			return Response.status(Status.BAD_REQUEST).build();

		if (!data.validFax())
			return Response.status(Status.CONFLICT).build();

		bucket.create(token.getString("username"), profilePic);

		user = Entity.newBuilder(userKey).set("username", user.getString("username"))
				.set("nif", token.getString("username")).set("password", user.getString("password")).set("email", email)
				.set("bio", bio).set("landLine", landLine).set("mobile", mobile).set("address", address)
				.set("zipCode", zipCode).set("website", website).set("twitter", twitter).set("instagram", instagram)
				.set("youtube", youtube).set("facebook", facebook).set("fax", fax)
				.set("events", user.getString("events")).set("role", user.getString("role"))
				.set("state", user.getString("state")).build();

		datastore.update(user);

		return Response.ok("Properties changed").cookie(cookie).build();
	}

	@POST
	@Path("/changePasswordCompany")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePasswordCompany(@CookieParam("Token") NewCookie cookie, ChangePasswordData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (data.emptyParameters())
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
					user = Entity.newBuilder(userKey).set("nif", user.getString("nif"))
							.set("username", user.getString("username")).set("email", user.getString("email"))
							.set("bio", user.getString("bio")).set("password", DigestUtils.sha512Hex(data.newPassword))
							.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
							.set("address", user.getString("address")).set("zipCode", user.getString("zipCode"))
							.set("role", user.getString("role")).set("state", user.getString("state"))
							.set("website", user.getString("website")).set("twitter", user.getString("twitter"))
							.set("instagram", user.getString("instagram")).set("youtube", user.getString("youtube"))
							.set("facebook", user.getString("facebook")).set("fax", user.getString("fax"))
							.set("events", user.getString("events")).build();
					datastore.put(user);

					return Response.ok("Password was changed").cookie(cookie).build();
				}
				return Response.status(Status.EXPECTATION_FAILED).entity("Passwords don't match.").build();
			}
			return Response.status(Response.Status.LENGTH_REQUIRED).build();
		}
		return Response.status(Status.CONFLICT).entity("Old password is incorrect.").build();
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
