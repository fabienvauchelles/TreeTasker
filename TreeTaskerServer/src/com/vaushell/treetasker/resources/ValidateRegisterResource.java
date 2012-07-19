package com.vaushell.treetasker.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.vaushell.treetasker.dao.EH_RegisterValidationPending;
import com.vaushell.treetasker.dao.EH_User;

@Path( "/valid" )
public class ValidateRegisterResource
{
	// PUBLIC
	@GET
	@Produces( MediaType.TEXT_PLAIN )
	public String validateUser( @QueryParam( "username" ) String username,
	                            @QueryParam( "valid-key" ) String validKey )
	{
		try
		{
			EH_RegisterValidationPending rvp = new EH_RegisterValidationPending(
			                                                                     datastore.get( KeyFactory.createKey( EH_RegisterValidationPending.KIND,
			                                                                                                          username ) ) );

			if ( validKey.equals( rvp.getValidKey() ) )
			{
				EH_User user = new EH_User(
				                            datastore.get( KeyFactory.createKey( EH_User.KIND,
				                                                                 username ) ) );
				user.setValidatedUser( true );
				datastore.put( user.getEntity() );
				datastore.delete( rvp.getEntity().getKey() );
				
				return "Validation réussie !";
			}
			else
			{
				return "Validation échouée...";
			}
		}
		catch ( EntityNotFoundException e )
		{

			return "Validation échouée...";
		}
	}

	private static final DatastoreService	datastore	= DatastoreServiceFactory.getDatastoreService();
}
