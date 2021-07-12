package pt.unl.fct.di.apdc.sharencare.filters;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

@Secured
@Provider
public class AuthenticationFilter implements ContainerRequestFilter {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// get cookie from request
		Map<String, Cookie> cookies = requestContext.getCookies();

		// Extract the token from the Authorization header
		String token = cookies.keySet().iterator().next();

		try {
			// Validate the token
			validateToken(requestContext, token);

		} catch (Exception e) {
			abortWithUnauthorized(requestContext);
		}
	}

	private void abortWithUnauthorized(ContainerRequestContext requestContext) {

		// Abort the filter chain with a 401 status code response
		// The WWW-Authenticate header is sent along with the response
		requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
	}

	private void validateToken(ContainerRequestContext requestContext, String tokenId) throws Exception {

		if (tokenId.equals(""))
			abortWithUnauthorized(requestContext);

		Key tokenKey = datastore.newKeyFactory().setKind("Token").newKey(tokenId);
		Entity token = datastore.get(tokenKey);

		if (token == null)
			abortWithUnauthorized(requestContext);

		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);

		if (user == null)
			abortWithUnauthorized(requestContext);

		if (user.getString("state").equals("DISABLED"))
			abortWithUnauthorized(requestContext);

		String verifier = user.getString("username").concat(user.getString("role"));

		if (!token.getString("verifier").equals(verifier))
			abortWithUnauthorized(requestContext);

		if (token.getBoolean("expirable") && token.getLong("expirationData") > System.currentTimeMillis()) {
			datastore.delete(userKey);
			abortWithUnauthorized(requestContext);
		}
	}

}
