package pt.unl.fct.di.apdc.sharencare.resources;

import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.apdc.sharencare.util.MarkerData;
import pt.unl.fct.di.apdc.sharencare.util.TrackData;
@Path("/map")
public class MapResource {

    private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();


    // op1 - registers a track
    @POST
    @Path("/registerTrack")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerTrack(TrackData data) {
        LOG.fine("Attempt to register track: " + data.title);

            /*
        if (!validateData(data))
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data").build();*/
        

        Transaction txn = datastore.newTransaction();

        try {
            Key mapKey = datastore.newKeyFactory().setKind("Track").newKey(data.title);
            Entity track = txn.get(mapKey);
            if (track != null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Track with name: " + data.title + " already exists").build();
            } else {
                track = Entity.newBuilder(mapKey)
                        .set("title", data.title)
                        .set("description", data.description)
                        .set("difficulty", data.difficulty)
                        .set("distance", data.distance)
                        .set("origin", data.origin)
                        .set("destination", data.destination)
                        .build();


                txn.add(track);
                txn.commit();
                return Response.ok("Track registered " + data.title).build();
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }

    // op2 - registers a marker
    @POST
    @Path("registerMarker")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerMarker(MarkerData data) {
        LOG.fine("Attempt to register marker: " + data.coordinates);

            /*
        if (!validateData(data))
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid data").build();
*/


        Transaction txn = datastore.newTransaction();

        try {
            Key mapKey = datastore.newKeyFactory().setKind("Marker").newKey(data.coordinates);
            Entity marker = txn.get(mapKey);
            if (marker != null) {
                txn.rollback();
                return Response.status(Response.Status.BAD_REQUEST).entity("Marker with coordinates: " + data.coordinates + " already exists").build();
            } else {
                marker = Entity.newBuilder(mapKey)
                        .set("description", data.description)
                        .set("coordinates", data.coordinates)
                        .build();


                txn.add(marker);
                txn.commit();
                return Response.ok("Marker registered " + data.coordinates).build();
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }

    }
/*
    //checks if all data is valid
    private boolean validateData(RegisterTrackData data) {

        String[] email = data.email.split("\\.");
        String[] landLine = data.landLine.split(" ");
        String[] mobile = data.mobile.split(" ");
        String[] postal = data.postal.split("-");

        int emailSize = email.length - 1;

        if (data.email.contains("@") && (email[emailSize].length() == 2 || email[emailSize].length() == 3))
            if (data.password.equals(data.confirmation))
                if (data.profileType.equals("") || data.profileType.equalsIgnoreCase("Publico") || data.profileType.equalsIgnoreCase("Privado"))
                    if (data.landLine.equals("") || (landLine[0].subSequence(0, 1).equals("+") && landLine[1].length() == 9))
                        if (data.postal.equals("") || (postal[0].length() == 4 && postal[1].length() == 3))
                            if (data.mobile.equals("") || (mobile[0].subSequence(0, 1).equals("+") && (
                                    mobile[1].substring(0, 2).equals("91") ||
                                            mobile[1].substring(0, 2).equals("93") ||
                                            mobile[1].substring(0, 2).equals("96"))
                                    && mobile[1].length() == 9))
                                return true;
        return false;
    }
*/
}
