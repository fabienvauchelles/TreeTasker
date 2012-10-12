/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;
import com.vaushell.treetasker.model.TT_UserTaskContainer;

public class EH_TT_UserTaskContainer
	extends A_EntityHandler
	implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public static final String	KIND				= "TaskContainer";

	public static final String	PROPERTY_NAME		= "name";
	public static final String	PROPERTY_OWNER		= "owner";

	public EH_TT_UserTaskContainer(
		Entity entity )
	{
		super( KIND, entity );
		init();
	}

	public EH_TT_UserTaskContainer(
		TT_UserTaskContainer container )
	{
		super( KIND, container.getId() );
		this.container = container;
		init();
	}

	public void addChildTask(
		EH_WS_Task childTask ) {
		childrenTasks.add( childTask );
	}

	public List<EH_WS_Task> getChildrenTasks() {
		return childrenTasks;
	}

	public TT_UserTaskContainer getContainer() {
		return container;
	}

	@Override
	public Entity getEntity() {
		entity.setProperty( PROPERTY_NAME, container.getName() );
		entity.setProperty( PROPERTY_OWNER, container.getOwner() );

		return entity;
	}

	@Override
	public void handleEntity(
		Entity entity )
		throws E_InvalidEntityHandling {
		checkKindsDoMatch( entity );

		container = new TT_UserTaskContainer( entity.getKey().getName(), (String) entity.getProperty( PROPERTY_NAME ),
			(String) entity.getProperty( PROPERTY_OWNER ) );
	}

	public void setChildrenTasks(
		List<EH_WS_Task> childrenTasks ) {
		this.childrenTasks = childrenTasks;
	}

	public void setContainer(
		TT_UserTaskContainer container ) {
		this.container = container;
	}

	private void init() {
		childrenTasks = new ArrayList<EH_WS_Task>();
	}

	// PROTECTED
	// PRIVATE
	private TT_UserTaskContainer	container;

	private List<EH_WS_Task>		childrenTasks;
}
