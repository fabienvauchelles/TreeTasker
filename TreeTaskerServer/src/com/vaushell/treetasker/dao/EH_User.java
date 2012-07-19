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
		this.validatedUser = false;
		init();
	}
	
	public EH_User(Entity entity)
	{
		super(entity);
		
		this.login = (String) entity.getKey().getName();
		this.password = (String) entity.getProperty( "password" );
		this.validatedUser = (Boolean) entity.getProperty( "validatedUser" );
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
		entity.setProperty( "password", getPassword() );
		entity.setProperty( "validatedUser", isValidatedUser() );

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
	private boolean validatedUser;

	private void init()
	{

	}

}
