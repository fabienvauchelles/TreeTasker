/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.crontask;

import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
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

	@GET
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
