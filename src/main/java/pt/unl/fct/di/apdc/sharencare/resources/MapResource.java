package pt.unl.fct.di.apdc.sharencare.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.paging.Page;
import com.google.appengine.repackaged.com.google.gson.reflect.TypeToken;
import com.google.cloud.datastore.*;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.sharencare.util.*;

@Path("/map")
public class MapResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	private final RakingUserResource raking = new RakingUserResource();
	private final Gson g = new Gson();
	final ObjectMapper objectMapper = new ObjectMapper();
	private final Storage storage = StorageOptions.newBuilder().setProjectId("capable-sphinx-312419").build()
			.getService();

	@SuppressWarnings("unchecked")
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
		 * 
		 */
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

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
				List<TrackMedia> trackMedia = new ArrayList<TrackMedia>();
				List<TrackNotes> trackNotes = new ArrayList<TrackNotes>();
				List<TrackDangerZones> trackDangerZones = new ArrayList<TrackDangerZones>();
				List<TrackMarkers> markers = new ArrayList<TrackMarkers>();

				track = Entity.newBuilder(mapKey).set("title", data.title).set("description", data.description)
						.set("difficulty", g.toJson(data.difficulty)).set("distance", data.distance).set("time", data.time)
						.set("type", data.type).set("solidarity_points", data.solidarityPoints)
						.set("comments", g.toJson(l)).set("trackMedia", g.toJson(trackMedia))
						.set("trackNotes", g.toJson(trackNotes)).set("trackDangerZones", g.toJson(trackDangerZones))
						.set("markers", g.toJson(markers)).set("average_rating", String.valueOf(0))
						.set("username", data.username).build();

				txn.add(track);

				Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
				Entity user = datastore.get(userKey);
				String e = user.getString("my_tracks");
				List<String> tracks = new ArrayList<String>();

				if (!e.equals(""))
					tracks = g.fromJson(e, List.class);

				tracks.add(data.title);

				user = Entity.newBuilder(userKey).set("username", token.getString("username"))
						.set("password", user.getString("password")).set("email", user.getString("email"))
						.set("bio", user.getString("bio")).set("profileType", user.getString("profileType"))
						.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
						.set("address", user.getString("address")).set("secondAddress", user.getString("secondAddress"))
						.set("zipCode", user.getString("zipCode")).set("role", user.getString("role"))
						.set("state", user.getString("state")).set("tags", user.getString("tags"))
						.set("events", user.getString("events")).set("points", user.getString("points"))
						.set("my_tracks", g.toJson(tracks)).build();

				txn.update(user);

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

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		/*
		 * END OF VERIFICATIONS
		 */
		if (!data.commentIsValid()) {
			return Response.status(Status.CONFLICT).build();
		}

		if (!data.ratingIsValid()) {
			return Response.status(Status.FORBIDDEN).build();
		}

		String username = token.getString("username");
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN).entity("User with username: " + username + " doesn't exist")
					.build();

		if (user.getString("role").equals("INSTITUTION")) {
			return Response.status(Status.CONFLICT).build();
		}

		Transaction txn = datastore.newTransaction();

		try {
			Key mapKey = datastore.newKeyFactory().setKind("Track").newKey(data.routeName);
			Entity track = txn.get(mapKey);

			String commentList = track.getString("comments");

			Type comment = new TypeToken<ArrayList<ReviewData>>() {
			}.getType();
			List<ReviewData> comments = new Gson().fromJson(commentList, comment);
			List<ReviewData> newComments = new ArrayList<ReviewData>();

			for (int i = 0; i < comments.size(); i++)
				newComments.add(comments.get(i));

			if (data != null) {
				raking.addPointsComents(data.username);

				BadWordsUtil swears = new BadWordsUtil();
				if (swears.hasBadWords(data.comment) && !data.comment.equals("")) {
					return Response.status(Status.METHOD_NOT_ALLOWED).build();
				}

				newComments.add(data);
			}

			float rating = getAverageRating(Float.parseFloat(data.rating),
					Float.parseFloat(track.getString("average_rating")));

			track = Entity.newBuilder(mapKey).set("title", track.getString("title"))
					.set("time", track.getString("time"))
					.set("description", track.getString("description"))
					.set("solidarity_points", track.getString("solidarity_points"))
					.set("difficulty", track.getString("difficulty")).set("distance", track.getString("distance"))
					.set("comments", g.toJson(newComments)).set("trackMedia", track.getString("trackMedia"))
					.set("trackNotes", track.getString("trackNotes"))
					.set("trackDangerZones", track.getString("trackDangerZones"))
					.set("markers", track.getString("markers")).set("type", track.getString("type"))
					.set("username", track.getString("username")).set("average_rating", String.valueOf(rating)).build();

			txn.update(track);
			txn.commit();

			return Response.ok("Comment from " + data.username + " registered.").cookie(cookie).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/finishedTrack")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response finishedTrack(@CookieParam("Token") NewCookie cookie, FinishedTrack data) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		String username = token.getString("username");
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
		Entity user = datastore.get(userKey);

		if (user == null)
			return Response.status(Status.FORBIDDEN).entity("User with username: " + username + " doesn't exist")
					.build();

		if (user.getString("role").equals("INSTITUTION"))
			return Response.status(Status.CONFLICT).build();

		Transaction txn = datastore.newTransaction();

		try {
			Key mapKey = datastore.newKeyFactory().setKind("Track").newKey(data.routeName);
			Entity track = txn.get(mapKey);

			String media = track.getString("trackMedia");
			String notes = track.getString("trackNotes");
			String marker = track.getString("markers");
			String zones = track.getString("trackDangerZones");

			Type trackMedia = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> listTrackMedia = new Gson().fromJson(media, trackMedia);

			Type trackNotes = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> listTrackNotes = new Gson().fromJson(notes, trackNotes);

			Type trackZones = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> listTrackZones = new Gson().fromJson(zones, trackZones);

			Type trackMarker = new TypeToken<ArrayList<String>>() {
			}.getType();
			List<String> listTrackMarker = new Gson().fromJson(marker, trackMarker);

			if (!data.media.isEmpty()) {
				for (String m : data.media) {
					listTrackMedia.add(m);
				}
			}

			if (!data.notes.isEmpty()) {
				for (String n : data.notes) {
					listTrackNotes.add(n);
				}
			}

			if (!data.zones.isEmpty()) {
				for (String n : data.zones) {
					listTrackZones.add(n);
				}
			}

			if (!data.markers.isEmpty()) {
				for (String n : data.markers) {
					listTrackMarker.add(n);
				}
			}
			
			List<TrackMedia> mediaList = getMedia(media);
			
			String transformedName = transformBucketName(track.getString("title"));
			
			String bucketName = "capable-sphinx-312419" + "-" + transformedName;
			Bucket bucket = null;
			
			Page<Bucket> b = storage.list();
			boolean hasBucket = false;
			for(Bucket bu: b.getValues()) {
				if(bu.getName().equals(bucketName)) {
					hasBucket = true;
				}
				
			}
			if(!hasBucket) {
				bucket = storage.create(BucketInfo.of(bucketName));
			}else {
				bucket = storage.get(bucketName,
						Storage.BucketGetOption.fields(Storage.BucketField.values()));
			}
			
			for(TrackMedia t: mediaList) {
				if(t.image.length != 0) {
					bucket.create(t.imageName, t.image);
				}
			}
			
			track = Entity.newBuilder(mapKey).set("title", track.getString("title"))
					.set("time", track.getString("time"))
					.set("description", track.getString("description")).set("difficulty", track.getString("difficulty"))
					.set("distance", track.getString("distance")).set("comments", track.getString("comments"))
					.set("trackMedia", g.toJson(listTrackMedia)).set("trackNotes", g.toJson(listTrackNotes))
					.set("trackDangerZones", g.toJson(listTrackZones)).set("markers", g.toJson(listTrackMarker))
					.set("solidarity_points", track.getString("solidarity_points")).set("type", track.getString("type"))
					.set("average_rating", track.getString("average_rating"))
					.set("username", track.getString("username")).build();
			txn.update(track);
			txn.commit();

			return Response.ok("Track media added.").cookie(cookie).build();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
		}
	}

	@POST
	@Path("/deleteComment")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteComment(@CookieParam("Token") NewCookie cookie, RemoveCommentData data) {

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key backOfficeUserKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity backofficeUser = datastore.get(backOfficeUserKey);

		if (backofficeUser == null)
			return Response.status(Status.BAD_REQUEST).entity("BackOffice given doesn't exist").build();

		if (!backofficeUser.getString("role").equals("GA")) {
			return Response.status(Status.FORBIDDEN).build();
		}

		Key trackKey = datastore.newKeyFactory().setKind("Event").newKey(data.title);
		Entity track = datastore.get(trackKey);

		if (track == null)
			return Response.status(Status.BAD_REQUEST).entity("Track with title: " + data.title + " doesn't exist")
					.build();

		String m = track.getString("comments");

		Type stringList = new TypeToken<ArrayList<ReviewData>>() {
		}.getType();
		List<ReviewData> comment = g.fromJson(m, stringList);

		List<ReviewData> newComment = new ArrayList<ReviewData>();

		for (int i = 0; i < comment.size(); i++) {
			if (!comment.get(i).getUsername().equals(data.userOfComment)) {
				newComment.add(comment.get(i));
			}
		}

		track = Entity.newBuilder(trackKey).set("title", track.getString("title"))
				.set("time", track.getString("time"))
				.set("description", track.getString("description")).set("difficulty", track.getString("difficulty"))
				.set("distance", track.getString("distance")).set("comments", g.toJson(newComment))
				.set("trackMedia", track.getString("trackMedia")).set("trackNotes", track.getString("trackNotes"))
				.set("trackDangerZones", track.getString("trackDangerZones")).set("markers", track.getString("markers"))
				.set("solidarity_points", track.getString("solidarity_points")).set("type", track.getString("type"))
				.set("average_rating", track.getString("average_rating")).set("username", track.getString("username"))
				.build();

		datastore.update(track);

		return Response.ok("Comment deleted.").build();

	}

	@POST
	@Path("/deleteTrack")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTrack(@CookieParam("Token") NewCookie cookie, @QueryParam("title") String title) {

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key trackKey = datastore.newKeyFactory().setKind("Track").newKey(title);
		Entity track = datastore.get(trackKey);

		if (track == null)
			return Response.status(Status.BAD_REQUEST).entity("Track with title: " + title + " doesn't exist").build();

		datastore.delete(trackKey);

		return Response.ok("Event deleted.").build();

	}

	@POST
	@Path("/deleteTrackWeb")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTrack(@CookieParam("Token") NewCookie cookie, DeleteTrackData data) {

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key trackKey = datastore.newKeyFactory().setKind("Track").newKey(data.trackName);
		Entity track = datastore.get(trackKey);

		if (track == null)
			return Response.status(Status.CONFLICT).entity("There are no tracks available to be deleted.").build();

		datastore.delete(trackKey);

		return Response.ok("Event deleted.").build();

	}

	@GET
	@Path("/listAllTrack")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listAllTrack(@CookieParam("Token") NewCookie cookie) {

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		/*
		 * END OF VERIFICATIONS
		 */

		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Track").build();

		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> tracks = new ArrayList<>();
		while (eventsQuery.hasNext()) {
			String event = g.toJson(eventsQuery.next().getProperties().values());
			tracks.add(event);
		}

		return Response.ok(g.toJson(tracks)).cookie(cookie).build();

	}

	@SuppressWarnings("unchecked")
	@GET
	@Path("/listUserTrack")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listUserTrack(@CookieParam("Token") NewCookie cookie) {

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		/*
		 * END OF VERIFICATIONS
		 */

		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Track").build();

		QueryResults<Entity> eventsQuery = datastore.run(query);
		List<String> tracks = new ArrayList<>();

		ObjectMapper mapper = new ObjectMapper();
		List<String> userTracks = new ArrayList<String>();

		try {
			userTracks = Arrays.asList(mapper.readValue(user.getString("my_tracks"), String[].class));
			while (eventsQuery.hasNext()) {
				Entity t = eventsQuery.next();
				if (userTracks.contains(t.getString("title"))) {
					String track = g.toJson(t.getProperties().values());
					tracks.add(track);
				}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return Response.ok(g.toJson(tracks)).cookie(cookie).build();

	}

	private float getAverageRating(float newRating, float oldRating) {
		if (oldRating == 0)
			return newRating;
		return (newRating + oldRating) / 2;
	}

	@GET
	@Path("/getAllComments")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllComments(@CookieParam("Token") NewCookie cookie, @QueryParam("title") String title) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key trackKey = datastore.newKeyFactory().setKind("Track").newKey(title);
		Entity track = datastore.get(trackKey);

		if (track == null)
			return Response.status(Status.BAD_REQUEST).entity("Track with title: " + title + " doesn't exist").build();

		/*
		 * END OF VERIFICATIONS
		 */

		String review = track.getString("comments");

		Type reviewList = new TypeToken<ArrayList<ReviewData>>() {
		}.getType();
		List<ReviewData> reviewsList = new Gson().fromJson(review, reviewList);

		return Response.ok(g.toJson(reviewsList)).cookie(cookie).build();
	}

	@GET
	@Path("/getAllCommentsByLikes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllCommentsByLikes(@CookieParam("Token") NewCookie cookie, @QueryParam("title") String title) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		Key trackKey = datastore.newKeyFactory().setKind("Track").newKey(title);
		Entity track = datastore.get(trackKey);

		if (track == null)
			return Response.status(Status.BAD_REQUEST).entity("Track with title: " + title + " doesn't exist").build();

		/*
		 * END OF VERIFICATIONS
		 */

		String review = track.getString("comments");

		Type reviewList = new TypeToken<ArrayList<ReviewData>>() {
		}.getType();
		List<ReviewData> reviewsList = new Gson().fromJson(review, reviewList);

		Collections.sort(reviewsList);

		return Response.ok(g.toJson(reviewsList)).cookie(cookie).build();
	}
	
	
	private List<TrackMedia> getMedia(String media) {
		Type t = new TypeToken<List<String>>() {
		}.getType();
		Type t1 = new TypeToken<TrackMedia>() {
		}.getType();
		List<TrackMedia> mediaList = new ArrayList<>();

		if (media.equals("[]"))
			return mediaList;

		List<String> strings = g.fromJson(media, t);

		for (String s : strings)
			mediaList.add(g.fromJson(s, t1));
		return mediaList;
	}
	
	@POST
	@Path("/getMediaPic")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMediaPic(@CookieParam("Token") NewCookie cookie, GetMediaPic data) {
		
		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();
		
		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		if (token == null) {
			System.out.println("The given token does not exist.");
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist").build();

		}

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		}
		
		String transformedName = transformBucketName(data.title);
		
		String bucketName = "capable-sphinx-312419" + "-" + transformedName;
		Bucket bucket = null;
		if(storage.get(bucketName,Storage.BucketGetOption.fields(Storage.BucketField.values())) == null) {
			return Response.status(Status.NOT_ACCEPTABLE).build();
		}else {
			bucket = storage.get("capable-sphinx-312419-sharencare-apdc-2021",
					Storage.BucketGetOption.fields(Storage.BucketField.values()));
		}
		
		byte[] picture = null;
		Page<Blob> blobs = bucket.list();
		for (Blob blob : blobs.getValues()) {
			if (data.pic.equals(blob.getName())) {
				picture = blob.getContent();
			}
		}

		return Response.ok(g.toJson(picture)).cookie(cookie).build();
	}

	@GET
	@Path("/getTrackProperties")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTrackProperties(@CookieParam("Token") NewCookie cookie, @QueryParam("trackId") String trackId) {

		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		Key trackKey = datastore.newKeyFactory().setKind("Track").newKey(trackId);
		Entity track = datastore.get(trackKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		if (track == null)
			return Response.status(Status.BAD_REQUEST).entity("Track with id: " + trackId + " doesn't exist").build();


		/*
		 * END OF VERIFICATIONS
		 */

		List<String> res = new ArrayList<>();

		//FAZER DISTANCIA
		String u = g.toJson(track.getString("time"));
		res.add(u);

		//FAZER NUMERO DE PARTICIPANTES DA TRACK E A SUA PONTUACAO

		Query<Entity> UserQuery = Query.newEntityQueryBuilder().setKind("User").build();
		QueryResults<Entity> UsersQuery = datastore.run(UserQuery);
		while (UsersQuery.hasNext()) {
			Entity user = UsersQuery.next();
			if(user.getString("my_tracks").contains(trackId)) {
				//ADICIONA NOME DO USER A RESPOSTA
				u = g.toJson(user.getString("name"));
				res.add(u);
				//ADICIONA PONTUACAOA RESPOSTA MAS ESTA MAL
				u = g.toJson(user.getString("points"));
				res.add(u);
			}
		}

		/*
        Query<Entity> query = Query.newEntityQueryBuilder().setKind("Event").build();

        QueryResults<Entity> eventsQuery = datastore.run(query);
        List<String> events = new ArrayList<>();
        while (eventsQuery.hasNext()) {
            String event = g.toJson(eventsQuery.next().getProperties().values());
            events.add(event);
        }
*/
		return Response.ok(g.toJson(res)).cookie(cookie).build();
	}
	
	@POST
	@Path("/addTrackToUser")
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTrackToUser(@CookieParam("Token") NewCookie cookie, @QueryParam("trackId") String trackId) {
		/*
		 * MAKE ALL VERIFICATIONS BEFORE METHOD START
		 */

		if (cookie.getName().equals(""))
			return Response.status(Status.UNAUTHORIZED).build();

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(cookie.getName());
		Entity token = datastore.get(tokenKey);

		Key trackKey = datastore.newKeyFactory().setKind("Track").newKey(trackId);
		Entity track = datastore.get(trackKey);

		if (token == null)
			return Response.status(Status.NOT_FOUND).entity("Token with id: " + cookie.getName() + " doesn't exist")
					.build();

		if (track == null)
			return Response.status(Status.BAD_REQUEST).entity("Track with id: " + trackId + " doesn't exist").build();
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null) {
			System.out.println("The user with the given token does not exist.");
			return Response.status(Status.FORBIDDEN)
					.entity("User with username: " + token.getString("username") + " doesn't exist").build();
		}

		/*
		 * END OF VERIFICATIONS
		 */
		
		
		Type t = new TypeToken<List<String>>() {
		}.getType();
		
		List<String> tracks = g.fromJson(user.getString("my_tracks"), t);
		
		tracks.add(trackId);
		

		user = Entity.newBuilder(userKey).set("username", token.getString("username"))
				.set("password", user.getString("password")).set("email", user.getString("email"))
				.set("bio", user.getString("bio")).set("profileType", user.getString("profileType"))
				.set("landLine", user.getString("landLine")).set("mobile", user.getString("mobile"))
				.set("address", user.getString("address")).set("secondAddress", user.getString("secondAddress"))
				.set("zipCode", user.getString("zipCode")).set("role", user.getString("role"))
				.set("state", user.getString("state")).set("tags", user.getString("tags"))
				.set("events", user.getString("events")).set("points", user.getString("points"))
				.set("my_tracks", g.toJson(tracks)).build();

		
		datastore.update(user);
		
		return Response.ok(g.toJson(tracks)).build();
	}
	
	private String transformBucketName(String titleName) {
		String bucketName = titleName;
		
		char[] c = titleName.toCharArray();
		char dot = '.';
		char hifen = '-';
	
		
		for(int i = 0; i < c.length; i++)
			if(!(Character.isLowerCase(c[i]) || c[i] == dot || c[i] == hifen || Character.isDigit(c[i])))
				c[i] = '.';
		
		bucketName = c.toString();
		return bucketName;
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
