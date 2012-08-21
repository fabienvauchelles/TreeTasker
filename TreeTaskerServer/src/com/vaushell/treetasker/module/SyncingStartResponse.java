package com.vaushell.treetasker.module;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SyncingStartResponse
{
	// PUBLIC
	public SyncingStartResponse()
	{
		init();
	}

	public Set<String> getDeletedIds()
	{
		return deletedIds;
	}

	public void setDeletedIds( Set<String> deletedIds )
	{
		this.deletedIds = deletedIds;
	}

	public void addDeletedId( String nodeId )
	{
		this.deletedIds.add( nodeId );
	}

	public Set<String> getNeedUpdateIds()
	{
		return needUpdateIds;
	}

	public void setNeedUpdateIds( Set<String> needUpdateIds )
	{
		this.needUpdateIds = needUpdateIds;
	}

	public void addNeedUpdateId( String nodeId )
	{
		this.needUpdateIds.add( nodeId );
	}

	public Set<WS_Task> getMoreRecentTasks()
	{
		return moreRecentTasks;
	}

	public void setMoreRecentTasks( Set<WS_Task> moreRecentTasks )
	{
		this.moreRecentTasks = moreRecentTasks;
	}

	public void addMoreRecentTask( WS_Task moreRecentTask )
	{
		this.moreRecentTasks.add( moreRecentTask );
	}

	public Set<WS_Task> getTasksToAdd()
	{
		return tasksToAdd;
	}

	public void setTasksToAdd( Set<WS_Task> tasksToAdd )
	{
		this.tasksToAdd = tasksToAdd;
	}

	public void addTaskToAdd( WS_Task taskToAdd )
	{
		this.tasksToAdd.add( taskToAdd );
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate( Date date )
	{
		this.date = date;
	}

	// PROTECTED
	// PRIVATE
	private Set<String>	 deletedIds;
	private Set<String>	 needUpdateIds;
	private Set<WS_Task>	moreRecentTasks;
	private Set<WS_Task>	tasksToAdd;
	private Date	     date;

	private void init()
	{
		this.deletedIds = new HashSet<String>();
		this.needUpdateIds = new HashSet<String>();
		this.moreRecentTasks = new HashSet<WS_Task>();
		this.tasksToAdd = new HashSet<WS_Task>();
	}
}
