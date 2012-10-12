/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.vaushell.treetasker.TreeTaskerWebApplication;
import com.vaushell.treetasker.application.actionbar.TTWActionBar;
import com.vaushell.treetasker.application.content.TTWcontent;
import com.vaushell.treetasker.application.content.layout.LoginLayout;
import com.vaushell.treetasker.application.header.TTWHeader;
import com.vaushell.treetasker.application.tree.A_NavigationNode;
import com.vaushell.treetasker.application.tree.TTWtree;
import com.vaushell.treetasker.application.tree.TaskNode;
import com.vaushell.treetasker.application.window.RegistrationWindow;
import com.vaushell.treetasker.application.window.UserWindow;
import com.vaushell.treetasker.dao.EH_TT_UserTaskContainer;
import com.vaushell.treetasker.dao.EH_WS_Task;
import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.model.TT_UserTaskContainer;
import com.vaushell.treetasker.net.UserSession;
import com.vaushell.treetasker.net.WS_Task;
import com.vaushell.treetasker.tools.TT_Tools;

/**
 * The web application controller.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TreeTaskerWebApplicationController
	implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public TreeTaskerWebApplicationController(
		TreeTaskerWebApplication application )
	{
		this.application = application;
		init();
	}

	/**
	 * Adds a subtask to the currentNode.
	 */
	public void addNewSubTask() {
		TaskNode parentNode = (TaskNode) getTree().getCurrentNode();

		if ( parentNode != null )
		{
			TT_Task newTask = new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null, new Date(),
				TT_Task.TODO );
			TaskNode newNode = new TaskNode( newTask, this );

			List<TT_Task> childrenList = parentNode.getTask().getChildrenTask();
			if ( !childrenList.isEmpty() )
			{
				newTask.setPreviousTask( childrenList.get( childrenList.size() - 1 ) );
			}

			getTree().addNode( newNode, parentNode );
			setTaskParent( newNode.getTask(), parentNode.getTask() );
			getTree().expandNode( parentNode );
		}
		else
		{
			TT_Task newTask = new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null, new Date(),
				TT_Task.TODO );

			TT_UserTaskContainer container = getUserContainer().getContainer();
			if ( !container.getRootTasks().isEmpty() )
			{
				newTask.setPreviousTask( container.getRootTasks().get( container.getRootTasks().size() - 1 ) );
			}
			container.addRootTask( newTask );

			getTree().addNode( new TaskNode( newTask, this ) );
		}
	}

	/**
	 * Adds a new task after the current node. Adds a new task if there is no
	 * selection.
	 */
	public void addNewTask() {
		ArrayList<EH_WS_Task> tasksToUpdate = new ArrayList<EH_WS_Task>();

		TaskNode siblingNode = (TaskNode) getTree().getCurrentNode();
		TaskNode parentNode = null;

		if ( siblingNode != null )
		{
			parentNode = (TaskNode) getTree().getParent( siblingNode );
		}

		if ( parentNode != null )
		{
			TaskNode newNode = new TaskNode( new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null,
				new Date(), TT_Task.TODO ), this );

			int siblingIndex = parentNode.getTask().getChildrenTask().indexOf( siblingNode.getTask() );
			if ( siblingIndex != parentNode.getTask().getChildrenTask().size() - 1 )
			{
				parentNode.getTask().getChildrenTask().get( siblingIndex + 1 ).setPreviousTask( newNode.getTask() );
				tasksToUpdate.add( new EH_WS_Task( new WS_Task( parentNode.getTask().getChildrenTask()
					.get( siblingIndex + 1 ) ), getUserContainer() ) );
			}
			newNode.getTask().setPreviousTask( siblingNode.getTask() );

			getTree().addNode( newNode, parentNode );
			getTree().moveAfterSiblingNode( newNode, siblingNode );
			newNode.getTask().setParent( parentNode.getTask() );
		}
		else
		{
			TaskNode newNode = null;

			if ( siblingNode != null )
			{
				newNode = new TaskNode( new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null, new Date(),
					TT_Task.TODO ), this );

				int siblingIndex = getUserContainer().getContainer().getRootTasks().indexOf( siblingNode.getTask() );
				if ( siblingIndex != getUserContainer().getContainer().getRootTasks().size() - 1 )
				{
					getUserContainer().getContainer().getRootTasks().get( siblingIndex + 1 )
						.setPreviousTask( newNode.getTask() );
					tasksToUpdate.add( new EH_WS_Task( new WS_Task( getUserContainer().getContainer().getRootTasks()
						.get( siblingIndex + 1 ) ), getUserContainer() ) );
				}
				newNode.getTask().setPreviousTask( siblingNode.getTask() );

				getTree().addNode( newNode );
				getTree().moveAfterSiblingNode( newNode, siblingNode );
			}
			else
			{
				TT_Task newTask = new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null, new Date(),
					TT_Task.TODO );
				newNode = new TaskNode( newTask, this );

				TT_UserTaskContainer container = getUserContainer().getContainer();
				if ( !container.getRootTasks().isEmpty() )
				{
					newTask.setPreviousTask( container.getRootTasks().get( container.getRootTasks().size() - 1 ) );
				}
				container.addRootTask( newTask );

				getTree().addNode( newNode );
			}

			tasksToUpdate.add( new EH_WS_Task( new WS_Task( newNode.getTask() ), getUserContainer() ) );
		}
		TT_ServerControllerDAO.getInstance().createOrUpdateTasks( tasksToUpdate );
	}

	@SuppressWarnings( "unchecked" )
	/**
	 * Copies all the selected tasks to the clipboard.
	 */
	public void copyTask() {
		copiedTasks.clear();
		for ( TaskNode taskNode : (Set<TaskNode>) getTree().getValue() )
		{
			copiedTasks.add( taskNode.getTask().getCopy() );
		}
	}

	@SuppressWarnings( "unchecked" )
	/**
	 * Deletes all the selected tasks.
	 */
	public void deleteTasks() {
		Set<TT_Task> tasksToUpdate = new HashSet<TT_Task>();
		Set<EH_WS_Task> tasksToDelete = new HashSet<EH_WS_Task>();
		EH_TT_UserTaskContainer userContainer = getUserContainer();

		for ( TaskNode taskNode : (Set<TaskNode>) getTree().getValue() )
		{
			tasksToUpdate.remove( taskNode.getTask() ); // Si le noeud doit être
														// supprimé, il n'y a
														// pas de mise à jour à
														// faire

			// Mise à jour de la position du noeud suivant
			if ( taskNode.getTask().getParent() != null )
			{
				int taskIndex = taskNode.getTask().getParent().getChildrenTask().indexOf( taskNode.getTask() );
				if ( taskIndex != taskNode.getTask().getParent().getChildrenTask().size() - 1 )
				{
					taskNode.getTask().getParent().getChildrenTask().get( taskIndex + 1 )
						.setPreviousTask( taskNode.getTask().getPreviousTask() );
					tasksToUpdate.add( taskNode.getTask().getParent().getChildrenTask().get( taskIndex + 1 ) );
				}
			}
			else
			{
				int taskIndex = getUserContainer().getContainer().getRootTasks().indexOf( taskNode.getTask() );
				if ( taskIndex != getUserContainer().getContainer().getRootTasks().size() - 1 )
				{
					getUserContainer().getContainer().getRootTasks().get( taskIndex + 1 )
						.setPreviousTask( taskNode.getTask().getPreviousTask() );
					tasksToUpdate.add( getUserContainer().getContainer().getRootTasks().get( taskIndex + 1 ) );
				}
			}

			addTasksToDeleteRecursively( taskNode.getTask(), tasksToDelete, userContainer );
		}

		TT_ServerControllerDAO.getInstance().deleteTasks( tasksToDelete );
		getContent().setView( null );
		for ( TT_Task task : tasksToUpdate )
		{
			TT_ServerControllerDAO.getInstance().createOrUpdateTask(
				new EH_WS_Task( new WS_Task( task ), getUserContainer() ) );
		}
	}

	/**
	 * 
	 * @return the application action bar.
	 */
	public TTWActionBar getActionBar() {
		if ( actionBar == null )
		{
			actionBar = new TTWActionBar( this );
		}
		return actionBar;
	}

	/**
	 * 
	 * @return the application instance.
	 */
	public TreeTaskerWebApplication getApplication() {
		return application;
	}

	/**
	 * 
	 * @return the application content view.
	 */
	public TTWcontent getContent() {
		if ( content == null )
		{
			content = new TTWcontent();
		}
		return content;
	}

	/**
	 * 
	 * @return the application header.
	 */
	public TTWHeader getHeader() {
		if ( header == null )
		{
			header = new TTWHeader();
		}
		return header;
	}

	/**
	 * 
	 * @return the application login layout.
	 */
	public LoginLayout getLoginLayout() {
		if ( loginLayout == null )
		{
			loginLayout = new LoginLayout( this );
		}
		return loginLayout;
	}

	/**
	 * 
	 * @return the application tree, which is the tasks list.
	 */
	public TTWtree getTree() {
		if ( tree == null )
		{
			tree = new TTWtree( this );
		}
		return tree;
	}

	/**
	 * 
	 * @return the user's container entity handler. Used with GAE.
	 */
	public EH_TT_UserTaskContainer getUserContainer() {
		if ( userContainer == null )
		{
			if ( userSession != null )
			{
				userContainer = TT_ServerControllerDAO.getInstance().getUserContainer( userSession.getUserName() );
			}
		}
		return userContainer;
	}

	/**
	 * 
	 * @return the application user window.
	 */
	public UserWindow getUserWindow() {
		if ( userWindow == null )
		{
			userWindow = new UserWindow( this );
		}
		return userWindow;
	}

	/**
	 * Try to log an user.
	 * 
	 * @param userName
	 * @param password
	 * @return if the login suceed or not.
	 */
	public boolean login(
		String userName,
		String password ) {
		if ( !"".equals( userName ) && password != null )
		{
			userSession = TT_ServerControllerDAO.getInstance().authenticateUser( userName,
				TT_Tools.encryptPassword( userName, password ) );
			return userSession.isValid();
		}
		return false;
	}

	/**
	 * Paste the tasks copied in the clipboard as children of the current task,
	 * or as roots if there is no task selected.
	 */
	public void pasteTask() {
		TaskNode parentNode = (TaskNode) getTree().getCurrentNode();
		if ( parentNode != null )
		{
			for ( TT_Task copiedTask : copiedTasks )
			{
				saveTasksRecursively( copiedTask.getCopy(), parentNode.getTask() );
			}
		}
		else
		{
			for ( TT_Task copiedTask : copiedTasks )
			{
				saveTasksRecursively( copiedTask.getCopy(), null );
			}
		}
		refresh();
	}

	/**
	 * Opens the registration window.
	 */
	public void queryRegistration() {
		RegistrationWindow subWindow = new RegistrationWindow();
		application.getMainWindow().addWindow( subWindow );
		subWindow.center();
	}

	/**
	 * Reloads the tasks list from the server.
	 */
	public void refresh() {
		ArrayList<TT_Task> rootTasksList = new ArrayList<TT_Task>();
		HashMap<String, TT_Task> idToTaskMap = new HashMap<String, TT_Task>();

		HashMap<String, String> orderMap = new HashMap<String, String>();
		HashMap<String, String> firstChildrenMap = new HashMap<String, String>();

		for ( EH_WS_Task ehTask : TT_ServerControllerDAO.getInstance().getAllTasks( getUserContainer() ) )
		{
			WS_Task wsTask = ehTask.getTask();

			if ( wsTask.getPreviousId() == null )
			{
				firstChildrenMap.put( wsTask.getParentId(), wsTask.getId() );
			}
			else
			{
				orderMap.put( wsTask.getPreviousId(), wsTask.getId() );
			}

			idToTaskMap.put( wsTask.getId(), wsTask.update( new TT_Task() ) );
		}

		for ( String parentId : firstChildrenMap.keySet() )
		{
			if ( parentId == null )
			{
				TT_Task currentTask = idToTaskMap.get( firstChildrenMap.get( null ) );

				while ( currentTask != null )
				{
					TT_Task nextTask = idToTaskMap.get( orderMap.get( currentTask.getID() ) );
					currentTask.setPreviousTask( nextTask );
					rootTasksList.add( currentTask );
					currentTask = nextTask;
				}
			}
			else
			{
				TT_Task parentTask = idToTaskMap.get( parentId );
				TT_Task currentTask = idToTaskMap.get( firstChildrenMap.get( parentId ) );

				while ( currentTask != null )
				{
					TT_Task nextTask = idToTaskMap.get( orderMap.get( currentTask.getID() ) );
					currentTask.setPreviousTask( nextTask );
					currentTask.setParent( parentTask );
					currentTask = nextTask;
				}
			}
		}

		refreshTreeTasks( rootTasksList );
	}

	/**
	 * Set <code>parent</code> as <code>child</code>'s parent.
	 * 
	 * @param child
	 * @param parent
	 */
	public void setTaskParent(
		TT_Task child,
		TT_Task parent ) {
		child.setParent( parent );
		child.setLastModificationDate( new Date() );
		WS_Task wsTask = new WS_Task( child );
		TT_ServerControllerDAO.getInstance().createOrUpdateTask( new EH_WS_Task( wsTask, getUserContainer() ) );
	}

	/**
	 * Displays the login layout.
	 */
	public void showLoginWindow() {
		UserWindow userWindow = getUserWindow();
		application.setMainWindow( userWindow );
	}

	/**
	 * Displays the user's view.
	 */
	public void showUserWindow() {
		UserWindow userWindow = getUserWindow();
		userWindow.setUserView();
	}

	/**
	 * Updates the task content.
	 * 
	 * @param task
	 * @param title
	 *            new title
	 * @param description
	 *            new description
	 */
	public void updateTaskContent(
		TT_Task task,
		String title,
		String description ) {

		boolean isModified = false;
		String oldTitle = task.getTitle();
		String oldDescription = task.getDescription();
		// Détection de modification (amélioration possible : comparer l'objet
		// sérialiser)

		if ( oldTitle != null )
		{
			if ( !oldTitle.equals( title ) )
			{
				isModified = true;
			}
		}
		else if ( title != null )
		{
			isModified = true;
		}
		if ( oldDescription != null )
		{
			if ( !oldDescription.equals( description ) )
			{
				isModified = true;
			}
		}
		else if ( description != null )
		{
			isModified = true;
		}

		task.setTitle( title );
		task.setDescription( description );
		if ( isModified )
		{
			task.setLastModificationDate( new Date() );
		}
		WS_Task wsTask = new WS_Task( task );
		TT_ServerControllerDAO.getInstance().createOrUpdateTask( new EH_WS_Task( wsTask, getUserContainer() ) );
	}

	@SuppressWarnings( "unchecked" )
	/**
	 * Valid all the selected tasks if at most one is not validated. Unvalid all the selected tasks otherwise.
	 * @param selectedNode the last node selected.
	 */
	public void validTask(
		A_NavigationNode selectedNode ) {
		if ( !getTree().getValue().contains( selectedNode ) )
		{
			getTree().unselectAll();
			getTree().select( selectedNode );
		}
		int newStatus = TT_Task.TODO;
		List<EH_WS_Task> tasksToUpdate = new ArrayList<EH_WS_Task>();

		for ( TaskNode node : (Set<TaskNode>) getTree().getValue() )
		{
			TT_Task selectedTask = node.getTask();
			if ( selectedTask.getStatus() == TT_Task.TODO )
			{
				newStatus = TT_Task.DONE;
			}
		}
		for ( TaskNode node : (Set<TaskNode>) getTree().getValue() )
		{
			TT_Task selectedTask = node.getTask();
			selectedTask.setLastModificationDate( new Date() );
			selectedTask.setStatus( newStatus );
			tasksToUpdate.add( new EH_WS_Task( new WS_Task( selectedTask ), getUserContainer() ) );
			getTree().refreshNodeIcon( node );
		}
		if ( getContent().getView() != null )
		{
			getContent().getView().refreshStyle();
		}
		TT_ServerControllerDAO.getInstance().createOrUpdateTasks( tasksToUpdate );
	}

	private void addChildrenTaskNodesRecursively(
		TaskNode rootNode ) {
		for ( TT_Task childTask : rootNode.getTask().getChildrenTask() )
		{
			TaskNode childNode = new TaskNode( childTask, this );
			getTree().addNode( childNode, rootNode );
			getTree().expandNode( childNode );
			addChildrenTaskNodesRecursively( childNode );
		}
	}

	private void addTasksToDeleteRecursively(
		TT_Task task,
		Set<EH_WS_Task> tasksToDelete,
		EH_TT_UserTaskContainer userContainer ) {
		tasksToDelete.add( new EH_WS_Task( new WS_Task( task ), userContainer ) );
		for ( TT_Task childTask : task.getChildrenTask() )
		{
			addTasksToDeleteRecursively( childTask, tasksToDelete, userContainer );
		}
	}

	private void init() {
		copiedTasks = new ArrayList<TT_Task>();
	}

	private void refreshTreeTasks(
		Collection<TT_Task> rootTasks ) {
		getTree().removeAllNodes();
		for ( TT_Task rootTask : rootTasks )
		{
			TaskNode rootNode = new TaskNode( rootTask, this );
			getTree().addNode( rootNode );
			getTree().expandNode( rootNode );
			addChildrenTaskNodesRecursively( rootNode );
		}
	}

	private void saveTasksRecursively(
		TT_Task task,
		TT_Task parentTask ) {
		setTaskParent( task, parentTask );
		for ( TT_Task childTask : task.getChildrenTask() )
		{
			saveTasksRecursively( childTask, task );
		}
	}

	private List<TT_Task>						copiedTasks;

	// PROTECTED
	// PRIVATE
	private final TreeTaskerWebApplication		application;
	private UserWindow							userWindow;
	private TTWActionBar						actionBar;
	private TTWHeader							header;
	private LoginLayout							loginLayout;
	private TTWtree								tree;
	private TTWcontent							content;
	private UserSession							userSession;
	private transient EH_TT_UserTaskContainer	userContainer;
}
