/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.net;

import java.util.Date;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.vaushell.treetasker.model.TT_Task;

@XmlRootElement
public class WS_Task
{
	public static final int	TODO	= 0;
	public static final int	DONE	= 1;
	public static final int	DELETED	= 2;

	public WS_Task()
	{
		this( null, null, null, null, TODO, null, null );
	}

	public WS_Task(
		String id,
		String title,
		String description,
		Date lastModificationDate,
		int status,
		String parentId,
		String nextId )
	{
		this.id = id;
		this.title = title;
		this.description = description;
		this.lastModificationDate = lastModificationDate;
		this.status = status;
		this.parentId = parentId;
		previousId = nextId;
		init();
	}

	public WS_Task(
		TT_Task task )
	{
		this( task.getID(), task.getTitle(), task.getDescription(), task.getLastModificationDate(), task.getStatus(),
			task.getParent() != null ? task.getParent().getID() : null, task.getPreviousTask() != null ? task
				.getPreviousTask().getID() : null );
	}

	@Override
	public boolean equals(
		Object obj ) {
		if ( this == obj )
		{
			return true;
		}
		if ( obj == null )
		{
			return false;
		}
		if ( getClass() != obj.getClass() )
		{
			return false;
		}
		WS_Task other = (WS_Task) obj;
		if ( id == null )
		{
			if ( other.id != null )
			{
				return false;
			}
		}
		else if ( !id.equals( other.id ) )
		{
			return false;
		}
		return true;
	}

	public String getDescription() {
		return description;
	}

	public String getId() {
		return id;
	}

	@XmlJavaTypeAdapter( JerseyDateAdapter.class )
	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	public String getParentId() {
		return parentId;
	}

	public String getPreviousId() {
		return previousId;
	}

	public int getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ( id == null ? 0 : id.hashCode() );
		return result;
	}

	public void setDescription(
		String description ) {
		this.description = description;
	}

	public void setId(
		String id ) {
		this.id = id;
	}

	public void setLastModificationDate(
		Date lastModificationDate ) {
		this.lastModificationDate = lastModificationDate;
	}

	public void setParentId(
		String parentId ) {
		this.parentId = parentId;
	}

	public void setPreviousId(
		String previousId ) {
		this.previousId = previousId;
	}

	public void setStatus(
		int status ) {
		this.status = status;
	}

	public void setTitle(
		String title ) {
		this.title = title;
	}

	public TT_Task update(
		TT_Task task ) {
		task.setID( id );
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( lastModificationDate );
		task.setStatus( status );

		return task;
	}

	public TT_Task update(
		TT_Task task,
		HashMap<String, TT_Task> tasksMap ) {
		task.setID( id );
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( lastModificationDate );
		task.setStatus( status );
		task.setParent( tasksMap.get( parentId ) );
		task.setPreviousTask( tasksMap.get( previousId ) );

		return task;
	}

	public WS_Task update(
		WS_Task task ) {
		task.setId( id );
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( lastModificationDate );
		task.setStatus( status );
		task.setParentId( parentId );
		task.setPreviousId( previousId );

		return task;
	}

	private void init() {
	}

	// PROTECTED
	// PRIVATE
	private String	id;
	private String	title;
	private String	description;
	private Date	lastModificationDate;
	private int		status;
	private String	parentId;
	private String	previousId;
}
