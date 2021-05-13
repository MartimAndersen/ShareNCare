package pt.unl.fct.di.apdc.sharencare.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.ChangePasswordData;
import pt.unl.fct.di.apdc.sharencare.util.ChangePropertyData;
import pt.unl.fct.di.apdc.sharencare.util.ChangeRoleData;
import pt.unl.fct.di.apdc.sharencare.util.ChangeStateData;
import pt.unl.fct.di.apdc.sharencare.util.ListRolesData;
import pt.unl.fct.di.apdc.sharencare.util.LogoutUserData;
import pt.unl.fct.di.apdc.sharencare.util.RemoveUserData;
import pt.unl.fct.di.apdc.sharencare.util.TokenData;

import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

@Path("/loggedIn")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class InsideLoginResource {
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	AuthTokenResource t = new AuthTokenResource();

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
		
		if(!t.validToken(tokenKey)) 
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + 
					" has expired. Please login again to continue using the application")
					.build();
		
		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);
		
		if(currentUser.getString("state") == "DISABLED")
			return Response.status(Status.BAD_REQUEST).entity("User with id: " + currentUser.getString("username") + " is disabled.")
					.build();
		
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
		return Response.ok(data.userToDelete + " was sucessfully removed").build();

	}

	// op3
	@POST
	@Path("/op3")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeProperty(ChangePropertyData data) {

		String email = data.email;
		String profileType = data.profileType;
		String landLine = data.landLine;
		String mobile = data.mobile;
		String adress = data.adress;
		String secondAdress = data.secondAdress;
		String postal = data.postal;
		
		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + " doesn't exist")
					.build();

		if(!t.validToken(tokenKey)) 
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + 
					" has expired. Please login again to continue using the application")
					.build();
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);
		
		if (user == null)
			return Response.status(Status.BAD_REQUEST)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		
		if(user.getString("state") == "DISABLED")
			return Response.status(Status.BAD_REQUEST).entity("User with id: " + user.getString("username") + " is disabled.")
					.build();
		

		if (data.email == "")
			email = user.getString("email");
		if (data.profileType == "")
			profileType = user.getString("profileType");
		if (data.landLine == "")
			landLine = user.getString("landLine");
		if (data.mobile == "")
			mobile = user.getString("mobile");
		if (data.adress == "")
			adress = user.getString("adress");
		if (data.secondAdress == "")
			secondAdress = user.getString("secondAdress");
		if (data.postal == "")
			postal = user.getString("postal");

		if (!validateData(data))
			return Response.status(Status.BAD_REQUEST).entity("Invalid data").build();
		
		/*
		 * END OF VERIFICATIONS
		 */

		user = Entity.newBuilder(userKey).set("password", user.getString("password")).set("email", email)
				.set("profileType", profileType).set("landLine", landLine).set("mobile", mobile).set("adress", adress)
				.set("secondAdress", secondAdress).set("postal", postal).set("role", user.getString("role"))
				.set("state", user.getString("state")).build();

		datastore.update(user);
		return Response.ok("Properties changed").build();
	}

	// op4
	@POST
	@Path("/op4")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changeRole(ChangeRoleData data) {
		
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
		
		if(!t.validToken(tokenKey)) 
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + 
					" has expired. Please login again to continue using the application")
					.build();
		
		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);
		
		if(currentUser.getString("state") == "DISABLED")
			return Response.status(Status.BAD_REQUEST).entity("User with id: " + currentUser.getString("username") + " is disabled.")
					.build();


		if (userToChange == null)
			return Response.status(Status.FORBIDDEN).entity(data.userToChange + " does not exist").build();
		

		if (!checkRoleChange(userToChange.getString("role"), data.roleToChange, token.getString("role")))
			return Response.status(Status.FORBIDDEN)
					.entity("You do not have permissions to change " + data.userToChange + " role").build();
		
		/*
		 * END OF VERIFICATIONS
		 */

		userToChange = Entity.newBuilder(userToChangeKey).set("password", userToChange.getString("password"))
				.set("email", userToChange.getString("email")).set("profileType", userToChange.getString("profileType"))
				.set("landLine", userToChange.getString("landLine")).set("mobile", userToChange.getString("mobile"))
				.set("adress", userToChange.getString("adress"))
				.set("secondAdress", userToChange.getString("secondAdress"))
				.set("postal", userToChange.getString("postal")).set("role", data.roleToChange)
				.set("state", userToChange.getString("state")).build();

		datastore.update(userToChange);
		return Response.ok(data.userToChange + " role was changed to: " + data.roleToChange).build();

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
		
		if(!t.validToken(tokenKey)) 
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + 
					" has expired. Please login again to continue using the application")
					.build();
		
		Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity currentUser = datastore.get(currentUserKey);
		
		if(currentUser.getString("state") == "DISABLED")
			return Response.status(Status.BAD_REQUEST).entity("User with id: " + currentUser.getString("username") + " is disabled.")
					.build();
		

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

		userToChange = Entity.newBuilder(userToChangeKey).set("password", userToChange.getString("password"))
				.set("email", userToChange.getString("email")).set("profileType", userToChange.getString("profileType"))
				.set("landLine", userToChange.getString("landLine")).set("mobile", userToChange.getString("mobile"))
				.set("adress", userToChange.getString("adress"))
				.set("secondAdress", userToChange.getString("secondAdress"))
				.set("postal", userToChange.getString("postal")).set("role", userToChange.getString("role"))
				.set("state", data.state).build();

		datastore.put(userToChange);
		return Response.ok(data.userToChange + " state was changed to: " + data.state).build();

	}

	// op7
	@POST
	@Path("/op7")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response logout(TokenData data) throws IOException {
		
		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + " doesn't exist")
					.build();
		
		/*
		 * END OF VERIFICATIONS
		 */

		String user = token.getString("username");

		datastore.delete(tokenKey);
		return Response.ok(user + " has logged out").build();
	}
	// op8.1d
		@POST
		@Path("/op81a")
		@Consumes(MediaType.APPLICATION_JSON)
		public Response listPublicUsers(TokenData data) {
			
			/*
			 * MAKE ALL VERIFICATIONS BEFORE METHOD START
			 */
			
			Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
			Entity token = datastore.get(tokenKey);
	
			if (token == null)
				return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + " doesn't exist")
						.build();
			
			if(!t.validToken(tokenKey)) 
				return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + 
						" has expired. Please login again to continue using the application")
						.build();
			
			Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
			Entity currentUser = datastore.get(currentUserKey);
			
			if(currentUser.getString("state") == "DISABLED")
				return Response.status(Status.BAD_REQUEST).entity("User with id: " + currentUser.getString("username") + " is disabled.")
						.build();

			/*
			 * END OF VERIFICATIONS
			 */
			
			Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
					.setFilter(PropertyFilter.eq("profileType", "Publico")).build();

			QueryResults<Entity> results = datastore.run(query);
			List<String> r = new ArrayList<String>();

			if(results == null)
				return Response.status(Status.NO_CONTENT).entity("No users with profile type 'Publico'").build();
			
			while (results.hasNext())
				r.add(results.next().getKey().getName());

			return Response.ok(g.toJson(r)).build();
			
		}
		
	// op8.1d
	@POST
	@Path("/op81d")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response changePassword(ChangePasswordData data) {
		
		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + " doesn't exist")
					.build();
		
		if(!t.validToken(tokenKey)) 
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + 
					" has expired.Please login again to continue using the application")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);
		

		if (user == null)
			return Response.status(Status.FORBIDDEN).entity(token.getString("username") + " does not exist").build();
		
		
		if(user.getString("state") == "DISABLED")
			return Response.status(Status.BAD_REQUEST).entity("User with id: " + user.getString("username") + " is disabled.")
					.build();
		
		/*
		 * END OF VERIFICATIONS
		 */

		String hashedPWD = user.getString("password");
		if (hashedPWD.equals(DigestUtils.sha512Hex(data.oldPassword))) {
			if (data.newPassword.equals(data.confirmation)) {

				user = Entity.newBuilder(userKey).set("password", DigestUtils.sha512Hex(data.newPassword))
						.set("email", user.getString("email")).set("profileType", user.getString("profileType"))
						.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
						.set("adress", user.getString("adress")).set("secondAdress", user.getString("secondAdress"))
						.set("postal", user.getString("postal")).set("role", user.getString("role"))
						.set("state", user.getString("state")).build();

				datastore.put(user);
				return Response.ok("Password was changed").build();
			}
			return Response.status(Status.BAD_REQUEST).entity("Passwords don't match").build();
		}
		return Response.status(Status.BAD_REQUEST).entity("Old password is incorrect").build();
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
		
		if(!t.validToken(tokenKey)) 
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + 
					" has expired.Please login again to continue using the application")
					.build();
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);
		
		if(user == null)
			return Response.status(Status.BAD_REQUEST).entity("User doesn't exist")
					.build();
		
		if(user.getString("state") == "DISABLED")
			return Response.status(Status.BAD_REQUEST).entity("User with id: " + user.getString("username") + " is disabled.")
					.build();
		
		/*
		 * END OF VERIFICATIONS
		 */

		String userRole = token.getString("role");

		if (userRole.equals("GBO") || userRole.equals("GA")) {

			Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
					.setFilter(PropertyFilter.eq("role", data.role)).build();

			QueryResults<Entity> results = datastore.run(query);
			List<String> r = new ArrayList<String>();

			while (results.hasNext())
				r.add(results.next().getKey().getName());

			return Response.ok(g.toJson(r)).build();
		}
		else
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
			
			if(!t.validToken(tokenKey)) 
				return Response.status(Status.BAD_REQUEST).entity("Token with id: " + data.tokenId + 
						" has expired.Please login again to continue using the application")
						.build();
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.userToLogout);
			Entity user = datastore.get(userKey);
			
			Key currentUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
			Entity currentUser = datastore.get(currentUserKey);
			
			if(currentUser.getString("state") == "DISABLED")
				return Response.status(Status.BAD_REQUEST).entity("User with id: " + currentUser.getString("username") + " is disabled.")
						.build();

			if (user == null)
				return Response.status(Status.FORBIDDEN).entity(token.getString("username") + " does not exist").build();
			
			if(!token.getString("role").equals("GA"))
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
		
		String[] email = data.email.split("\\.");
		String[] landLine = data.landLine.split(" ");
		String[] mobile = data.mobile.split(" ");
		String[] postal = data.postal.split("-");

		int emailSize = email.length-1;
		
		if (data.email.contains("@") && (email[emailSize].length() == 2 || email[emailSize].length() == 3))
					if (data.profileType.equals("") || data.profileType.equalsIgnoreCase("Publico") || data.profileType.equalsIgnoreCase("Privado"))
						if (data.landLine.equals("") || (landLine[0].subSequence(0, 1).equals("+") && landLine[1].length() == 9)) 
							if(data.postal.equals("") || (postal[0].length() == 4 && postal[1].length() == 3))
								if(data.mobile.equals("") || (mobile[0].subSequence(0, 1).equals("+") && (
															  mobile[1].substring(0, 2).equals("91") || 
															  mobile[1].substring(0, 2).equals("93") || 
															  mobile[1].substring(0, 2).equals("96"))
														   && mobile[1].length() == 9))
																		return true;
		return false;
	}
	
	private boolean checkRoleChange(String roleOfUser, String roleToChange, String currentUserRole) {
		if (!roleOfUser.equals("USER")) {
			if (roleToChange.equals("GBO") && (currentUserRole.equals("SU") || currentUserRole.equals("GA")))
				return true;
			else if (roleToChange.equals("GA") && currentUserRole.equals("SU"))
				return true;
		}
		return false;
	}

}
