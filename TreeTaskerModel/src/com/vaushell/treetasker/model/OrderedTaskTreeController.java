package com.vaushell.treetasker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.vaushell.treetasker.net.WS_Task;

public class OrderedTaskTreeController
	implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public OrderedTaskTreeController()
	{
		this( (TT_UserTaskContainer) null );
	}

	public OrderedTaskTreeController(
		String ownerId )
	{
		this( new TT_UserTaskContainer( UUID.randomUUID().toString(), TT_UserTaskContainer.DEFAULT_NAME, ownerId ) );
	}

	public OrderedTaskTreeController(
		TT_UserTaskContainer rootContainer )
	{
		this.rootContainer = rootContainer;
		init();
	}

	public void addRootTask(
		TT_Task task ) {
		addRootTask( task, getSafeTaskId( getLastRootTask() ) );
	}

	public void addRootTask(
		TT_Task task,
		String previousId ) {
		addTask( task, null, previousId );
	}

	public void addTask(
		TT_Task task,
		String parentId ) {
		addTask( task, parentId, getSafeTaskId( getLastChildTask( parentId ) ) );
	}

	public void addTask(
		TT_Task task,
		String parentId,
		String previousId ) {
		// If task already exists in the tree, calling move
		if ( taskExistsWarn( task.getID() ) )
		{
			moveTask( task.getID(), parentId, previousId );
			return;
		}

		// Checking whether parent task or previous task exist in tree.
		checkTaskExistence( parentId );
		checkTaskExistence( previousId );

		TT_Task parentTask = tasksMap.get( parentId );
		TT_Task previousTask = tasksMap.get( previousId );
		Date modificationDate = new Date();

		// Fetching the list where to include the task in
		List<TT_Task> parentList = null;

		if ( parentTask == null ) // Roots list...
		{
			parentList = rootContainer.getRootTasks();
		}
		else
		// ... or children list
		{
			parentList = parentTask.getChildrenTask();
		}

		if ( previousTask == null ) // Adding task in first position
		{
			if ( !parentList.isEmpty() ) // Modifying current first task
											// precedence (+ date)
			{
				parentList.get( 0 ).setPreviousTask( task );
				parentList.get( 0 ).setLastModificationDate( new Date( modificationDate.getTime() ) );
			}

			parentList.add( 0, task );
		}
		else
		// Inserting task
		{
			int previousTaskIndex = parentList.indexOf( previousTask );

			if ( previousTaskIndex != parentList.size() - 1 ) // If there's a
																// task with the
																// same
																// precedence,
																// updating it
			{
				TT_Task nextTask = parentList.get( previousTaskIndex + 1 );
				nextTask.setPreviousTask( task );
				nextTask.setLastModificationDate( new Date( modificationDate.getTime() ) );
			}

			parentList.add( previousTaskIndex + 1, task );
		}

		// Updating the new task data
		task.setParent( parentTask );
		task.setPreviousTask( previousTask );
		task.setLastModificationDate( new Date( modificationDate.getTime() ) );

		registerSubTree( task );
	}

	public TT_Task getNextTask(
		String taskId ) {
		checkTaskExistence( taskId );

		TT_Task task = tasksMap.get( taskId );

		List<TT_Task> parentList = null;
		if ( task.getParent() == null ) // Roots list...
		{
			parentList = rootContainer.getRootTasks();
		}
		else
		// ... or children list
		{
			parentList = task.getParent().getChildrenTask();
		}

		int taskIndex = parentList.indexOf( task );
		if ( taskIndex == parentList.size() - 1 )
		{
			return null;
		}
		else
		{
			return parentList.get( taskIndex + 1 );
		}
	}

	public TT_UserTaskContainer getRootContainer() {
		return rootContainer;
	}

	public List<TT_Task> getRootTasksList() {
		return Collections.unmodifiableList( rootContainer.getRootTasks() );
	}

	public HashMap<String, TT_Task> getTaskMap() {
		return tasksMap;
	}

	public void moveTask(
		String taskId,
		String parentId,
		String previousId ) {
		checkTaskExistence( taskId );
		checkTaskExistence( parentId );
		checkTaskExistence( previousId );
		TT_Task task = tasksMap.get( taskId );
		TT_Task oldParentTask = task.getParent();
		TT_Task oldPreviousTask = task.getPreviousTask();
		Date modificationDate = new Date();

		// REMOVES
		List<TT_Task> oldParentList = null;

		if ( oldParentTask == null )
		{
			oldParentList = rootContainer.getRootTasks();
		}
		else
		{
			oldParentList = oldParentTask.getChildrenTask();
		}

		int taskIndex = oldParentList.indexOf( task );

		if ( taskIndex != oldParentList.size() - 1 )
		{
			TT_Task nextTask = oldParentList.get( taskIndex + 1 );
			nextTask.setPreviousTask( oldPreviousTask );
			nextTask.setLastModificationDate( new Date( modificationDate.getTime() ) );
		}

		oldParentList.remove( taskIndex );

		// ADDS
		TT_Task newParentTask = tasksMap.get( parentId );
		TT_Task newPreviousTask = tasksMap.get( previousId );

		List<TT_Task> newParentList = null;

		if ( newParentTask == null )
		{
			newParentList = rootContainer.getRootTasks();
		}
		else
		{
			newParentList = newParentTask.getChildrenTask();
		}

		if ( newPreviousTask == null )
		{

			if ( !newParentList.isEmpty() )
			{
				newParentList.get( 0 ).setPreviousTask( task );
				newParentList.get( 0 ).setLastModificationDate( new Date( modificationDate.getTime() ) );
			}

			newParentList.add( 0, task );
		}
		else
		{
			int previousTaskIndex = newParentList.indexOf( newPreviousTask );

			if ( previousTaskIndex != newParentList.size() - 1 )
			{
				TT_Task nextTask = newParentList.get( previousTaskIndex + 1 );
				nextTask.setPreviousTask( task );
				nextTask.setLastModificationDate( new Date( modificationDate.getTime() ) );
			}

			newParentList.add( previousTaskIndex + 1, task );
		}

		task.setParent( newParentTask );
		task.setPreviousTask( newPreviousTask );
		task.setLastModificationDate( new Date( modificationDate.getTime() ) );
	}

	public void reinit(
		Collection<WS_Task> data ) {
		rootContainer.getRootTasks().clear();
		tasksMap.clear();

		// Rebuilding tree
		HashMap<String, String> orderMap = new HashMap<String, String>();
		HashMap<String, String> firstChildrenMap = new HashMap<String, String>();

		for ( WS_Task wsTask : data )
		{
			if ( wsTask.getPreviousId() == null )
			{
				firstChildrenMap.put( wsTask.getParentId(), wsTask.getId() );
			}
			else
			{
				orderMap.put( wsTask.getPreviousId(), wsTask.getId() );
			}

			tasksMap.put( wsTask.getId(), wsTask.update( new TT_Task() ) );
		}

		List<TT_Task> orderedRootTasks = rootContainer.getRootTasks();

		for ( String parentId : firstChildrenMap.keySet() )
		{
			if ( parentId == null )
			{
				TT_Task currentTask = tasksMap.get( firstChildrenMap.get( null ) );
				while ( currentTask != null )
				{
					TT_Task nextTask = tasksMap.get( orderMap.get( currentTask.getID() ) );
					if ( nextTask != null )
					{
						nextTask.setPreviousTask( currentTask );
					}
					orderedRootTasks.add( currentTask );
					currentTask = nextTask;
				}
			}
			else
			{
				TT_Task parentTask = tasksMap.get( parentId );
				TT_Task currentTask = tasksMap.get( firstChildrenMap.get( parentId ) );

				while ( currentTask != null )
				{
					TT_Task nextTask = tasksMap.get( orderMap.get( currentTask.getID() ) );
					if ( nextTask != null )
					{
						nextTask.setPreviousTask( currentTask );
					}
					currentTask.setParent( parentTask );
					parentTask.addChildTask( currentTask );
					currentTask = nextTask;
				}
			}
		}
	}

	public List<TT_Task> removeTask(
		String taskId ) {
		checkTaskExistence( taskId );

		TT_Task taskToRemove = tasksMap.get( taskId );
		TT_Task parentTask = taskToRemove.getParent();
		TT_Task previousTask = taskToRemove.getPreviousTask();
		Date modificationDate = new Date();

		List<TT_Task> parentList = null;

		if ( parentTask == null )
		{
			parentList = rootContainer.getRootTasks();
		}
		else
		{
			parentList = parentTask.getChildrenTask();
		}

		int taskIndex = parentList.indexOf( taskToRemove );

		if ( taskIndex != parentList.size() - 1 )
		{
			TT_Task nextTask = parentList.get( taskIndex + 1 );
			nextTask.setPreviousTask( previousTask );
			nextTask.setLastModificationDate( new Date( modificationDate.getTime() ) );
		}

		parentList.remove( taskIndex );

		ArrayList<TT_Task> removedTasksList = new ArrayList<TT_Task>();
		removeRecursively( taskToRemove, removedTasksList );

		return removedTasksList;
	}

	public void setRootContainer(
		TT_UserTaskContainer rootContainer ) {
		this.rootContainer = rootContainer;
	}

	private void checkTaskExistence(
		String taskId ) {
		if ( taskId != null && !tasksMap.containsKey( taskId ) )
		{
			throw new IllegalStateException( String.format( "Task '%s' doesn't exist in controller.", taskId ) );
		}
	}

	private TT_Task getLastChildTask(
		String parentId ) {
		checkTaskExistence( parentId );

		if ( parentId == null )
		{
			if ( rootContainer.getRootTasks().isEmpty() )
			{
				return null;
			}
			return rootContainer.getRootTasks().get( rootContainer.getRootTasks().size() - 1 );
		}
		else
		{
			TT_Task parentTask = tasksMap.get( parentId );

			if ( parentTask.getChildrenTask().isEmpty() )
			{
				return null;
			}
			return parentTask.getChildrenTask().get( parentTask.getChildrenTask().size() - 1 );
		}
	}

	private TT_Task getLastRootTask() {
		return getLastChildTask( null );
	}

	private String getSafeTaskId(
		TT_Task task ) {
		return task != null ? task.getID() : null;
	}

	private void init() {
		tasksMap = new HashMap<String, TT_Task>();
	}

	private void registerSubTree(
		TT_Task task ) {
		tasksMap.put( task.getID(), task );

		for ( TT_Task childTask : task.getChildrenTask() )
		{
			registerSubTree( childTask );
		}
	}

	private void removeRecursively(
		TT_Task taskToRemove,
		List<TT_Task> removedTasksList ) {
		taskToRemove.setStatus( TT_Task.DELETED );
		taskToRemove.setParent( null );
		taskToRemove.setPreviousTask( null );
		taskToRemove.setLastModificationDate( new Date() );

		removedTasksList.add( taskToRemove );
		tasksMap.remove( taskToRemove.getID() );

		for ( TT_Task childTask : taskToRemove.getChildrenTask() )
		{
			removeRecursively( childTask, removedTasksList );
		}

		taskToRemove.getChildrenTask().clear();
	}

	private boolean taskExistsWarn(
		String taskId ) {
		if ( tasksMap.containsKey( taskId ) )
		{
			System.err.println( "[WARNING] You should use moveTask if a task already exists in controller." );
			return true;
		}
		return false;
	}

	private TT_UserTaskContainer		rootContainer;
	private HashMap<String, TT_Task>	tasksMap;
}
