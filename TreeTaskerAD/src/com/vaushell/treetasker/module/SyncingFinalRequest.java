/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.module;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SyncingFinalRequest
{
	// PUBLIC
	public SyncingFinalRequest()
	{
		this( null );
	}

	public SyncingFinalRequest( UserSession userSession )
	{
		this( userSession, null );
	}

	public SyncingFinalRequest( UserSession userSession,
	                            String containerId )
	{
		this.userSession = userSession;
		this.containerId = containerId;
		init();
	}

	public UserSession getUserSession()
	{
		return userSession;
	}

	public void setUserSession( UserSession userSession )
	{
		this.userSession = userSession;
	}

	public String getContainerId()
	{
		return containerId;
	}

	public void setContainerId( String containerId )
	{
		this.containerId = containerId;
	}

	public Set<WS_Task> getUpToDateTasks()
	{
		return upToDateTasks;
	}

	public void setUpToDateTasks( Set<WS_Task> upToDateTasks )
	{
		this.upToDateTasks = upToDateTasks;
	}

	public void addUpToDateTask( WS_Task task )
	{
		this.upToDateTasks.add( task );
	}

	// PROTECTED
	// PRIVATE
	private UserSession	 userSession;
	private String	     containerId;
	private Set<WS_Task>	upToDateTasks;

	private void init()
	{
		this.upToDateTasks = new HashSet<WS_Task>();
	}
}
