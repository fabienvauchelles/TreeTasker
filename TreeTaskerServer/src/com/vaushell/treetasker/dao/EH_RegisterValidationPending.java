package com.vaushell.treetasker.dao;

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
		init();
	}

	public EH_RegisterValidationPending( Entity entity )
	{
		super( entity );

		this.login = (String) entity.getKey().getName();
		this.validKey = (String) entity.getProperty( "validKey" );
	}

	public String getLogin()
	{
		return login;
	}

	public String getValidKey()
	{
		return validKey;
	}

	@Override
	public Entity getEntity()
	{
		entity.setProperty( "validKey", getValidKey() );

		return entity;
	}

	@Override
	public void handleEntity( Entity entity )
	    throws E_InvalidEntityHandling
	{
		checkKindsDoMatch( entity );

	}

	// PROTECTED
	// PRIVATE
	private String	login;
	private String	validKey;

	private void init()
	{

	}

}
