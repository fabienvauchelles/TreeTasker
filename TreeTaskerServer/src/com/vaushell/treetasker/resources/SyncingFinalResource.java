/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.resources;

import java.util.ArrayList;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.vaushell.treetasker.dao.EH_TT_UserTaskContainer;
import com.vaushell.treetasker.dao.EH_WS_Task;
import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.net.SyncingFinalRequest;
import com.vaushell.treetasker.net.SyncingFinalResponse;
import com.vaushell.treetasker.net.WS_Task;

@Path( "/syncing2" )
public class SyncingFinalResource
{
	public static final TT_ServerControllerDAO	DAO	= TT_ServerControllerDAO.getInstance();

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	public SyncingFinalResponse finalizeSync(
		SyncingFinalRequest finalisation ) {
		SyncingFinalResponse response = new SyncingFinalResponse();
		EH_TT_UserTaskContainer datastoreContainer = DAO.getUserContainer( finalisation.getUserSession().getUserName() );

		if ( datastoreContainer != null )
		{
			ArrayList<EH_WS_Task> tasks = new ArrayList<EH_WS_Task>();

			for ( WS_Task task : finalisation.getUpToDateTasks() )
			{
				EH_WS_Task datastoreTaskWithSamePrecedence = DAO.getNextTask( datastoreContainer, task.getPreviousId() );

				if ( datastoreTaskWithSamePrecedence != null ) // Conflit de
																// précédence
				{
					datastoreTaskWithSamePrecedence.getTask().setPreviousId( task.getId() );
					datastoreTaskWithSamePrecedence.getTask().setLastModificationDate( new Date() );
					response.getUpToDateTasks().add( datastoreTaskWithSamePrecedence.getTask() );

					tasks.add( datastoreTaskWithSamePrecedence );
				}

				tasks.add( new EH_WS_Task( task, datastoreContainer ) );
			}

			DAO.createOrUpdateTasks( tasks );
		}
		else
		{
			response.setSyncState( SyncingFinalResponse.SYNC_KO );
			response.setMessage( "[ERROR] User container not found." );
		}

		return response;
	}
}
