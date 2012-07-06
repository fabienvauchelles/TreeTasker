package com.vaushell.treetasker.dao;

import java.util.UUID;

import com.google.appengine.api.datastore.Entity;

public abstract class A_EntityHandler
{
	// PUBLIC
	public A_EntityHandler( Entity entity )
	{
		if ( entity == null )
			throw new NullPointerException();

		this.entity = entity;
	}

	public A_EntityHandler( String kind )
	{
		this( kind, UUID.randomUUID().toString() );
	}

	public A_EntityHandler( String kind,
	                        String key )
	{
		if ( kind == null
		     || kind.trim().isEmpty()
		     || key == null
		     || key.trim().isEmpty() )
			throw new E_InvalidEntityHandling(
			                                    String.format( "Illegal kind or key used: kind='%s' key=''",
			                                                   kind,
			                                                   key ) );
		this.entity = new Entity( kind, key );
	}

	public A_EntityHandler( String kind,
	                        long key )
	{
		if ( kind == null
		     || kind.trim().isEmpty() )
			throw new E_InvalidEntityHandling(
			                                    String.format( "Illegal kind used: kind='%s' key=''",
			                                                   kind,
			                                                   key ) );
		this.entity = new Entity( kind, key );
	}

	public String getKind()
	{
		return entity.getKind();
	}

	public abstract Entity getEntity();

	public abstract void handleEntity( Entity entity )
	    throws E_InvalidEntityHandling;

	// PROTECTED
	protected Entity	entity;
	
	protected void checkKindsDoMatch(Entity entity)
	{
		if ( !entity.getKind().equals( getKind() ) )
			throw new E_InvalidEntityHandling(
			                                    String.format( "Bad kind of entity in argument: kind='%d' argument.kind='%d'",
			                                                   entity.getKind(),
			                                                   getKind() ) );
	}

	// PRIVATE
	@SuppressWarnings( "unused" )
	private A_EntityHandler() // On empêche de pouvoir créer une entity sans
		                      // type (kind)
	{
	}
}
