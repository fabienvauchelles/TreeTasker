package com.vaushell.treetasker.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TT_Task
{
	public static final int TODO = 0;
	public static final int DONE = 1;

	public TT_Task(
		String ID,
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

	public void setID(
		String iD )
	{
		ID = iD;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(
		String title )
	{
		this.title = title;
	}

	public Date getLastModificationDate()
	{
		return lastModificationDate;
	}

	public void setLastModificationDate(
		Date lastModificationDate )
	{
		this.lastModificationDate = lastModificationDate;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(
		int status )
	{
		this.status = status;
	}

	public List<TT_Task> getChildrenTask()
	{
		return childrenTask;
	}

	public void addChildTask(
		TT_Task childTask )
	{
		if ( childTask == null )
			return;
		if ( childTask.getParent() != null )
		{
			childTask.getParent().removeChildTask( childTask );
		}
		this.childrenTask.add( childTask );
	}

	public void removeChildTask(
		TT_Task childTask )
	{
		this.childrenTask.remove( childTask );
	}

	public TT_Task getParent()
	{
		return parentTask;
	}

	public void setParent(
		TT_Task parent )
	{
		this.parentTask = parent;
	}

	private void init()
	{
		this.parentTask = null;
		this.childrenTask = new ArrayList<TT_Task>();
	}

	private String ID;
	private String title;
	private Date lastModificationDate;
	private int status;
	private List<TT_Task> childrenTask;
	private TT_Task parentTask;
}
