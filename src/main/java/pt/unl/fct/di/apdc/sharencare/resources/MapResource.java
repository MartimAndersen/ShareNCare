package pt.unl.fct.di.apdc.sharencare.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.repackaged.com.google.gson.reflect.TypeToken;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.ReviewData;
import pt.unl.fct.di.apdc.sharencare.util.MarkerData;
import pt.unl.fct.di.apdc.sharencare.util.TrackData;

@Path("/map")
public class MapResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final Gson g = new Gson();
	final ObjectMapper objectMapper = new ObjectMapper();

	@POST
	@Path("/registerTrack")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerTrack(@CookieParam("Token") NewCookie cookie, TrackData data) {
		
		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (data.title.equals("")) 
			return Response.status(Status.FORBIDDEN).build();
		
		if (cookie.getName().equals(""))
			return Response.status(Status.BAD_REQUEST).build();
		
		/*
		 * END OF VERIFICATIONS
		 */


		Transaction txn = datastore.newTransaction();

		try {
			Key mapKey = datastore.newKeyFactory().setKind("Track").newKey(data.title);
			Entity track = txn.get(mapKey);
			if (track != null) {
				txn.rollback();
				return Response.status(Status.CONFLICT).entity("The track with the given title already exists.")
						.build();
			} else {
				List<ReviewData> l = new ArrayList<ReviewData>();
				track = Entity.newBuilder(mapKey).set("title", data.title).set("description", data.description)
						.set("origin", data.origin).set("destination", data.destination)
						.set("difficulty", data.difficulty).set("distance", data.distance).set("comments", g.toJson(l)).build();

				txn.add(track);
				txn.commit();
				return Response.ok("Track " + data.title + " registered.").cookie(cookie).build();
			}
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/registerMarker")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerMarker(MarkerData data) {
		
		Transaction txn = datastore.newTransaction();

		try {
			Key mapKey = datastore.newKeyFactory().setKind("Marker").newKey(data.coordinates);
			Entity marker = txn.get(mapKey);
			if (marker != null) {
				txn.rollback();
				return Response.status(Response.Status.BAD_REQUEST)
						.entity("Marker with coordinates: " + data.coordinates + " already exists").build();
			} else {
				marker = Entity.newBuilder(mapKey).set("description", data.description)
						.set("coordinates", data.coordinates).build();

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

	@POST
	@Path("/comment")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerComment(@CookieParam("Token") NewCookie cookie, ReviewData data) {
		
		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();
		
		/*
		 * END OF VERIFICATIONS
		 */
		if(data.commentIsValid()) {
			return Response.status(Status.CONFLICT).build();
		}
		
		if(data.ratingIsValid()) {
			return Response.status(Status.FORBIDDEN).build();
		}


		Transaction txn = datastore.newTransaction();

		try {
			Key mapKey = datastore.newKeyFactory().setKind("Track").newKey(data.routeName);
			Entity track = txn.get(mapKey);
			
			String commentList = track.getString("comment");
			
			Type comment = new TypeToken<ArrayList<ReviewData>>(){}.getType();
			List<ReviewData> comments = new Gson().fromJson(commentList, comment);
			List<ReviewData> newComments = new ArrayList<ReviewData>();

			for (int i = 0; i < comments.size(); i++)
				newComments.add(comments.get(i));

			newComments.add(data);

			track = Entity.newBuilder(mapKey).set("title", track.getString("title"))
					.set("description", track.getString("description")).set("origin", track.getString("origin"))
					.set("destination", track.getString("destination")).set("difficulty", track.getString("difficulty"))
					.set("distance", track.getString("distance")).set("comments", g.toJson(newComments)).build();

			txn.add(track);
			txn.commit();
			
			return Response.ok("Comment from " + data.username + " registered.").cookie(cookie).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	/*
	 * //checks if all data is valid private boolean validateData(RegisterTrackData
	 * data) {
	 * 
	 * String[] email = data.email.split("\\."); String[] landLine =
	 * data.landLine.split(" "); String[] mobile = data.mobile.split(" "); String[]
	 * postal = data.postal.split("-");
	 * 
	 * int emailSize = email.length - 1;
	 * 
	 * if (data.email.contains("@") && (email[emailSize].length() == 2 ||
	 * email[emailSize].length() == 3)) if (data.password.equals(data.confirmation))
	 * if (data.profileType.equals("") ||
	 * data.profileType.equalsIgnoreCase("public") ||
	 * data.profileType.equalsIgnoreCase("private")) if (data.landLine.equals("") ||
	 * (landLine[0].subSequence(0, 1).equals("+") && landLine[1].length() == 9)) if
	 * (data.postal.equals("") || (postal[0].length() == 4 && postal[1].length() ==
	 * 3)) if (data.mobile.equals("") || (mobile[0].subSequence(0, 1).equals("+") &&
	 * ( mobile[1].substring(0, 2).equals("91") || mobile[1].substring(0,
	 * 2).equals("93") || mobile[1].substring(0, 2).equals("96")) &&
	 * mobile[1].length() == 9)) return true; return false; }
	 */
}
