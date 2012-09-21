package com.vaushell.treetasker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
		init();
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

		if ( !getChildrenTask().contains( childTask ) )
		{
			if ( childTask != this /*
									 * && !getAllAncestors().contains( childTask
									 * )
									 */)
			{
				childrenTask.add( childTask );
				childTask.setParent( this );
			}
		}
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
		return childrenTask;
	}

	public TT_Task getCopy() {
		TT_Task copy = new TT_Task( UUID.randomUUID().toString(), title, description, new Date(), status );
		for ( TT_Task childTask : childrenTask )
		{
			childTask.getCopy().setParent( copy );
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

	public int getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	public boolean removeChildTask(
		TT_Task childTask ) {
		boolean res = false;
		if ( childrenTask.contains( childTask ) )
		{
			res = childrenTask.remove( childTask );
			childTask.setParent( null );
		}
		return res;
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
		for ( TT_Task childTask : childrenTask )
		{
			childTask.setLastModificationDateRecursively( lastModificationDate );
		}
	}

	public void setParent(
		TT_Task parent ) {
		if ( parent == getParent() )
		{
			return;
		}

		if ( parent == null )
		{
			parentTask.removeChildTask( this );
			parentTask = parent;
		}
		else if ( parent != this && !parent.getAllAncestors().contains( this ) )
		{
			if ( getParent() != null )
			{
				getParent().removeChildTask( this );
			}
			parentTask = parent;
			parent.addChildTask( this );
		}
	}

	public void setStatus(
		int status ) {
		this.status = status;
	}

	public void setStatusRecursively(
		int status ) {
		this.status = status;
		for ( TT_Task childTask : childrenTask )
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
		childrenTask = new ArrayList<TT_Task>();
	}

	private String			ID;
	private String			title;
	private String			description;
	private Date			lastModificationDate;
	private int				status;
	private List<TT_Task>	childrenTask;
	private TT_Task			parentTask;
}
