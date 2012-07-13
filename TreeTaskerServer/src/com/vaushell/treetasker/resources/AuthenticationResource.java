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

@Path( "/login" )
public class AuthenticationResource
{
	public static final DatastoreService	datastore	= DatastoreServiceFactory.getDatastoreService();

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public UserSession login( UserAuthenticationRequest request )
	{
		try
		{
			EH_User user = new EH_User(
			                            datastore.get( KeyFactory.createKey( EH_User.KIND,
			                                                                 request.getLogin() ) ) );

			if ( user.getPassword().equals( request.getPassword() ) )
			{
				return new UserSession( user.getLogin(), UUID.randomUUID()
				                                             .toString() );
			}
		}
		catch ( EntityNotFoundException e )
		{
			return new UserSession();
		}
		return new UserSession();
	}
}