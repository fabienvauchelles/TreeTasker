package com.vaushell.treetasker.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.module.UserAuthenticationRequest;
import com.vaushell.treetasker.module.UserSession;

@Path( "/login" )
public class AuthenticationResource
{

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public UserSession login( UserAuthenticationRequest request )
	{
		return DAO.authenticateUser( request.getLogin(), request.getPassword() );
	}
	
	private static final TT_ServerControllerDAO DAO = TT_ServerControllerDAO.getInstance();
}
