/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.resources;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.vaushell.treetasker.dao.EH_TT_UserTaskContainer;
import com.vaushell.treetasker.dao.EH_WS_Task;
import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.net.SyncingStartRequest;
import com.vaushell.treetasker.net.SyncingStartResponse;
import com.vaushell.treetasker.net.TaskStamp;

@Path( "/syncing1" )
public class SyncingStartResource
{
	public static final TT_ServerControllerDAO	DAO	= TT_ServerControllerDAO.getInstance();

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public SyncingStartResponse sync(
		SyncingStartRequest request ) {
		EH_TT_UserTaskContainer userContainer = DAO.getUserContainer( request.getUserSession().getUserName() );

		HashMap<String, EH_WS_Task> datastoreTasksMap = new HashMap<String, EH_WS_Task>();

		for ( EH_WS_Task task : DAO.getAllTasks( userContainer ) )
		{
			datastoreTasksMap.put( task.getTask().getId(), task );
		}

		SyncingStartResponse response = new SyncingStartResponse();

		// Liste des ids des noeuds présents sur le téléphone
		for ( TaskStamp taskStamp : request.getIdList() )
		{
			EH_WS_Task datastoreTask = datastoreTasksMap.get( taskStamp.getTaskID() );

			if ( datastoreTask != null )
			{
				if ( datastoreTask.getTask().getLastModificationDate().after( taskStamp.getLastModificationDate() ) ) // Le
																														// serveur
																														// a
																														// une
																														// version
																														// plus
																														// récente
				{
					response.addMoreRecentTask( datastoreTask.getTask() );
				}
				else if ( datastoreTask.getTask().getLastModificationDate()
					.before( taskStamp.getLastModificationDate() ) )
				{
					response.addNeedUpdateId( taskStamp.getTaskID() );
				}
			}
			else
			{
				if ( DAO.isTaskDeleted( userContainer, taskStamp.getTaskID() ) )
				{
					response.addDeletedId( taskStamp.getTaskID() );
				}
				else
				{
					response.addNeedUpdateId( taskStamp.getTaskID() );
				}
			}

			datastoreTasksMap.remove( taskStamp.getTaskID() );
		}

		for ( EH_WS_Task ttTask : datastoreTasksMap.values() )
		{
			response.addTaskToAdd( ttTask.getTask() );
		}

		// Liste des ids des noeuds supprimés sur le téléphone
		for ( String taskId : request.getRemovedIdList() )
		{
			if ( taskId != null && !taskId.trim().isEmpty() )
			{
				EH_WS_Task datastoreTask = DAO.getTask( userContainer, taskId );

				if ( datastoreTask != null )
				{
					DAO.deleteTask( datastoreTask );
				}
				response.addDeletedId( taskId );
			}
		}

		return response;
	}
}
