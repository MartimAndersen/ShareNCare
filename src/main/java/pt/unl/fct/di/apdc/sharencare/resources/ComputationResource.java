package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.ArrayList;
import java.util.List;
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
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.RemoveUserData;
import pt.unl.fct.di.apdc.sharencare.util.ShowByRole;
import pt.unl.fct.di.apdc.sharencare.util.UpdatePassword;
import pt.unl.fct.di.apdc.sharencare.util.UpdateRole;
import pt.unl.fct.di.apdc.sharencare.util.UpdateState;
import pt.unl.fct.di.apdc.sharencare.util.UpdateValues;

@Path("/utils")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ComputationResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Gson g = new Gson();

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public ComputationResource() {
	} // nothing to be done here @GET

	@POST
	@Path("/removes")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeAccount(RemoveUserData data) {
		LOG.fine("Remove attempt for user: " + data.username);

		Key userKeyToRemove = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity user1 = datastore.get(userKeyToRemove);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity userByToken = datastore.get(tokenKey);
		if (user1 != null) {
			if (userByToken != null) {
				if (data.username.equals(userByToken.getString("username"))) {
					datastore.delete(userKeyToRemove);
					datastore.delete(tokenKey);
					return Response.ok(g.toJson(data.username)).build();
				} else if ((userByToken.getString("role").equals("GBO") || userByToken.getString("role").equals("GA")
						|| userByToken.getString("role").equals("SU")) && user1.getString("role").equals("USER")) {
					datastore.delete(userKeyToRemove);
					return Response.ok(g.toJson(data.username)).build();
				} else if ((userByToken.getString("role").equals("GA") || userByToken.getString("role").equals("SU"))
						&& user1.getString("role").equals("GBO")) {
					datastore.delete(userKeyToRemove);
					return Response.ok(g.toJson(data.username)).build();
				} else {
					return Response.status(Status.BAD_REQUEST).entity("Invalid operation.").build();
				}
			} else {
				return Response.status(Status.FORBIDDEN).entity("You are not logged in").build();
			}
		} else {
			return Response.status(Status.FORBIDDEN).entity("Username does not exist.").build();
		}
	}

	@PUT
	@Path("/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateAttributes(UpdateValues data) {
		LOG.fine("Update attempt by user: " + data.username);

		String perfil, morada, phone1, phone2, codigoPostal;
		Key userKeyToUpdate = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity userByToken = datastore.get(tokenKey);
		Entity user1 = datastore.get(userKeyToUpdate);
		if (user1 != null) {
			if (userByToken != null) {
				if (data.username.equals(userByToken.getString("username"))) {
					if (data.perfil.equals("") && data.perfilVerification()) {
						perfil = user1.getString("perfil");
					} else {
						perfil = data.perfil;
					}
					if (data.phone1.equals("")) {
						phone1 = user1.getString("phone1");
					} else {
						phone1 = data.phone1;
					}
					if (data.phone2.equals("")) {
						phone2 = user1.getString("phone2");
					} else {
						phone2 = data.phone2;
					}
					if (data.morada.equals("")) {
						morada = user1.getString("morada");
					} else {
						morada = data.morada;
					}
					if (data.codigoPostal.equals("")) {
						codigoPostal = user1.getString("codigoPostal");
					} else {
						codigoPostal = data.codigoPostal;
					}
					user1 = Entity.newBuilder(userKeyToUpdate).set("email", user1.getString("email"))
							.set("password", user1.getString("password")).set("role", user1.getString("role"))
							.set("state", user1.getString("state")).set("perfil", perfil).set("phone1", phone1)
							.set("phone2", phone2).set("morada", morada).set("codigoPostal", codigoPostal).build();
					datastore.put(user1);
					return Response.ok(g.toJson(user1)).build();
				} else {
					return Response.status(Status.BAD_REQUEST).entity("Only can update your own atributes.").build();
				}
			} else {
				return Response.status(Status.FORBIDDEN).entity("You are not logged in").build();
			}
		} else {
			return Response.status(Status.FORBIDDEN).entity("Username does not exist.").build();
		}

	}

	@PUT
	@Path("/updaterole")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateRole(UpdateRole data) {
		LOG.fine("Update attempt by user: " + data.username);
		Key userKeyByToken = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Key userKeyToUpdateRole = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity userByToken = datastore.get(userKeyByToken);
		Entity userToUpdateRole = datastore.get(userKeyToUpdateRole);
		if (userToUpdateRole != null) {
			if (userByToken != null) {
				if ((userByToken.getString("role").equals("GA") || userByToken.getString("role").equals("SU"))
						&& userToUpdateRole.getString("role").equals("USER")) {
					userToUpdateRole = Entity.newBuilder(userKeyToUpdateRole)
							.set("email", userToUpdateRole.getString("email"))
							.set("password", userToUpdateRole.getString("password")).set("role", data.role)
							.set("state", userToUpdateRole.getString("state"))
							.set("perfil", userToUpdateRole.getString("perfil"))
							.set("phone1", userToUpdateRole.getString("phone1"))
							.set("phone2", userToUpdateRole.getString("phone2"))
							.set("morada", userToUpdateRole.getString("morada"))
							.set("codigoPostal", userToUpdateRole.getString("codigoPostal")).build();
					datastore.put(userByToken);
					datastore.put(userToUpdateRole);
					return Response.ok(g.toJson(userToUpdateRole)).build();
				} else if (userToUpdateRole.getString("role").equals("USER")
						&& userByToken.getString("role").equals("SU")) {
					userToUpdateRole = Entity.newBuilder(userKeyToUpdateRole)
							.set("email", userToUpdateRole.getString("email"))
							.set("password", userToUpdateRole.getString("password")).set("role", data.role)
							.set("state", userToUpdateRole.getString("state"))
							.set("perfil", userToUpdateRole.getString("perfil"))
							.set("phone1", userToUpdateRole.getString("phone1"))
							.set("phone2", userToUpdateRole.getString("phone2"))
							.set("morada", userToUpdateRole.getString("morada"))
							.set("codigoPostal", userToUpdateRole.getString("codigoPostal")).build();
					datastore.put(userByToken);
					datastore.put(userToUpdateRole);
					return Response.ok(g.toJson(userToUpdateRole)).build();
				} else {
					return Response.status(Status.BAD_REQUEST).entity("Invalid operation.").build();
				}
			} else {
				return Response.status(Status.FORBIDDEN).entity("You are not logged in").build();
			}
		} else {
			return Response.status(Status.FORBIDDEN).entity("Username does not exist.").build();
		}
	}

	@PUT
	@Path("/updatestate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateState(UpdateState data) {
		LOG.fine("Update attempt by user: " + data.username);

		Key userKeyByToken = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Key userKeyToUpdateState = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Entity userByToken = datastore.get(userKeyByToken);
		Entity userToUpdateState = datastore.get(userKeyToUpdateState);
		if (userToUpdateState != null) {
			if (userByToken != null) {
				if ((userByToken.getString("role").equals("GA") || userByToken.getString("role").equals("SU")
						|| userByToken.getString("role").equals("GBO"))
						&& userToUpdateState.getString("role").equals("USER")) {
					userToUpdateState = Entity.newBuilder(userKeyToUpdateState)
							.set("email", userToUpdateState.getString("email"))
							.set("password", userToUpdateState.getString("password"))
							.set("role", userToUpdateState.getString("role")).set("state", data.state)
							.set("perfil", userToUpdateState.getString("perfil"))
							.set("phone1", userToUpdateState.getString("phone1"))
							.set("phone2", userToUpdateState.getString("phone2"))
							.set("morada", userToUpdateState.getString("morada"))
							.set("codigoPostal", userToUpdateState.getString("codigoPostal")).build();
					datastore.put(userToUpdateState);
					return Response.ok(g.toJson(userToUpdateState)).build();
				} else if ((userByToken.getString("role").equals("GA") || userByToken.getString("role").equals("SU"))
						&& userToUpdateState.getString("role").equals("GBO")) {
					userToUpdateState = Entity.newBuilder(userKeyToUpdateState)
							.set("email", userToUpdateState.getString("email"))
							.set("password", userToUpdateState.getString("password"))
							.set("role", userToUpdateState.getString("role")).set("state", data.state)
							.set("perfil", userToUpdateState.getString("perfil"))
							.set("phone1", userToUpdateState.getString("phone1"))
							.set("phone2", userToUpdateState.getString("phone2"))
							.set("morada", userToUpdateState.getString("morada"))
							.set("codigoPostal", userToUpdateState.getString("codigoPostal")).build();
					datastore.put(userToUpdateState);
					return Response.ok(g.toJson(userToUpdateState)).build();
				} else if (userByToken.getString("role").equals("SU")
						&& userToUpdateState.getString("role").equals("GA")) {
					userToUpdateState = Entity.newBuilder(userKeyToUpdateState)
							.set("email", userToUpdateState.getString("email"))
							.set("password", userToUpdateState.getString("password"))
							.set("role", userToUpdateState.getString("role")).set("state", data.state)
							.set("perfil", userToUpdateState.getString("perfil"))
							.set("phone1", userToUpdateState.getString("phone1"))
							.set("phone2", userToUpdateState.getString("phone2"))
							.set("morada", userToUpdateState.getString("morada"))
							.set("codigoPostal", userToUpdateState.getString("codigoPostal")).build();
					datastore.put(userToUpdateState);
					return Response.ok(g.toJson(userToUpdateState)).build();
				} else {
					return Response.status(Status.BAD_REQUEST).entity("Invalid operation.").build();
				}
			} else {
				return Response.status(Status.FORBIDDEN).entity("You are not logged in").build();
			}
		} else {
			return Response.status(Status.FORBIDDEN).entity("Username does not exist.").build();
		}
	}

	@PUT
	@Path("/updatepassword")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updatePassword(UpdatePassword data) {
		LOG.fine("Update attempt by user: " + data.username);

		Key userKeyToUpdate = datastore.newKeyFactory().setKind("User").newKey(data.username);
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity userByToken = datastore.get(tokenKey);
		Entity userToUpdate = datastore.get(userKeyToUpdate);
		if (userToUpdate != null) {
			if (userByToken != null) {
				if (data.password.equals(data.passwordCom) && userToUpdate.getString("role").equals("USER")) {
					if (DigestUtils.sha512Hex(data.passwordOld).equals(userToUpdate.getString("password"))) {
						userToUpdate = Entity.newBuilder(userKeyToUpdate).set("email", userToUpdate.getString("email"))
								.set("password", DigestUtils.sha512Hex(data.password))
								.set("role", userToUpdate.getString("role"))
								.set("state", userToUpdate.getString("state"))
								.set("perfil", userToUpdate.getString("perfil"))
								.set("phone1", userToUpdate.getString("phone1"))
								.set("phone2", userToUpdate.getString("phone2"))
								.set("morada", userToUpdate.getString("morada"))
								.set("codigoPostal", userToUpdate.getString("codigoPostal")).build();
						datastore.put(userToUpdate);
						return Response.ok(g.toJson(userToUpdate)).build();

					} else {
						return Response.status(Status.BAD_REQUEST).entity("Old password is wrong").build();
					}
				} else {
					return Response.status(Status.BAD_REQUEST).entity("Passwords dont match or user is not role USER.")
							.build();
				}
			} else {
				return Response.status(Status.FORBIDDEN).entity("You are not logged in.").build();
			}

		} else {
			return Response.status(Status.FORBIDDEN).entity("Username does not exist.").build();
		}

	}

	@POST
	@Path("/getbyrole")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getByRole(ShowByRole data) {
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(data.tokenId);
		Entity userByToken = datastore.get(tokenKey);
		if (userByToken != null) {
			if (userByToken.getString("role").equals("GBO")) {
				Query<Entity> query = Query.newEntityQueryBuilder().setKind("User")
						.setFilter(PropertyFilter.eq("role", data.role)).build();
				QueryResults<Entity> logs = datastore.run(query);
				List<Key> roleUsers = new ArrayList<Key>();
				logs.forEachRemaining(user -> {
					roleUsers.add(user.getKey());
				});
				return Response.ok(g.toJson(roleUsers)).build();
			} else {
				return Response.status(Status.BAD_REQUEST).entity("You are not a GBO.").build();
			}
		} else {
			return Response.status(Status.FORBIDDEN).entity("You are not logged in.").build();
		}
	}

}
