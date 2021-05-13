package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.datastore.*;

import pt.unl.fct.di.apdc.sharencare.util.RegisterData;

//import pt.unl.fct.di.apdc.APDC56253.util.LoginData;
//import pt.unl.fct.di.apdc.APDC56253.util.RegisterData;

@Path("/register")
public class RegisterResource {

	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	// op1 - registers an user
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(RegisterData data) {
		LOG.fine("Attempt to register user: " + data.username);

		if (!validateData(data))
			return Response.status(Status.BAD_REQUEST).entity("Invalid data").build();

		Transaction txn = datastore.newTransaction();

		try {
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			if (user != null) {
				txn.rollback();
				return Response.status(Status.BAD_REQUEST)
						.entity("User with username: " + data.username + " already exists").build();
			} else {
				user = Entity.newBuilder(userKey).set("email", data.email)
						.set("password", DigestUtils.sha512Hex(data.password)).set("mobile", data.mobile)
						.set("adress", data.adress).set("postal", data.postal).set("role", "USER")
						.set("state", "ENABLED").build();

				txn.add(user);
				txn.commit();
				return Response.ok("User registered " + data.username).build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}

	}

	// checks if all data is valid
	private boolean validateData(RegisterData data) {

		String[] email = data.email.split("\\.");
		String[] mobile = data.mobile.split(" ");
		String[] postal = data.postal.split("-");

		int emailSize = email.length - 1;

		if (data.email.contains("@") && (email[emailSize].length() == 2 || email[emailSize].length() == 3))
			if (data.password.equals(data.confirmation))
				if (data.postal.equals("") || (postal[0].length() == 4 && postal[1].length() == 3))
					if (data.mobile.equals("")
							|| (mobile[0].subSequence(0, 1).equals("+") && (mobile[1].substring(0, 2).equals("91")
									|| mobile[1].substring(0, 2).equals("93") || mobile[1].substring(0, 2).equals("96"))
									&& mobile[1].length() == 9))
						return true;
		return false;
	}

}
