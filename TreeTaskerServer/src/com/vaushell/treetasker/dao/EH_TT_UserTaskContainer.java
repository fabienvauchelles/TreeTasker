package com.vaushell.treetasker.dao;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.appengine.api.datastore.Entity;
import com.vaushell.treetasker.model.TT_UserTaskContainer;

public class EH_TT_UserTaskContainer
    extends A_EntityHandler
    implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// PUBLIC
	public static final String	KIND	       = "TaskContainer";

	public static final String	PROPERTY_NAME	= "name";
	public static final String	PROPERTY_OWNER	= "owner";

	public EH_TT_UserTaskContainer( TT_UserTaskContainer container )
	{
		super( KIND, container.getId() );
		this.container = container;
		init();
	}

	public EH_TT_UserTaskContainer( Entity entity )
	{
		super( KIND, entity );
		init();
	}

	public TT_UserTaskContainer getContainer()
	{
		return container;
	}

	public void setContainer(TT_UserTaskContainer container)
	{
		this.container = container;
	}


	public Set<EH_TT_Task> getChildrenTasks()
	{
		return childrenTasks;
	}

	public void setChildrenTasks(Set<EH_TT_Task> childrenTasks) 
	{
		this.childrenTasks = childrenTasks;
	}
	
	public void addChildTask( EH_TT_Task childTask )
	{
		this.childrenTasks.add( childTask );
	}

	@Override
	public Entity getEntity()
	{
		entity.setProperty( PROPERTY_NAME, container.getName() );
		entity.setProperty( PROPERTY_OWNER, container.getOwner() );

		return entity;
	}

	@Override
	public void handleEntity( Entity entity )
	    throws E_InvalidEntityHandling
	{
		checkKindsDoMatch( entity );

		container = new TT_UserTaskContainer(
		                                      entity.getKey().getName(),
		                                      (String) entity.getProperty( PROPERTY_NAME ),
		                                      (String) entity.getProperty( PROPERTY_OWNER ) );
	}

	// PROTECTED
	// PRIVATE
	private TT_UserTaskContainer	container;
	private Set<EH_TT_Task>	     childrenTasks;

	private void init()
	{
		this.childrenTasks = new HashSet<EH_TT_Task>();
	}
}
