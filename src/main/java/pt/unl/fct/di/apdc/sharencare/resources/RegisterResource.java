package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.cloud.datastore.*;

import org.apache.commons.codec.digest.DigestUtils;
import pt.unl.fct.di.apdc.sharencare.util.RegisterData;

//import pt.unl.fct.di.apdc.APDC56253.util.LoginData;
//import pt.unl.fct.di.apdc.APDC56253.util.RegisterData;

@Path("/register")
public class RegisterResource {

    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();


    //private HttpServletRequest httpRequest;

    // op1 - registers a user
    @POST
    @Path("/user")
    @Consumes(MediaType.APPLICATION_JSON)
    //@Consumes(MediaType.MULTIPART_FORM_DATA +";charset=utf-8")
    public Response registerUser(RegisterData data) {

        System.out.println("Entrei no REGISTER_USER()");

        System.out.println(data.username);
        System.out.println(data.email);
        System.out.println(data.password);
        System.out.println(data.confirmation);
        System.out.println(data.mobile);
        System.out.println(data.address);
        System.out.println(data.postal);


        LOG.fine("Attempt to register user: " + data.username);


        //if (!validateData(data))
        //	return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data").build();

        System.out.println("ANTES DA TXN");

        Transaction txn = datastore.newTransaction();

        System.out.println("DEPOIS DA TXN");

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
            Entity user = txn.get(userKey);
            if (user != null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("User with username: " + data.username + " already exists").build();
            } else {
                user = Entity.newBuilder(userKey)
                        .set("username", data.username)
                        .set("email", data.email)
                        .set("password", DigestUtils.sha512Hex(data.password))
                        .set("mobile", data.mobile)
                        .set("address", data.address)
                        .set("postal", data.postal)
                        .set("role", "USER")
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

//		System.out.println(data.username);
//		System.out.println(data.email);
//		System.out.println(data.password);
//		System.out.println(data.confirmation);
//		System.out.println(data.mobile);
//		System.out.println(data.address);
//		System.out.println(data.postal);
//		System.out.println(data.role);
//		System.out.println(data.state);
//
//		return Response.ok().build();

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
