/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.dao;

import com.google.appengine.api.datastore.Entity;

public abstract class A_EntityHandler
{
	// PUBLIC
	public A_EntityHandler( String kind,
	                        Entity entity )
	{
		if ( entity == null
		     || kind == null )
			throw new NullPointerException();

		this.kind = kind;
		checkKindsDoMatch( entity );
		this.entity = entity;
		
		this.handleEntity( entity );
	}

	public A_EntityHandler( String kind,
	                        String key )
	{
		if ( kind == null
		     || kind.trim().isEmpty()
		     || key == null
		     || key.trim().isEmpty() )
			throw new E_InvalidEntityHandling(
			                                   String.format( "Illegal kind or key used: kind='%s' key='%s'",
			                                                  kind,
			                                                  key ) );
		this.kind = kind;
		this.entity = new Entity( kind, key );
	}

	public A_EntityHandler( String kind,
	                        String key,
	                        Entity parentEntity )
	{
		if ( kind == null
		     || kind.trim().isEmpty()
		     || key == null
		     || key.trim().isEmpty() )
			throw new E_InvalidEntityHandling(
			                                   String.format( "Illegal kind or key used: kind='%s' key='%s'",
			                                                  kind,
			                                                  key ) );
		this.kind = kind;
		this.entity = new Entity( kind, key, parentEntity.getKey() );
	}

	public A_EntityHandler( String kind,
	                        long key )
	{
		if ( kind == null
		     || kind.trim().isEmpty() )
			throw new E_InvalidEntityHandling(
			                                   String.format( "Illegal kind used: kind='%s' key='%s'",
			                                                  kind,
			                                                  key ) );
		this.kind = kind;
		this.entity = new Entity( kind, key );
	}

	public A_EntityHandler( String kind,
	                        long key,
	                        Entity parentEntity )
	{
		if ( kind == null
		     || kind.trim().isEmpty() )
			throw new E_InvalidEntityHandling(
			                                   String.format( "Illegal kind used: kind='%s' key='%s'",
			                                                  kind,
			                                                  key ) );
		this.kind = kind;
		this.entity = new Entity( kind, key, parentEntity.getKey() );
	}

	public String getKind()
	{
		return kind;
	}

	public abstract Entity getEntity();

	public abstract void handleEntity( Entity entity )
	    throws E_InvalidEntityHandling;

	// PROTECTED
	protected String	kind;
	protected Entity	entity;

	protected void checkKindsDoMatch( Entity entity )
	{
		if ( !entity.getKind().equals( kind ) )
			throw new E_InvalidEntityHandling(
			                                   String.format( "Bad kind of entity in argument: kind='%s' argument.kind='%s'",
			                                                  kind,
			                                                  entity.getKind() ) );
	}

	// PRIVATE
	@SuppressWarnings( "unused" )
	private A_EntityHandler() // On empêche de pouvoir créer une entity sans
		                      // type (kind)
	{
	}
}
