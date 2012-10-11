/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TT_UserTaskContainer
		implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// PUBLIC
	public static final String	DEFAULT_NAME	= "default";

	public TT_UserTaskContainer()
	{
		this( DEFAULT_NAME, null );
	}

	public TT_UserTaskContainer( String owner )
	{
		this( DEFAULT_NAME, owner );
	}

	public TT_UserTaskContainer( String name,
	                             String owner )
	{
		this( UUID.randomUUID().toString(), name, owner );
	}

	public TT_UserTaskContainer( String id,
	                             String name,
	                             String owner )
	{
		this.id = id;
		this.name = name;
		this.owner = owner;
		init();
	}

	public String getId()
	{
		return id;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public String getOwner()
	{
		return owner;
	}

	public void setOwner( String owner )
	{
		this.owner = owner;
	}

	public String getName()
	{
		return name;
	}

	public void setName( String name )
	{
		this.name = name;
	}

	public Set<TT_Task> getRootTasks()
	{
		return rootTasks;
	}
	
	public void setRootTasks( Set<TT_Task> tasks )
	{
		rootTasks.clear();
		rootTasks.addAll(tasks);
	}

	public void addRootTask( TT_Task rootTask )
	{
		rootTasks.add( rootTask );
	}

	// PROTECTED
	// PRIVATE
	private String	     id;
	private String	     owner;
	private String	     name;
	private Set<TT_Task>	rootTasks;

	private void init()
	{
		this.rootTasks = new HashSet<TT_Task>();
	}
}
