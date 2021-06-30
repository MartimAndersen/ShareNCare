package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;	
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Value;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.AddEventData;
import pt.unl.fct.di.apdc.sharencare.util.ChangePasswordData;
import pt.unl.fct.di.apdc.sharencare.util.ChangePropertyData;
import pt.unl.fct.di.apdc.sharencare.util.ChangeRoleData;
import pt.unl.fct.di.apdc.sharencare.util.ChangeStateData;
import pt.unl.fct.di.apdc.sharencare.util.ListEventsData;
import pt.unl.fct.di.apdc.sharencare.util.ListRolesData;
import pt.unl.fct.di.apdc.sharencare.util.LogoutUserData;
import pt.unl.fct.di.apdc.sharencare.util.ProfileData;
import pt.unl.fct.di.apdc.sharencare.util.RemoveUserData;
import pt.unl.fct.di.apdc.sharencare.util.TokenData;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

@Path("/loggedIn")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class InsideLoginResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Storage storage = StorageOptions.newBuilder().setProjectId("capable-sphinx-312419").build().getService();
	private final Bucket bucket = storage.get("capable-sphinx-312419-sharencare-apdc-2021", Storage.BucketGetOption.fields(Storage.BucketField.values()));
	
	private final Gson g = new Gson();
	AuthTokenResource t = new AuthTokenResource();
	private static final Logger LOG = Logger.getLogger(InsideLoginResource.class.getName());

	// op2
	@POST
	@Path("/op2")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeUser(RemoveUserData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userToDelete);
		Entity user = datastore.get(userKey);

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + " doesn't exist")
					.build();

		if (!t.validToken(tokenKey))
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId
					+ " has expired. Please login again to continue using the application").build();

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
	@Path("/changeAttributes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeProperty(ProfileData data) {
		
		
		
		if (data.tokenId.equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		if (data.allEmptyParameters()) {
			System.out.println("Please enter at least one new attribute.");
			return Response.status(Status.LENGTH_REQUIRED).build();
		}

		//TODO
		String email = data.email;
		String mobile = data.mobile;
		String landLine = data.landLine;
		String address = data.address;
		String secondAddress = data.secondAddress;
		String zipCode = data.zipCode;
		String profileType = data.profileType;
		List<Integer> tags = data.tags; 
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
			  if(!data.validEmail()) { 
				  System.out.println("Invalid email."); 
				  return Response.status(Status.PRECONDITION_FAILED).build(); 
			  } 
		  }
		 //TODO
		if(data.profilePic.length == 0) {
			profilePic = null;
		}
		if (data.profileType.equals(""))
			profileType = user.getString("profileType");

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

		if (data.secondAddress.equals(""))
			secondAddress = user.getString("secondAddress");

		if (data.zipCode.equals("")) 
			zipCode = user.getString("postal");
	
	    else {
			if (!data.validPostalCode()) {
				System.out.println("Invalid postal code.");
				return Response.status(Status.METHOD_NOT_ALLOWED).build();
			}
		}
		
		/*if(data.tags.isEmpty())
			tags = g.toJson(user.getString("tags"));*/
		
		//falta saber que identificador utilizar para a profile pic
		bucket.create(token.getString("username") , profilePic);
		/*
		if(data.profilePic == null)
			profilePic = user.getBlob("profilePic");
	*/
//		if (!validateData(data))
//			return Response.status(Status.BAD_REQUEST).entity("Invalid data").build();
		

		user = Entity.newBuilder(userKey)
				.set("username", token.getString("username"))
				.set("password", user.getString("password"))
				.set("confirmation", user.getString("password"))
				.set("email", email)
				.set("publicProfile", profileType)
				.set("landLine", landLine)
				.set("mobile", mobile)
				.set("address", address)
				.set("secondAddress", secondAddress)
				.set("postal", zipCode)
				.set("tags", g.toJson(tags))
				.set("events", g.toJson(user.getString("events")))
				.set("role", user.getString("role"))
				.set("state", user.getString("state")).build();

		datastore.update(user);

		return Response.ok("Properties changed").build();
	}
	
	@GET
	@Path("/getUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@QueryParam("username") String username, @QueryParam("tokenId") String tokenId) {
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);
		
		if (user == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + username + " doesn't exist").build();
		}

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);
		
		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + tokenId + " doesn't exist")
					.build();

		}

		return Response.ok(g.toJson(user.getProperties().values())).build();		
	}
	
	@POST	
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
		
		LOG.info("Cheguei");
		
		String email = data.newEmail;	
		String profileType = data.newProfileType;	
		String landLine = data.newLandLine;	
		String mobile = data.newMobile;	
		String address = data.newAddress;	
		String secondAddress = data.newSecondAddress;	
		String postal = data.newPostal;	
		
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
			return Response.status(Status.FORBIDDEN).entity("User with username: " + token.getString("username") + " doesn't exist").build();	
		}	
		if (user.getString("state").equals("DISABLED")) {	
			System.out.println("The user with the given token is disabled.");	
			return Response.status(Status.NOT_ACCEPTABLE).entity("User with id: " + user.getString("username") + " is disabled.")	
					.build();	
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
		user = Entity.newBuilder(userKey).set("username", token.getString("username")).set("password", user.getString("password")).set("email", email)	
				.set("profileType", profileType).set("landLine", landLine).set("mobile", mobile).set("address", address)	
				.set("secondAddress", secondAddress).set("postal", postal).set("role", user.getString("role"))	
				.set("state", user.getString("state")).build();	
		datastore.update(user);	
		return Response.ok("Properties changed.").cookie(cookie).build();	
	}

	// op4
	@POST
	@Path("/changeRole")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeRole(@CookieParam("Token") NewCookie cookie, ChangeRoleData data) { 

		if (data.emptyParameters()) {
			System.out.println("Please fill in all fields.");
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}

		Key userToChangeKey = datastore.newKeyFactory().setKind("User").newKey(data.userToBeChanged);
		Entity userToBeChanged = datastore.get(userToChangeKey);

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName()); 
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND)
					.entity("Token with id doesn't exist").build(); 
		}

//		if(!t.validToken(tokenKey)){
//			System.out.println("The given token is expired.");
//			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenIdChangeRole +
//					" has expired. Please login again to continue using the application")
//					.build();
//		}

		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);

		if (currentUser.getString("state").equals("DISABLED")) {
			System.out.println("The user with the given token is disabled.");
			return Response.status(Status.BAD_REQUEST)
					.entity("User with id: " + currentUser.getString("username") + " is disabled.").build();
		}

		if (userToBeChanged == null) {
			System.out.println("User to be changed does not exist.");
			return Response.status(Status.FORBIDDEN).entity(data.userToBeChanged + " does not exist.").build();
		}

		if (!checkRoleChange(userToBeChanged.getString("role"), data.roleToChange, token.getString("role"))) {
			System.out.println("You do not have permissions to execute this operation.");
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}

		userToBeChanged = Entity.newBuilder(userToChangeKey).set("username", data.userToBeChanged)
				.set("password", userToBeChanged.getString("password"))
				.set("confirmation", userToBeChanged.getString("password"))
				.set("email", userToBeChanged.getString("email"))
				.set("profileType", userToBeChanged.getString("profileType"))
				.set("landLine", userToBeChanged.getString("landLine"))
				.set("mobile", userToBeChanged.getString("mobile"))
				.set("address", userToBeChanged.getString("address"))
				.set("secondAddress", userToBeChanged.getString("secondAddress"))
				.set("postal", userToBeChanged.getString("postal"))
				.set("role", data.roleToChange)
				.set("state", userToBeChanged.getString("state"))
				.build();

		datastore.update(userToBeChanged);
		return Response.ok(data.userToBeChanged + " role was changed to: " + data.roleToChange).cookie(cookie).build();

	}

	// op5
	@POST
	@Path("/op5")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeState(ChangeStateData data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key userToChangeKey = datastore.newKeyFactory().setKind("User").newKey(data.userToChange);
		Entity userToChange = datastore.get(userToChangeKey);

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + " doesn't exist")
					.build();

		if (!t.validToken(tokenKey))
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId
					+ " has expired. Please login again to continue using the application").build();

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

		if (!checkRole(userToChange.getString("role"), token.getString("role")))
			return Response.status(Status.FORBIDDEN)
					.entity("You do not have permissions to change " + data.userToChange + " state").build();

		/*
		 * END OF VERIFICATIONS
		 */

		userToChange = Entity.newBuilder(userToChangeKey).set("username", data.userToChange)
				.set("password", userToChange.getString("password"))
				.set("confirmation", userToChange.getString("password")).set("email", userToChange.getString("email"))
				.set("profileType", userToChange.getString("profileType"))
				.set("landLine", userToChange.getString("landLine")).set("mobile", userToChange.getString("mobile"))
				.set("address", userToChange.getString("address"))
				.set("secondAddress", userToChange.getString("secondAddress"))
				.set("postal", userToChange.getString("postal")).set("role", userToChange.getString("role"))
				.set("state", data.state).build();

		datastore.put(userToChange);
		return Response.ok(data.userToChange + " state was changed to: " + data.state).build();

	}

	   // op7	
    @POST	
    @Path("/logout")	
    @Consumes(MediaType.APPLICATION_JSON)	
    public Response logout(@CookieParam("Token") NewCookie cookie) {	
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());	
		Entity token = datastore.get(tokenKey);	
		if (token == null) {	
			return Response.status(Status.NOT_FOUND).entity("Token doesn't exist").build();	
		}	
		String user = token.getString("username");	
		datastore.delete(tokenKey);	
		Cookie cookiee = new Cookie("Token", null, "/", null);	
		NewCookie cookieAux = new NewCookie(cookiee,null,-1,null,true,true);	
        return Response.ok(user + " is now logged out.").cookie(cookieAux).build();	
    }

//    // op7
//    @POST
//    @Path("/logout")
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response logout(TokenData data, @CookieParam("token") NewCookie cookie) {
//
//        NewCookie cookieAux = new NewCookie(cookie.getName(), null);
//
//        if (data.tokenId.equals("")) {
//            return Response.status(Status.UNAUTHORIZED).build();
//        }
//
//        Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
//        Entity token = datastore.get(tokenKey);
//
//        if (token == null) {
//            return Response.status(Status.NOT_FOUND).entity("Token with id: " + data.tokenId + " doesn't exist").build();
//        }
//
//        String user = token.getString("username");
//
//        datastore.delete(tokenKey);
//        return Response.ok(user + " has logged out").cookie(cookieAux).build();
//    }

 // op8.1d	
 	@POST	
 	@Path("/changePassword")	
 	@Consumes(MediaType.APPLICATION_JSON)	
 	public Response changePassword(@CookieParam("Token") NewCookie cookie, ChangePasswordData data) {	
 		if (data.emptyParameters()) {	
 			System.out.println("Please fill in all non-optional fields.");	
 			return Response.status(Response.Status.UNAUTHORIZED).build();	
 		}	
 		System.out.println("NAME DO COOKIE: " + cookie.getName());	
 		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());	
 		Entity token = datastore.get(tokenKey);	
 		if (token == null) {	
 			System.out.println("The given token does not exist.");	
 			return Response.status(Status.NOT_FOUND)	
 					.entity("Token with id: " + cookie.getName() + " doesn't exist").build();	
 		}	
// 		if(!t.validToken(tokenKey))	
// 			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenIdChangePassword +	
// 					" has expired.Please login again to continue using the application")	
// 					.build();	
 		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));	
 		Entity user = datastore.get(userKey);	
 		if (user == null) {	
 			System.out.println("The user with the given token does not exist.");	
 			return Response.status(Status.FORBIDDEN).entity(token.getString("username") + " does not exist").build();	
 		}	
 		if (user.getString("state").equals("DISABLED")) {	
 			System.out.println("The user with the given token is disabled.");	
 			return Response.status(Status.NOT_ACCEPTABLE)	
 					.entity("User with id: " + user.getString("username") + " is disabled.").build();	
 		}	
 		String hashedPWD = user.getString("password");	
 		if (hashedPWD.equals(DigestUtils.sha512Hex(data.oldPassword))) {	
 			if (data.validPasswordLength()) {	
 				if (data.newPassword.equals(data.confirmation)) {	
 					user = Entity.newBuilder(userKey).set("username", token.getString("username"))	
 							.set("password", DigestUtils.sha512Hex(data.newPassword))	
 							.set("confirmation", DigestUtils.sha512Hex(data.newPassword))	
 							.set("email", user.getString("email"))	
 							.set("profileType", user.getString("profileType"))	
 							.set("landLine", user.getString("landLine"))	
 							.set("mobile", user.getString("mobile"))	
 							.set("address", user.getString("address"))	
 							.set("secondAddress", user.getString("secondAddress"))	
 							.set("postal", user.getString("postal"))	
 							.set("role", user.getString("role"))	
 							.set("state", user.getString("state"))	
// 							.set("profilePic", user.getString("profilePic"))	
// 							.set("tags", user.getString("tags"))	
// 							.set("events", user.getString("events"))	
 							.build();	
 					datastore.put(user);	
 					return Response.ok("Password was changed").cookie(cookie).build();	
 				} else {	
 					return Response.status(Status.EXPECTATION_FAILED).entity("Passwords don't match.").build();	
 				}	
 			} else {	
 				System.out.println("Invalid password. Please enter 5 or more characters.");	
 				return Response.status(Response.Status.LENGTH_REQUIRED).build();	
 			}	
 		} else {	
 			return Response.status(Status.CONFLICT).entity("Old password is incorrect.").build();	
 		}	
 	}	
 	// op8.2d	
 	@POST	
 	@Path("/op82d")	
 	@Consumes(MediaType.APPLICATION_JSON)	
 	public Response listUsersWithRole(ListRolesData data) {	
 		/*	
 		 * MAKE ALL VERIFICATIONS BEFORE METHOD START	
 		 */	
 		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);	
 		Entity token = datastore.get(tokenKey);	
 		if (token == null)	
 			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + " doesn't exist")	
 					.build();	
 		if (!t.validToken(tokenKey))	
 			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId	
 					+ " has expired.Please login again to continue using the application").build();	
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
 		} else	
 			return Response.status(Status.FORBIDDEN).entity("You do not have permissions").build();	
 	}	
 	// op8.3a	
 	@POST	
 	@Path("/op83a")	
 	@Consumes(MediaType.APPLICATION_JSON)	
 	public Response logoutUser(LogoutUserData data) {	
 		/*	
 		 * MAKE ALL VERIFICATIONS BEFORE METHOD START	
 		 */	
 		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);	
 		Entity token = datastore.get(tokenKey);	
 		if (token == null)	
 			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + " doesn't exist")	
 					.build();	
 		if (!t.validToken(tokenKey))	
 			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId	
 					+ " has expired.Please login again to continue using the application").build();	
 		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userToLogout);	
 		Entity user = datastore.get(userKey);	
 		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));	
 		Entity currentUser = datastore.get(currentUserKey);	
 		if (currentUser.getString("state").equals("DISABLED"))	
 			return Response.status(Status.BAD_REQUEST)	
 					.entity("User with id: " + currentUser.getString("username") + " is disabled.").build();	
 		if (user == null)	
 			return Response.status(Status.FORBIDDEN).entity(token.getString("username") + " does not exist").build();	
 		if (!token.getString("role").equals("GA"))	
 			return Response.status(Status.FORBIDDEN).entity("You do not have permissions to logout a user").build();	
 		/*	
 		 * END OF VERIFICATIONS	
 		 */	
 		datastore.delete(userKey);	
 		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Token")	
 				.setFilter(PropertyFilter.eq("username", data.userToLogout)).build();	
 		QueryResults<Entity> results = datastore.run(query);	
 		while (results.hasNext())	
 			datastore.delete(results.next().getKey());	
 		return Response.ok("User was removed").build();	
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
 	private boolean validateData(ChangePropertyData data) {	
 		String[] email = data.newEmail.split("\\.");	
 		String[] landLine = data.newLandLine.split(" ");	
 		String[] mobile = data.newMobile.split(" ");	
 		String[] postal = data.newPostal.split("-");	
 		int emailSize = email.length - 1;	
 		if (data.newEmail.contains("@") && (email[emailSize].length() == 2 || email[emailSize].length() == 3))	
 			if (data.newProfileType.equals("") || data.newProfileType.equalsIgnoreCase("public")	
 					|| data.newProfileType.equalsIgnoreCase("private"))	
 				if (data.newLandLine.equals("")	
 						|| (landLine[0].subSequence(0, 1).equals("+") && landLine[1].length() == 9))	
 					if (data.newPostal.equals("") || (postal[0].length() == 4 && postal[1].length() == 3))	
 						if (data.newMobile.equals("") || (mobile[0].subSequence(0, 1).equals("+")	
 								&& (mobile[1].substring(0, 2).equals("91") || mobile[1].substring(0, 2).equals("93")	
 										|| mobile[1].substring(0, 2).equals("96"))	
 								&& mobile[1].length() == 9))	
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
 		
 	private String convertToString(List<Integer> t) {	
 		return g.toJson(t);	
 	}	
 	
 }	
