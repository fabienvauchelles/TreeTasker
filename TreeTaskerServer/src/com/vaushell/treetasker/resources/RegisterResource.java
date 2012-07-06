package com.vaushell.treetasker.resources;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.vaushell.treetasker.dao.EH_User;
import com.vaushell.treetasker.module.UserAuthenticationRequest;
import com.vaushell.treetasker.module.UserSession;

@Path( "/register" )
public class RegisterResource
{
	public static final DatastoreService	datastore	= DatastoreServiceFactory.getDatastoreService();

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public UserSession register( UserAuthenticationRequest request )
	{
		try
		{
			datastore.get( KeyFactory.createKey( EH_User.KIND,
			                                     request.getLogin() ) );
		}
		catch ( EntityNotFoundException e )
		{
			EH_User newUser = new EH_User( request.getLogin(),
			                               request.getPassword() );
			datastore.put( newUser.getEntity() );

			return new UserSession( request.getLogin(), UUID.randomUUID()
			                                                .toString() );
		}
		return new UserSession();
	}
}
