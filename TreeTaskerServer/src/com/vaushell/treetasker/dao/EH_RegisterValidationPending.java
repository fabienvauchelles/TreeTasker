/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.dao;

import java.util.Calendar;
import java.util.Date;

import com.google.appengine.api.datastore.Entity;

public class EH_RegisterValidationPending
    extends A_EntityHandler
{
	// PUBLIC
	public static final String	KIND	= "RegisterValidationPending";

	public EH_RegisterValidationPending( String login,
	                                     String validKey )
	{
		super( KIND, login );
		this.login = login;
		this.validKey = validKey;
		Calendar calendar = Calendar.getInstance();
		calendar.add( Calendar.HOUR_OF_DAY, 48 );
		this.expirationDate = calendar.getTime();
		init();
	}

	public EH_RegisterValidationPending( Entity entity )
	{
		super( KIND, entity );
	}

	public String getLogin()
	{
		return login;
	}

	public String getValidKey()
	{
		return validKey;
	}

	public Date getExpirationDate()
	{
		return expirationDate;
	}

	public void setExpirationDate( Date expirationDate )
	{
		this.expirationDate = expirationDate;
	}

	@Override
	public Entity getEntity()
	{
		entity.setProperty( "validKey", getValidKey() );
		entity.setProperty( "expirationDate", getExpirationDate() );

		return entity;
	}

	@Override
	public void handleEntity( Entity entity )
	    throws E_InvalidEntityHandling
	{
		checkKindsDoMatch( entity );

		this.login = (String) entity.getKey().getName();
		this.validKey = (String) entity.getProperty( "validKey" );
		this.expirationDate = (Date) entity.getProperty( "expirationDate" );
	}

	// PROTECTED
	// PRIVATE
	private String	login;
	private String	validKey;
	private Date	expirationDate;

	private void init()
	{

	}

}
