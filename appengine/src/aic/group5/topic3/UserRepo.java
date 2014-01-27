package aic.group5.topic3;

import com.google.appengine.api.datastore.*;

public class UserRepo {
	public static boolean contains( String name, String password ) {
		Entity user = getEntity( name );

		return user != null && user.getProperty( "password" ).equals( password );
	}

	public static Entity getEntity( String name ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.createKey( "User", name );

		try {
			return datastore.get( key );

		} catch( EntityNotFoundException e ) {
			return null;
		}
	}

	public static void saveEntity( Entity user ) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put( user );
	}

	public static boolean register( String name, String password ) {
		Entity user = getEntity( name );

		if( user != null ) {
			return false;
		} else {
			user = new Entity( "User", name );
			user.setProperty( "name", name );
			user.setProperty( "password", password );

			saveEntity( user );
			return true;
		}
	}
}
