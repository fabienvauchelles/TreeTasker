package com.vaushell.treetasker.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TT_UserTaskContainer
{
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
