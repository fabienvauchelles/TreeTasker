package com.vaushell.treetasker.net;

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

	public void addDeletedId(
		String nodeId ) {
		deletedIds.add( nodeId );
	}

	public void addMoreRecentTask(
		WS_Task moreRecentTask ) {
		moreRecentTasks.add( moreRecentTask );
	}

	public void addNeedUpdateId(
		String nodeId ) {
		needUpdateIds.add( nodeId );
	}

	public void addTaskToAdd(
		WS_Task taskToAdd ) {
		tasksToAdd.add( taskToAdd );
	}

	public Set<String> getDeletedIds() {
		return deletedIds;
	}

	public Set<WS_Task> getMoreRecentTasks() {
		return moreRecentTasks;
	}

	public Set<String> getNeedUpdateIds() {
		return needUpdateIds;
	}

	public Set<WS_Task> getTasksToAdd() {
		return tasksToAdd;
	}

	public void setDeletedIds(
		Set<String> deletedIds ) {
		this.deletedIds = deletedIds;
	}

	public void setMoreRecentTasks(
		Set<WS_Task> moreRecentTasks ) {
		this.moreRecentTasks = moreRecentTasks;
	}

	public void setNeedUpdateIds(
		Set<String> needUpdateIds ) {
		this.needUpdateIds = needUpdateIds;
	}

	public void setTasksToAdd(
		Set<WS_Task> tasksToAdd ) {
		this.tasksToAdd = tasksToAdd;
	}

	private void init() {
		deletedIds = new HashSet<String>();
		needUpdateIds = new HashSet<String>();
		moreRecentTasks = new HashSet<WS_Task>();
		tasksToAdd = new HashSet<WS_Task>();
	}

	// PROTECTED
	// PRIVATE
	private Set<String>		deletedIds;
	private Set<String>		needUpdateIds;
	private Set<WS_Task>	moreRecentTasks;

	private Set<WS_Task>	tasksToAdd;
}
