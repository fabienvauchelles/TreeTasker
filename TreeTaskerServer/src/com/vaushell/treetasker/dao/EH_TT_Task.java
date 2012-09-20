package com.vaushell.treetasker.dao;

import java.util.Date;

import com.google.appengine.api.datastore.Entity;
import com.vaushell.treetasker.module.WS_Task;

public class EH_TT_Task
    extends A_EntityHandler
{
	// PUBLIC
	public static final String	KIND	                 = "Task";

	public static final String	PROPERTY_PARENT_ID	     = "parent-id";
	public static final String	PROPERTY_TITLE	         = "title";
	public static final String	PROPERTY_DESC	         = "description";
	public static final String	PROPERTY_LAST_MODIF_DATE	= "last-modif-date";
	public static final String	PROPERTY_STATUS	         = "status";
	public static final String	PROPERTY_STATE	         = "state";

	public EH_TT_Task( WS_Task task,
	                   EH_TT_UserTaskContainer container )
	{
		super( KIND, task.getId(), container.getEntity() );
		this.container = container;
		this.task = task;
		init();
	}

	public EH_TT_Task( Entity entity )
	{
		super( KIND, entity );
		init();
	}

	public WS_Task getTask()
	{
		return task;
	}

	public EH_TT_UserTaskContainer getContainer()
	{
		return container;
	}

	public void setContainer( EH_TT_UserTaskContainer container )
	{
		this.container = container;
	}

	@Override
	public Entity getEntity()
	{
		entity.setProperty( PROPERTY_TITLE, task.getTitle() );
		entity.setProperty( PROPERTY_DESC, task.getDescription() );
		entity.setProperty( PROPERTY_LAST_MODIF_DATE,
		                    task.getLastModificationDate() );
		entity.setProperty( PROPERTY_STATUS, task.getStatus() );
		entity.setProperty( PROPERTY_PARENT_ID, task.getParentId() );

		return entity;
	}

	@Override
	public void handleEntity( Entity entity )
	    throws E_InvalidEntityHandling
	{
		checkKindsDoMatch( entity );

		this.task = new WS_Task(
		                         (String) entity.getKey().getName(),
		                         (String) entity.getProperty( PROPERTY_TITLE ),
		                         (String) entity.getProperty( PROPERTY_DESC ),
		                         (Date) entity.getProperty( PROPERTY_LAST_MODIF_DATE ),
		                         ( (Long) entity.getProperty( PROPERTY_STATUS ) ).intValue(),
		                         (String) entity.getProperty( PROPERTY_PARENT_ID ) );
	}

	@Override
	public int hashCode()
	{
		return task.hashCode();
	}

	@Override
	public boolean equals( Object obj )
	{
		if ( this == obj )
			return true;
		if ( obj == null )
			return false;
		if ( getClass() != obj.getClass() )
			return false;
		EH_TT_Task other = (EH_TT_Task) obj;
		return task.equals( other.task );
	}

	// PROTECTED
	// PRIVATE
	private WS_Task	                task;
	private EH_TT_UserTaskContainer	container;

	private void init()
	{
	}
}
