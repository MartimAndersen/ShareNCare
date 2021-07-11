package pt.unl.fct.di.apdc.sharencare.util;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

public class CommentedMethods {

	/*@POST
	@Path("/changeAttributesWeb")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeProperty(@CookieParam("Token") NewCookie cookie, ChangePropertyData data) {
		if (cookie.getName().equals("")) {
			System.out.println("You need to be logged in to execute this operation.");
			return Response.status(Status.UNAUTHORIZED).build();
		}
		if (data.allEmptyParameters()) {
			System.out.println("Please enter at least one new attribute.");
			return Response.status(Status.LENGTH_REQUIRED).build();
		}

		String email = data.newEmail;
		String profileType = data.newProfileType;
		String landLine = data.newLandLine;
		String mobile = data.newMobile;
		String address = data.newAddress;
		String secondAddress = data.newSecondAddress;
		String postal = data.newPostal;
//		byte[] profilePic = data.profilePic;

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id doesn't exist").build();
		}

//		if(!t.validToken(tokenKey))	
//			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + cookie.getName() +	
//					" has expired. Please login again to continue using the application")	
//					.build();	
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
		if (data.newEmail.equals("")) {
			email = user.getString("email");
		} else {
			if (!data.validEmail()) {
				System.out.println("Invalid email.");
				return Response.status(Status.PRECONDITION_FAILED).build();
			}
		}
		if (data.newProfileType.equals("")) {
			profileType = user.getString("profileType");
		}
		if (data.newLandLine.equals("")) {
			landLine = user.getString("landLine");
		}
		if (data.newMobile.equals("")) {
			mobile = user.getString("mobile");
		} else {
			if (!data.validPhone()) {
				System.out.println("Invalid mobile phone number.");
				return Response.status(Status.EXPECTATION_FAILED).build();
			}
		}
		if (data.newAddress.equals("")) {
			address = user.getString("address");
		}
		if (data.newSecondAddress.equals("")) {
			secondAddress = user.getString("secondAddress");
		}
		if (data.newPostal.equals("")) {
			postal = user.getString("postal");
		} else {
			if (!data.validPostalCode()) {
				System.out.println("Invalid postal code.");
				return Response.status(Status.CONFLICT).build();
			}
		}
//		bucket.create(token.getString("username"), profilePic);
		user = Entity.newBuilder(userKey).set("username", token.getString("username"))
				.set("password", user.getString("password")).set("email", email).set("profileType", profileType)
				.set("landLine", landLine).set("mobile", mobile).set("address", address)
				.set("secondAddress", secondAddress).set("postal", postal).set("role", user.getString("role"))
				.set("state", user.getString("state")).set("profilePic", user.getString("profilePic"))
				.set("tags", user.getString("tags")).set("events", user.getString("events")).build();
		datastore.update(user);
		return Response.ok("Properties changed.").cookie(cookie).build();
	}*/
}
