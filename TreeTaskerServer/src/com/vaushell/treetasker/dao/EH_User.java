/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.dao;

import com.google.appengine.api.datastore.Entity;

public class EH_User
    extends A_EntityHandler
{
	// PUBLIC
	public static final String	KIND	                = "User";

	public static final String	PROPERTY_PASSWORD	    = "password";
	public static final String	PROPERTY_VALIDATED_USER	= "validatedUser";

	public EH_User( String login,
	                String password )
	{
		super( KIND, login );
		this.login = login;
		this.password = password;
		this.validatedUser = false;
		init();
	}

	public EH_User( Entity entity )
	{
		super( KIND, entity );
	}

	public String getLogin()
	{
		return login;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	public boolean isValidatedUser()
	{
		return validatedUser;
	}

	public void setValidatedUser( boolean validatedUser )
	{
		this.validatedUser = validatedUser;
	}

	@Override
	public Entity getEntity()
	{
		entity.setProperty( PROPERTY_PASSWORD, getPassword() );
		entity.setProperty( PROPERTY_VALIDATED_USER, isValidatedUser() );

		return entity;
	}

	@Override
	public void handleEntity( Entity entity )
	    throws E_InvalidEntityHandling
	{
		this.login = (String) entity.getKey().getName();
		this.password = (String) entity.getProperty( PROPERTY_PASSWORD );
		this.validatedUser = (Boolean) entity.getProperty( PROPERTY_VALIDATED_USER );

		init();
	}

	// PROTECTED
	// PRIVATE
	private String	login;
	private String	password;
	private boolean	validatedUser;

	private void init()
	{
	}

}
