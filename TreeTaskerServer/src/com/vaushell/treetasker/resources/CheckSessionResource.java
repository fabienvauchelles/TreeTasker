package com.vaushell.treetasker.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.vaushell.treetasker.module.UserSession;
import com.vaushell.treetasker.module.UserSessionCheckRequest;

@Path( "/check" )
public class CheckSessionResource
{
	public static final DatastoreService	datastore	= DatastoreServiceFactory.getDatastoreService();

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public UserSession sessionCheck( UserSessionCheckRequest request )
	{
		try
		{
			Entity datastoreUserSession = datastore.get( KeyFactory.createKey( "UserSession",
			                                                                   request.getSessionId() ) );

			if ( request.getLogin() != null
			     && request.getLogin()
			               .equals( datastoreUserSession.getProperty( "username" ) ) )
			{
				return new UserSession( request.getLogin(),
				                        request.getSessionId() );
			}
			else
			{
				UserSession session = new UserSession();
				session.setSessionMessage( UserSession.MESSAGE_NO_SESSION_AVAILABLE );
				return session;
			}
		}
		catch ( EntityNotFoundException e )
		{
			UserSession session = new UserSession();
			session.setSessionMessage( UserSession.MESSAGE_NO_SESSION_AVAILABLE );
			return session;
		}
	}
}
