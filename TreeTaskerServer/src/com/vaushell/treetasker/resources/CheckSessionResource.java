package com.vaushell.treetasker.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.module.UserSession;
import com.vaushell.treetasker.module.UserSessionCheckRequest;

@Path( "/check" )
public class CheckSessionResource
{

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public UserSession sessionCheck( UserSessionCheckRequest request )
	{
		UserSession userSession = new UserSession( request.getLogin(), request.getSessionId() );
		
		if(DAO.checkUserSession( userSession ))
		{
			userSession.setSessionState( UserSession.SESSION_OK );
		}
		else
		{
			userSession.setSessionState( UserSession.SESSION_NOK );
			userSession.setSessionMessage( UserSession.MESSAGE_BAD_AUTHENTICATION );
		}

		return userSession;
	}
	
	private static final TT_ServerControllerDAO DAO = TT_ServerControllerDAO.getInstance();
}
