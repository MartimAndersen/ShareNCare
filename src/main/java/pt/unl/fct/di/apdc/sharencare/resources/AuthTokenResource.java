package pt.unl.fct.di.apdc.sharencare.resources;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;

public class AuthTokenResource {

	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

	public boolean validToken(Key tokenKey) {

		Entity token = datastore.get(tokenKey);
		
		Key userKey = datastore.newKeyFactory().setKind("User").newKey(token.getString("username"));
		Entity user = datastore.get(userKey);
		
		String verifier = user.getString("username").concat(user.getString("role"));
		
		if (!token.getBoolean("valid")) 
			return false;
		
		if(!token.getString("verifier").equals(verifier))
			return false;
		
		if (token.getLong("expirationData") > System.currentTimeMillis() || user == null) 
			return true;
			
			token = Entity.newBuilder(tokenKey)
					.set("username", token.getString("username"))
					.set("role", token.getString("role"))
					.set("creationData", token.getLong("creationData"))
					.set("expirationData", token.getLong("expirationData"))
					//.set("verifier", token.getString("verifier"))
					.set("valid", false)
					.build();
			
			return false;
		}

	
}
