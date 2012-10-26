/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class TT_UserTaskContainer
	implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	// PUBLIC
	public static final String	DEFAULT_NAME		= "default";

	public TT_UserTaskContainer()
	{
		this( DEFAULT_NAME, null );
	}

	public TT_UserTaskContainer(
		String owner )
	{
		this( DEFAULT_NAME, owner );
	}

	public TT_UserTaskContainer(
		String name,
		String owner )
	{
		this( UUID.randomUUID().toString(), name, owner );
	}

	public TT_UserTaskContainer(
		String id,
		String name,
		String owner )
	{
		this.id = id;
		this.name = name;
		this.owner = owner;
		init();
	}

	public void addRootTask(
		TT_Task rootTask ) {
		rootTasks.add( rootTask );
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public List<TT_Task> getRootTasks() {
		return rootTasks;
	}

	public void setId(
		String id ) {
		this.id = id;
	}

	public void setName(
		String name ) {
		this.name = name;
	}

	public void setOwner(
		String owner ) {
		this.owner = owner;
	}

	public void setRootTasks(
		Collection<TT_Task> tasks ) {
		rootTasks.clear();
		rootTasks.addAll( tasks );
	}

	private void init() {
		rootTasks = new LinkedList<TT_Task>();
	}

	// PROTECTED
	// PRIVATE
	private String			id;
	private String			owner;
	private String			name;

	private List<TT_Task>	rootTasks;
}
