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
	public SyncingFinalResponse finalizeSync( SyncingFinalRequest finalisation )
	{
		try
		{
			EH_TT_UserTaskContainer datastoreContainer = DAO.getUserContainer( finalisation.getUserSession()
			                                                                               .getUserName() );

			ArrayList<EH_TT_Task> tasks = new ArrayList<EH_TT_Task>();

			for ( WS_Task task : finalisation.getUpToDateTasks() )
			{
				tasks.add( new EH_TT_Task( task, datastoreContainer ) );
			}

			DAO.createOrUpdateTasks( tasks );

			return new SyncingFinalResponse();
		}
		catch ( Throwable th )
		{
			return new SyncingFinalResponse( SyncingFinalResponse.SYNC_KO );

		}
	}
}
