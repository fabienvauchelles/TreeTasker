package com.vaushell.treetasker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TT_Task
    implements Serializable
{
    private static final long serialVersionUID = 1L;
	public static final int	TODO	= 0;
	public static final int	DONE	= 1;

	public TT_Task( String ID,
	                String title,
	                Date lastModificationDate,
	                int status )
	{
		this.ID = ID;
		this.title = title;
		this.lastModificationDate = lastModificationDate;
		this.status = status;
		init();
	}

	public String getID()
	{
		return ID;
	}

	public void setID( String iD )
	{
		ID = iD;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle( String title )
	{
		this.title = title;
	}

	public Date getLastModificationDate()
	{
		return lastModificationDate;
	}

	public void setLastModificationDate( Date lastModificationDate )
	{
		this.lastModificationDate = lastModificationDate;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus( int status )
	{
		this.status = status;
	}

	public List<TT_Task> getChildrenTask()
	{
		return childrenTask;
	}

	public void addChildTask( TT_Task childTask )
	{
		if ( childTask == null )
			return;

		if ( !getChildrenTask().contains( childTask ) )
		{
			if ( childTask != this && !getAllAncestors().contains( childTask ) )
			{
				childrenTask.add( childTask );
				childTask.setParent( this );
			}
		}
	}

	public boolean removeChildTask( TT_Task childTask )
	{
		boolean res = false;
		if ( childrenTask.contains( childTask ) )
		{
			res = childrenTask.remove( childTask );
			childTask.setParent( null );
		}
		return res;
	}

	public TT_Task getParent()
	{
		return parentTask;
	}

	public void setParent( TT_Task parent )
	{
		if ( parent == getParent() )
			return;

		if ( parent == null )
		{
			this.parentTask.removeChildTask( this );
			this.parentTask = parent;
		}
		else if ( parent != this && !parent.getAllAncestors().contains( this ) )
		{
			if ( getParent() != null )
			{
				getParent().removeChildTask( this );
			}
			this.parentTask = parent;
			parent.addChildTask( this );
		}
	}

	public Set<TT_Task> getAllAncestors()
	{
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

	private void init()
	{
		this.parentTask = null;
		this.childrenTask = new ArrayList<TT_Task>();
	}

	private String	      ID;
	private String	      title;
	private Date	      lastModificationDate;
	private int	          status;
	private List<TT_Task>	childrenTask;
	private TT_Task	      parentTask;
}
