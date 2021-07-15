package pt.unl.fct.di.apdc.sharencare.resources;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.gson.Gson;

@Path("/delete")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class DeleteUsersResource {
	
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();

	@POST
	@Path("/user")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUser(@QueryParam("tokenId") String tokenId, @QueryParam("username") String username) {
		
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId + " doesn't exist")
					.build();
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.BAD_REQUEST).entity("User with username: " + username + " doesn't exist").build();
		
		datastore.delete(userKey);

		return Response.ok("User deleted.").build();

	}
	
	@POST
	@Path("/institution")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteInstitution(@QueryParam("tokenId") String tokenId, @QueryParam("username") String nif) {
		
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.BAD_REQUEST).entity("Token with id: " + tokenId + " doesn't exist")
					.build();
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(nif);
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.BAD_REQUEST).entity("Institution with nif: " + nif + " doesn't exist").build();
		
		datastore.delete(userKey);

		return Response.ok("Institution deleted.").build();

	}

}
