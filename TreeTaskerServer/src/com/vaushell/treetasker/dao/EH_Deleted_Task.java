/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.dao;

import com.google.appengine.api.datastore.Entity;

public class EH_Deleted_Task
    extends A_EntityHandler
{
	// PUBLIC
	public static final String	KIND	= "DeletedTask";

	public EH_Deleted_Task( String taskId,
	                        EH_TT_UserTaskContainer container )
	{
		super( KIND, taskId, container.getEntity() );
		this.container = container;
		this.taskId = taskId;
		init();
	}

	public EH_Deleted_Task( Entity entity )
	{
		super( KIND, entity );
		init();
	}

	public String getTaskId()
	{
		return taskId;
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
		return entity;
	}

	@Override
	public void handleEntity( Entity entity )
	    throws E_InvalidEntityHandling
	{
		checkKindsDoMatch( entity );

		this.taskId = entity.getKey().getName();
	}

	@Override
	public int hashCode()
	{
		return taskId.hashCode();
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
		EH_Deleted_Task other = (EH_Deleted_Task) obj;
		return taskId.equals( other.taskId );
	}

	// PROTECTED
	// PRIVATE
	private String	                taskId;
	private EH_TT_UserTaskContainer	container;

	private void init()
	{
	}
}
