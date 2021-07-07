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

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;


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
	AuthTokenResource t = new AuthTokenResource();

	@POST
	@Path("/changeAttributes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeAttributes(ProfileInstitutionData data) {

		if (data.tokenId.equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

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
		byte[] profilePic = data.profilePic;

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + data.tokenId + " doesn't exist")
					.build();
		}

//		if(!t.validToken(tokenKey))
//			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId +
//					" has expired. Please login again to continue using the application")
//					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null) {
			System.out.println("The institution with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("Institution with username: " + token.getString("username") + " doesn't exist").build();
		}

		if (user.getString("state").equals("DISABLED")) {
			System.out.println("The institution with the given token is disabled.");
			return Response.status(Status.NOT_ACCEPTABLE)
					.entity("Institution with id: " + user.getString("username") + " is disabled.").build();
		}

		if (data.email.equals(""))
			email = user.getString("email");
		else {
			if (!data.validEmail()) {
				System.out.println("Invalid email.");
				return Response.status(Status.PRECONDITION_FAILED).build();
			}
		}
		// TODO
		/*if (data.profilePic.length == 0) {
			profilePic = null;
		}*/

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
			zipCode = user.getString("postal");
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

		// falta saber que identificador utilizar para a profile pic
		bucket.create(token.getString("username"), profilePic);
		/*
		 * if(data.profilePic == null) profilePic = user.getBlob("profilePic");
		 */
//		if (!validateData(data))
//			return Response.status(Status.BAD_REQUEST).entity("Invalid data").build();

		user = Entity.newBuilder(userKey)
				.set("username", user.getString("username"))
				.set("nif", token.getString("username"))
				.set("password", user.getString("password"))
				.set("email", email)
				.set("profilePic", "")
				.set("landLine", landLine)
				.set("mobile", mobile)
				.set("address", address)
				.set("postal", zipCode)
				.set("website", website)
				.set("twitter", twitter)
				.set("instagram", instagram)
				.set("youtube", youtube)
				.set("facebook", facebook)
				.set("fax", fax)
				.set("events", user.getString("events"))
				.set("role", user.getString("role"))
				.set("state", user.getString("state")).build();

		datastore.update(user);

		return Response.ok("Properties changed").build();
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
	byte[] profilePic = data.profilePic;

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

	
	if (data.email.equals(""))
		email = user.getString("email");
	else {
		if (!data.validEmail()) {
			System.out.println("Invalid email.");
			return Response.status(Status.PRECONDITION_FAILED).build();
		}
	}
	// TODO
	if (data.profilePic.length == 0) {
		profilePic = null;
	}

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
		zipCode = user.getString("postal");
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

	// falta saber que identificador utilizar para a profile pic
	bucket.create(token.getString("username"), profilePic);
	/*
	 * if(data.profilePic == null) profilePic = user.getBlob("profilePic");
	 */
//	if (!validateData(data))
//		return Response.status(Status.BAD_REQUEST).entity("Invalid data").build();

	user = Entity.newBuilder(userKey).set("username", token.getString("username"))
			.set("password", user.getString("password")).set("confirmation", user.getString("password"))
			.set("email", email).set("landLine", landLine).set("mobile", mobile).set("address", address)
			.set("postal", zipCode).set("website", website).set("twitter", twitter).set("instagram", instagram)
			.set("youtube", youtube).set("facebook", facebook).set("fax", fax)
			.set("members", g.toJson(user.getString("members"))).set("events", g.toJson(user.getString("events")))
			.set("role", user.getString("role")).set("state", user.getString("state")).build();

	datastore.update(user);

	return Response.ok("Properties changed").cookie(cookie).build();
}

}

