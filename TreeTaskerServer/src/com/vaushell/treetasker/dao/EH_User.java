package com.vaushell.treetasker.dao;

import com.google.appengine.api.datastore.Entity;

public class EH_User
    extends A_EntityHandler
{
	// PUBLIC
	public static final String KIND = "User";
	
	public EH_User( String login,
	                String password )
	{
		super( KIND, login);
		this.login = login;
		this.password = password;
		init();
	}
	
	public EH_User(Entity entity)
	{
		super(entity);
		
		this.login = (String) entity.getKey().getName();
		this.password = (String) entity.getProperty( "password" );
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

	@Override
	public Entity getEntity()
	{
		entity.setProperty( "password", getPassword() );

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
	private String	password;

	private void init()
	{

	}

}
