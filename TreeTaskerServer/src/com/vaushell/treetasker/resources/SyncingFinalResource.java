/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.resources;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import com.vaushell.treetasker.dao.EH_TT_Task;
import com.vaushell.treetasker.dao.EH_TT_UserTaskContainer;
import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.module.SyncingFinalRequest;
import com.vaushell.treetasker.module.SyncingFinalResponse;
import com.vaushell.treetasker.module.WS_Task;

@Path( "/syncing2" )
public class SyncingFinalResource
{
	public static final TT_ServerControllerDAO	DAO	= TT_ServerControllerDAO.getInstance();

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	public SyncingFinalResponse finalizeSync(
		SyncingFinalRequest finalisation ) {
		EH_TT_UserTaskContainer datastoreContainer = DAO.getUserContainer( finalisation.getUserSession().getUserName() );

		if ( datastoreContainer != null )
		{
			ArrayList<EH_TT_Task> tasks = new ArrayList<EH_TT_Task>();

			for ( WS_Task task : finalisation.getUpToDateTasks() )
			{
				tasks.add( new EH_TT_Task( task, datastoreContainer ) );
			}

			DAO.createOrUpdateTasks( tasks );

			return new SyncingFinalResponse();
		}
		else
		{
			return new SyncingFinalResponse( SyncingFinalResponse.SYNC_KO );
		}
	}
}
