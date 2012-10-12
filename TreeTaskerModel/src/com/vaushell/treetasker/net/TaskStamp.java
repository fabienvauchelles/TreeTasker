/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.net;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class TaskStamp
{
	// PUBLIC
	public TaskStamp()
	{
		this( null, null );
	}

	public TaskStamp( String taskID,
	                  Date lastModificationDate )
	{
		this.taskID = taskID;
		this.lastModificationDate = lastModificationDate;
		init();
	}

	public String getTaskID()
	{
		return taskID;
	}

	public void setTaskID( String taskID )
	{
		this.taskID = taskID;
	}

	@XmlJavaTypeAdapter(JerseyDateAdapter.class)
	public Date getLastModificationDate()
	{
		return lastModificationDate;
	}

	public void setLastModificationDate( Date lastModificationDate )
	{
		this.lastModificationDate = lastModificationDate;
	}

	// PROTECTED
	// PRIVATE
	private String	taskID;
	private Date	lastModificationDate;

	private void init()
	{

	}
}
