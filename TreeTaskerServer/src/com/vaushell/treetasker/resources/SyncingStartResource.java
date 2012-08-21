package com.vaushell.treetasker.resources;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.vaushell.treetasker.dao.EH_Deleted_Task;
import com.vaushell.treetasker.dao.EH_TT_Task;
import com.vaushell.treetasker.dao.EH_TT_UserTaskContainer;
import com.vaushell.treetasker.dao.EH_User;
import com.vaushell.treetasker.module.SyncingStartRequest;
import com.vaushell.treetasker.module.SyncingStartResponse;
import com.vaushell.treetasker.module.TaskStamp;

@Path( "/syncing1" )
public class SyncingStartResource
{
	public static final DatastoreService	datastore	= DatastoreServiceFactory.getDatastoreService();

	@SuppressWarnings( "unused" )
	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public SyncingStartResponse sync( SyncingStartRequest request )
	{
		Entity containerEntity;
		HashMap<String, EH_TT_Task> datastoreTasks = null;

		Query containerQuery = new Query(
		                                  EH_TT_UserTaskContainer.KIND,
		                                  KeyFactory.createKey( EH_User.KIND,
		                                                        request.getUserSession()
		                                                               .getUserName() ) );
		containerQuery.setFilter( new Query.FilterPredicate(
		                                                     "name",
		                                                     FilterOperator.EQUAL,
		                                                     request.getContainerId() ) );

		containerEntity = datastore.prepare( containerQuery ).asSingleEntity();

		if ( containerEntity != null )
		{
			datastoreTasks = new HashMap<String, EH_TT_Task>();
			Query tasksQuery = new Query( EH_TT_Task.KIND,
			                              containerEntity.getKey() );
			for ( Entity taskEntity : datastore.prepare( tasksQuery )
			                                   .asIterable( FetchOptions.Builder.withDefaults() ) )
			{
				EH_TT_Task task = new EH_TT_Task( taskEntity );
				datastoreTasks.put( task.getTask().getID(), task );
			}
		}
		else
		{
			containerEntity = new Entity(
			                              EH_TT_UserTaskContainer.KIND,
			                              UUID.randomUUID().toString(),
			                              KeyFactory.createKey( EH_User.KIND,
			                                                    request.getUserSession()
			                                                           .getUserName() ) );
			containerEntity.setProperty( "name",
			                             request.getContainerId() );
			datastore.put( containerEntity );
		}

		SyncingStartResponse response = new SyncingStartResponse();
		response.setDate( new Date() );

		// Liste des ids des noeuds présents sur le téléphone
		for ( TaskStamp taskStamp : request.getIdList() )
		{
			System.err.println( "date: " + taskStamp.getLastModificationDate() );
			if ( datastoreTasks != null )
			{
				datastoreTasks.remove( taskStamp.getTaskID() );
			}

			try
			{
				Entity entity = datastore.get( KeyFactory.createKey( containerEntity.getKey(),
				                                                     EH_TT_Task.KIND,
				                                                     taskStamp.getTaskID() ) );
				EH_TT_Task datastoreTask = new EH_TT_Task( entity );

				if ( datastoreTask.getTask()
				                  .getLastModificationDate()
				                  .after( taskStamp.getLastModificationDate() ) ) // Le
								                                                  // serveur
								                                                  // a
								                                                  // une
								                                                  // version
								                                                  // plus
								                                                  // récente
				{
					response.addMoreRecentTask( datastoreTask.getTask() );
				}
				else if ( datastoreTask.getTask()
				                       .getLastModificationDate()
				                       .before( taskStamp.getLastModificationDate() ) )
				{
					response.addNeedUpdateId( taskStamp.getTaskID() );
				}
			}
			catch ( EntityNotFoundException e )
			{
				try
				{
					Entity entity = datastore.get( KeyFactory.createKey( containerEntity.getKey(),
					                                                     EH_Deleted_Task.KIND,
					                                                     taskStamp.getTaskID() ) );
					response.addDeletedId( taskStamp.getTaskID() );
				}
				catch ( EntityNotFoundException ex )
				{
					response.addNeedUpdateId( taskStamp.getTaskID() );
				}
			}
		}

		if ( datastoreTasks != null )
		{
			for ( EH_TT_Task ttTask : datastoreTasks.values() )
			{
				response.addTaskToAdd( ttTask.getTask() );
			}
		}

		// Liste des ids des noeuds supprimés sur le téléphone
		for ( String taskId : request.getRemovedIdList() )
		{
			if ( taskId != null
			     && !taskId.trim().isEmpty() )
			{
				try
				{
					Entity entity = datastore.get( KeyFactory.createKey( containerEntity.getKey(),
					                                                     EH_TT_Task.KIND,
					                                                     taskId ) );
					datastore.delete( entity.getKey() );
					datastore.put( new Entity( EH_Deleted_Task.KIND,
					                           taskId ) );
				}
				catch ( EntityNotFoundException e )
				{
				}
				response.addDeletedId( taskId );
			}
		}

		return response;
	}
}
