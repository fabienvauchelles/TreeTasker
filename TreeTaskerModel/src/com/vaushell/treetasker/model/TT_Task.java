/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TT_Task
	implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	public static final int		TODO				= 0;
	public static final int		DONE				= 1;
	public static final int		DELETED				= 2;

	public TT_Task()
	{
		this( null, null, "", null, TODO );
	}

	public TT_Task(
		String ID,
		String title,
		String description,
		Date lastModificationDate,
		int status )
	{
		this.ID = ID;
		this.title = title;
		this.description = description;
		this.lastModificationDate = lastModificationDate;
		this.status = status;
		init();
	}

	public void addChildTask(
		TT_Task childTask ) {
		if ( childTask == null )
		{
			return;
		}

		getChildrenTask().add( childTask );
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
		TT_Task other = (TT_Task) obj;
		if ( ID == null )
		{
			if ( other.ID != null )
			{
				return false;
			}
		}
		else if ( !ID.equals( other.ID ) )
		{
			return false;
		}
		return true;
	}

	public Set<TT_Task> getAllAncestors() {
		if ( getParent() == null )
		{
			return new HashSet<TT_Task>();
		}
		else
		{
			Set<TT_Task> ancestors = getParent().getAllAncestors();
			ancestors.add( getParent() );
			return ancestors;
		}
	}

	public List<TT_Task> getChildrenTask() {
		if ( childrenTask == null )
		{
			childrenTask = new LinkedList<TT_Task>();
		}
		return childrenTask;
	}

	public TT_Task getCopy() {
		TT_Task copy = new TT_Task( UUID.randomUUID().toString(), title, description, new Date(), status );

		TT_Task previousTask = null;
		for ( TT_Task childTask : getChildrenTask() )
		{
			TT_Task childTaskCopy = childTask.getCopy();
			childTaskCopy.setPreviousTask( previousTask );
			childTaskCopy.setParent( copy );
			copy.addChildTask( childTaskCopy );

			previousTask = childTaskCopy;
		}

		return copy;
	}

	public String getDescription() {
		return description;
	}

	public String getID() {
		return ID;
	}

	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	public TT_Task getParent() {
		return parentTask;
	}

	public TT_Task getPreviousTask() {
		return previousTask;
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
		result = prime * result + ( ID == null ? 0 : ID.hashCode() );
		return result;
	}

	public boolean isADescendantOf(
		TT_Task otherTask ) {
		return otherTask == null || otherTask.isAnAncestorOf( this );
	}

	public boolean isAnAncestorOf(
		TT_Task otherTask ) {
		return otherTask != null && ( this == otherTask.getParent() || isAnAncestorOf( otherTask.getParent() ) );
	}

	public boolean removeChildTask(
		TT_Task childTask ) {
		boolean res = false;
		if ( getChildrenTask().contains( childTask ) )
		{
			res = getChildrenTask().remove( childTask );
			childTask.setParent( null );
		}
		return res;
	}

	/**
	 * Heavy to use.
	 * 
	 * @return A list of all descendants
	 */
	public List<TT_Task> retrieveAllDescendants() {
		ArrayList<TT_Task> descendants = new ArrayList<TT_Task>();

		for ( TT_Task childTask : getChildrenTask() )
		{
			retrieveDescendantsRec( childTask, descendants );
		}

		return descendants;
	}

	public void setDescription(
		String description ) {
		this.description = description;
	}

	public void setID(
		String iD ) {
		ID = iD;
	}

	public void setLastModificationDate(
		Date lastModificationDate ) {
		this.lastModificationDate = lastModificationDate;
	}

	public void setLastModificationDateRecursively(
		Date lastModificationDate ) {
		this.lastModificationDate = lastModificationDate;
		for ( TT_Task childTask : getChildrenTask() )
		{
			childTask.setLastModificationDateRecursively( lastModificationDate );
		}
	}

	public void setParent(
		TT_Task parentTask ) {
		if ( parentTask == getParent() )
		{
			return;
		}

		this.parentTask = parentTask;
	}

	public void setPreviousTask(
		TT_Task previousTask ) {
		this.previousTask = previousTask;
	}

	public void setStatus(
		int status ) {
		this.status = status;
	}

	public void setStatusRecursively(
		int status ) {
		this.status = status;
		for ( TT_Task childTask : getChildrenTask() )
		{
			childTask.setStatusRecursively( status );
		}
	}

	public void setTitle(
		String title ) {
		this.title = title;
	}

	private void init() {
		parentTask = null;
		previousTask = null;
		childrenTask = null;
	}

	private void retrieveDescendantsRec(
		TT_Task task,
		List<TT_Task> descendants ) {
		descendants.add( task );

		for ( TT_Task childTask : task.getChildrenTask() )
		{
			retrieveDescendantsRec( childTask, descendants );
		}
	}

	private String			ID;
	private String			title;
	private String			description;
	private Date			lastModificationDate;
	private int				status;
	private List<TT_Task>	childrenTask;
	private TT_Task			parentTask;
	private TT_Task			previousTask;
}
