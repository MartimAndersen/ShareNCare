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

	private final Gson g = new Gson();

	@POST
	@Path("/changeAttributes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeAttributes(@CookieParam("Token") NewCookie cookie, ProfileInstitutionData data) {

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
		
		if(user.getString("role").equals("USER")) {
			return Response.status(Status.CONFLICT).build();
		}

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
				.set("state", user.getString("state")).set("coordinates", user.getString("coordinates")).build();

		datastore.update(user);

		return Response.ok("Properties changed").cookie(cookie).build();
	}

	@POST
	@Path("/changeAttributesWeb")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeAttributesWeb(@CookieParam("Token") NewCookie cookie, ProfileInstitutionData data) {

		if (cookie.getName().equals("")) {
			System.out.println("You need to be logged in to execute this operation.");
			return Response.status(Status.UNAUTHORIZED).build();
		}
		if (data.allEmptyParameters()) {
			System.out.println("Please enter at least one new attribute.");
			return Response.status(Status.LENGTH_REQUIRED).build();
		}

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
		//byte[] profilePic = data.profilePic;
		String bio = data.bio;

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id doesn't exist").build();
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
		
		if(user.getString("role").equals("USER")) {
			return Response.status(Status.CONFLICT).build();
		}


		if (data.email.equals(""))
			email = user.getString("email");
		else {
			if (!data.validEmail()) {
				System.out.println("Invalid email.");
				return Response.status(Status.PRECONDITION_FAILED).build();
			}
		}

//		if (data.profilePic.length == 0) {
//			profilePic = null;
//		}

		if (data.landLine.equals(""))
			landLine = user.getString("landLine");

		if (data.mobile.equals(""))
			mobile = user.getString("mobile");
		else {
			if (!data.validPhone()) {
				System.out.println("Invalid mobile phone number.");
				return Response.status(Status.EXPECTATION_FAILED).build();
			}
		}

		if (data.address.equals(""))
			address = user.getString("address");

		if (data.zipCode.equals("")) {
			zipCode = user.getString("zipCode");
		} else if (!data.validPostalCode()) {
			System.out.println("Invalid postal code.");
			return Response.status(Status.METHOD_NOT_ALLOWED).build();
		}

		if (data.website.equals("")) {
			website = user.getString("website");

		} else if (!data.validWebsite()) {
			System.out.println("Invalid website URL");
			return Response.status(Status.BAD_REQUEST).build();
		}

		if (data.instagram.equals(""))
			instagram = user.getString("instagram");

		if (data.twitter.equals(""))
			twitter = user.getString("twitter");

		if (data.facebook.equals(""))
			facebook = user.getString("facebook");

		if (data.youtube.equals(""))
			youtube = user.getString("youtube");

		if (data.fax.equals("")) {
			fax = user.getString("fax");

		} else if (!data.validFax()) {
			System.out.println("Invalid fax number");
			return Response.status(Status.CONFLICT).build();
		}
		if (data.bio.equals("")) {
			bio = user.getString("bio");
		}

		// falta saber que identificador utilizar para a profile pic
		//bucket.create(token.getString("username"), profilePic);
		/*
		 * if(data.profilePic == null) profilePic = user.getBlob("profilePic");
		 */
//	if (!validateData(data))
//		return Response.status(Status.BAD_REQUEST).entity("Invalid data").build();

		user = Entity.newBuilder(userKey).set("username", token.getString("username"))
				.set("password", user.getString("password")).set("nif", user.getString("nif"))/**.set("profilePic", user.getString("profilePic"))*/
				.set("email", email).set("landLine", landLine).set("mobile", mobile).set("address", address)
				.set("zipCode", zipCode).set("website", website).set("twitter", twitter).set("instagram", instagram)
				.set("youtube", youtube).set("facebook", facebook).set("fax", fax)
//			.set("members", g.toJson(user.getString("members")))
				.set("events", g.toJson(user.getString("events")))
				.set("role", user.getString("role")).set("state", user.getString("state")).set("bio", bio).set("coordinates", user.getString("coordinates")).build();

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
		
		if(user.getString("role").equals("INSTITUTION")) {
			return Response.status(Status.CONFLICT).build();
		}

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
							.set("events", user.getString("events")).set("coordinates", user.getString("coordinates")).build();
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
