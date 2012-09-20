package com.vaushell.treetasker.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.module.UserAuthenticationRequest;
import com.vaushell.treetasker.module.UserSession;

@Path( "/register" )
public class RegisterResource
{
	public static final TT_ServerControllerDAO	DAO	= TT_ServerControllerDAO.getInstance();

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public UserSession register( UserAuthenticationRequest request )
	{
		return DAO.registerUser( request.getLogin(), request.getPassword() );
	}
}
