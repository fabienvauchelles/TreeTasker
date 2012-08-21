package com.vaushell.treetasker.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.vaushell.treetasker.dao.EH_TT_Task;
import com.vaushell.treetasker.dao.EH_TT_UserTaskContainer;
import com.vaushell.treetasker.dao.EH_User;
import com.vaushell.treetasker.module.SyncingFinalRequest;
import com.vaushell.treetasker.module.SyncingFinalResponse;
import com.vaushell.treetasker.module.WS_Task;

@Path( "/syncing2" )
public class SyncingFinalResource
{
	public static final DatastoreService	datastore	= DatastoreServiceFactory.getDatastoreService();

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	public SyncingFinalResponse finalizeSync( SyncingFinalRequest finalisation )
	{
		Query containerQuery = new Query(
		                                  EH_TT_UserTaskContainer.KIND,
		                                  KeyFactory.createKey( EH_User.KIND,
		                                                        finalisation.getUserSession()
		                                                                    .getUserName() ) );
		containerQuery.setFilter( new Query.FilterPredicate(
		                                                     "name",
		                                                     FilterOperator.EQUAL,
		                                                     finalisation.getContainerId() ) );
		Entity datastoreContainer = datastore.prepare( containerQuery )
		                                     .asSingleEntity();

		if ( datastoreContainer != null )
		{
			EH_TT_UserTaskContainer wsContainer = new EH_TT_UserTaskContainer(
			                                                                   datastoreContainer );

			for ( WS_Task task : finalisation.getUpToDateTasks() )
			{
				EH_TT_Task wsTask = new EH_TT_Task( task, wsContainer );

				datastore.put( wsTask.getEntity() );
			}

			return new SyncingFinalResponse();
		}
		else
		{
			return new SyncingFinalResponse( SyncingFinalResponse.SYNC_KO );
		}
	}
}
