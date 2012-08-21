package com.vaushell.treetasker.crontask;

import java.util.Date;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.vaushell.treetasker.dao.EH_RegisterValidationPending;
import com.vaushell.treetasker.dao.EH_User;

@Path( "/reg-clean" )
public class PendingRegistrationCleanResource
{
	// PUBLIC
	public static final DatastoreService	datastore	= DatastoreServiceFactory.getDatastoreService();

	@POST
	public void cleanPendingRegistrations()
	{
		Query query = new Query( EH_RegisterValidationPending.KIND );
		query.setFilter( new FilterPredicate( "expirationDate",
		                                      FilterOperator.LESS_THAN,
		                                      new Date() ) );

		List<Entity> expiredEntities = datastore.prepare( query )
		                                        .asList( FetchOptions.Builder.withDefaults() );

		if ( !expiredEntities.isEmpty() )
		{
			for ( Entity entity : expiredEntities )
			{
				datastore.delete( entity.getKey() );
				datastore.delete( KeyFactory.createKey( EH_User.KIND,
				                                        entity.getKey()
				                                              .getName() ) );
			}
		}
	}
}
