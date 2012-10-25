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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.vaadin.ui.Window.Notification;
import com.vaushell.tools.xmldirtyparser.XPathDirtyParserMultiple;
import com.vaushell.treetasker.TreeTaskerWebApplication;
import com.vaushell.treetasker.application.actionbar.TTWActionBar;
import com.vaushell.treetasker.application.actionbar.TaskerCoachParserAction;
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
import com.vaushell.treetasker.model.OrderedTaskTreeController;
import com.vaushell.treetasker.model.TT_Task;
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
		TT_Task newTask = new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null, new Date(), TT_Task.TODO );

		if ( parentNode != null )
		{
			treeController.addTask( newTask, parentNode.getTask().getID() );

			TaskNode newNode = new TaskNode( newTask, this );

			getTree().addNode( newNode, parentNode );
			getTree().expandNode( parentNode );
		}
		else
		{
			treeController.addRootTask( newTask );
			getTree().addNode( new TaskNode( newTask, this ) );
		}

		sendTask( newTask );
	}

	/**
	 * Adds a new task after the current node. Adds a new task if there is no
	 * selection.
	 */
	public void addNewTask() {
		TaskNode siblingNode = (TaskNode) getTree().getCurrentNode();
		TaskNode parentNode = null;

		if ( siblingNode != null )
		{
			parentNode = (TaskNode) getTree().getParent( siblingNode );
		}

		if ( parentNode != null ) // Sibling node is not a root
		{
			TaskNode newNode = new TaskNode( new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null,
				new Date(), TT_Task.TODO ), this );

			getTree().addNode( newNode, parentNode );
			getTree().moveAfterSiblingNode( newNode, parentNode, siblingNode );

			treeController.addTask( newNode.getTask(), parentNode.getTask().getID(), siblingNode.getTask().getID() );

			TT_Task nextTask = treeController.getNextTask( newNode.getTask().getID() );
			if ( nextTask != null )
			{
				sendTasks( newNode.getTask(), nextTask );
			}
			else
			{
				sendTask( newNode.getTask() );
			}
		}
		else
		{
			TaskNode newNode = null;

			if ( siblingNode != null )
			{
				newNode = new TaskNode( new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null, new Date(),
					TT_Task.TODO ), this );

				getTree().addNode( newNode );
				getTree().moveAfterSiblingNode( newNode, parentNode, siblingNode );

				treeController.addRootTask( newNode.getTask(), siblingNode.getTask().getID() );

				TT_Task nextTask = treeController.getNextTask( newNode.getTask().getID() );
				if ( nextTask != null )
				{
					sendTasks( newNode.getTask(), nextTask );
				}
				else
				{
					sendTask( newNode.getTask() );
				}
			}
			else
			{
				TT_Task newTask = new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null, new Date(),
					TT_Task.TODO );
				newNode = new TaskNode( newTask, this );

				getTree().addNode( newNode );

				treeController.addRootTask( newTask );

				sendTask( newNode.getTask() );
			}
		}
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

	public void deleteAllTasks() {
		getTree().removeAllNodes();
		sendDeleteTasks( treeController.getTaskMap().values() );
		treeController.reinit( Collections.<WS_Task> emptyList() );
	}

	@SuppressWarnings( "unchecked" )
	/**
	 * Deletes all the selected tasks.
	 */
	public void deleteTasks() {
		Set<TT_Task> tasksToUpdate = new HashSet<TT_Task>();
		Set<TT_Task> tasksToDelete = new HashSet<TT_Task>();

		HashSet<TT_Task> selectedTasks = new HashSet<TT_Task>();
		for ( TaskNode taskNode : (Set<TaskNode>) getTree().getValue() )
		{
			selectedTasks.add( taskNode.getTask() );
		}

		// Removing descendant node from selection
		// And removing nodes from visual tree
		for ( TaskNode taskNode : (Set<TaskNode>) getTree().getValue() )
		{
			selectedTasks.removeAll( taskNode.getTask().retrieveAllDescendants() );
			getTree().removeNodeRecursively( taskNode );
		}

		for ( TT_Task taskToDelete : selectedTasks )
		{
			tasksToUpdate.remove( taskToDelete );
			// Must update the next node for precedence modification
			tasksToUpdate.add( treeController.getNextTask( taskToDelete.getID() ) );

			// Removing all subtasks
			tasksToDelete.addAll( treeController.removeTask( taskToDelete.getID() ) );
		}

		for ( TT_Task deletedTask : tasksToDelete )
		{
			TT_ServerControllerDAO.getInstance().deleteTask(
				new EH_WS_Task( new WS_Task( deletedTask ), getUserContainer() ) );
		}

		sendTasks( tasksToUpdate );

		getContent().setView( null );
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

	public void importFromTaskCoach(
		InputStream is ) {
		deleteAllTasks();
		XPathDirtyParserMultiple parser = new XPathDirtyParserMultiple();
		TaskerCoachParserAction parseAction = new TaskerCoachParserAction( treeController );
		parser.addAction( parseAction );
		try
		{
			parser.parse( new InputSource( is ) );
		}
		catch ( FileNotFoundException e )
		{
			e.printStackTrace();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		catch ( SAXException e )
		{
			getUserWindow().showNotification( "Format inconnu", Notification.TYPE_ERROR_MESSAGE );
		}
		sendTasks( treeController.getTaskMap().values() );
		refresh();
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

			if ( userSession.isValid() )
			{
				treeController = new OrderedTaskTreeController( getUserContainer().getContainer() );
			}

			return userSession.isValid();
		}
		return false;
	}

	public void move(
		TaskNode nodeToMove,
		TaskNode parentNode,
		TaskNode previousNode ) {
		HashSet<TT_Task> tasksToUpdate = new HashSet<TT_Task>();

		TT_Task taskToMove = nodeToMove.getTask();
		String parentTaskId = null;
		if ( parentNode != null )
		{
			parentTaskId = parentNode.getTask().getID();
		}
		String previousTaskId = null;
		if ( previousNode != null )
		{
			previousTaskId = previousNode.getTask().getID();
		}

		tasksToUpdate.add( taskToMove );
		TT_Task currentNextTask = treeController.getNextTask( taskToMove.getID() );
		if ( currentNextTask != null )
		{
			tasksToUpdate.add( currentNextTask );
		}

		treeController.moveTask( taskToMove.getID(), parentTaskId, previousTaskId );

		currentNextTask = treeController.getNextTask( taskToMove.getID() );
		if ( currentNextTask != null )
		{
			tasksToUpdate.add( currentNextTask );
		}

		getTree().moveAfterSiblingNode( nodeToMove, parentNode, previousNode );

		sendTasks( tasksToUpdate );
	}

	/**
	 * Paste the tasks copied in the clipboard as children of the current task,
	 * or as roots if there is no task selected.
	 */
	public void pasteTask() {
		HashSet<TT_Task> tasksToSend = new HashSet<TT_Task>();
		TaskNode parentNode = (TaskNode) getTree().getCurrentNode();

		if ( parentNode != null )
		{
			for ( TT_Task copiedTask : copiedTasks )
			{
				treeController.addTask( copiedTask, parentNode.getTask().getID() );
				tasksToSend.add( copiedTask );
				tasksToSend.addAll( copiedTask.retrieveAllDescendants() );
			}
		}
		else
		{
			for ( TT_Task copiedTask : copiedTasks )
			{
				treeController.addRootTask( copiedTask );
				tasksToSend.add( copiedTask );
				tasksToSend.addAll( copiedTask.retrieveAllDescendants() );
			}
		}

		sendTasks( tasksToSend );

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
		ArrayList<WS_Task> tasksFromDatastore = new ArrayList<WS_Task>();
		for ( EH_WS_Task ehTask : TT_ServerControllerDAO.getInstance().getAllTasks( getUserContainer() ) )
		{
			tasksFromDatastore.add( ehTask.getTask() );
		}

		treeController.reinit( tasksFromDatastore );

		refreshTreeTasks( treeController.getRootTasksList() );
	}

	public void refreshTreeTasks(
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

		sendTask( task );
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

	//
	// private void addTasksToDeleteRecursively(
	// TT_Task task,
	// Set<EH_WS_Task> tasksToDelete,
	// EH_TT_UserTaskContainer userContainer ) {
	// tasksToDelete.add( new EH_WS_Task( new WS_Task( task ), userContainer )
	// );
	// for ( TT_Task childTask : task.getChildrenTask() )
	// {
	// addTasksToDeleteRecursively( childTask, tasksToDelete, userContainer );
	// }
	// }

	private void init() {
		copiedTasks = new ArrayList<TT_Task>();
	}

	//
	// private void saveTasksRecursively(
	// TT_Task task,
	// TT_Task parentTask ) {
	// setTaskParent( task, parentTask );
	// for ( TT_Task childTask : task.getChildrenTask() )
	// {
	// saveTasksRecursively( childTask, task );
	// }
	// }

	private void sendDeleteTasks(
		Collection<TT_Task> tasksToDelete ) {
		ArrayList<EH_WS_Task> ehTasksToDelete = new ArrayList<EH_WS_Task>();

		for ( TT_Task taskToDelete : tasksToDelete )
		{
			ehTasksToDelete.add( new EH_WS_Task( new WS_Task( taskToDelete ), getUserContainer() ) );
		}

		TT_ServerControllerDAO.getInstance().deleteTasks( ehTasksToDelete );
	}

	private void sendTask(
		TT_Task taskToSend ) {
		TT_ServerControllerDAO.getInstance().createOrUpdateTask(
			new EH_WS_Task( new WS_Task( taskToSend ), getUserContainer() ) );
	}

	private void sendTasks(
		Collection<TT_Task> tasksToSend ) {
		ArrayList<EH_WS_Task> ehTasksToSend = new ArrayList<EH_WS_Task>();

		for ( TT_Task taskToSend : tasksToSend )
		{
			ehTasksToSend.add( new EH_WS_Task( new WS_Task( taskToSend ), getUserContainer() ) );
		}

		TT_ServerControllerDAO.getInstance().createOrUpdateTasks( ehTasksToSend );
	}

	private void sendTasks(
		TT_Task... tasksToSend ) {
		ArrayList<EH_WS_Task> ehTasksToSend = new ArrayList<EH_WS_Task>();

		for ( TT_Task taskToSend : tasksToSend )
		{
			ehTasksToSend.add( new EH_WS_Task( new WS_Task( taskToSend ), getUserContainer() ) );
		}

		TT_ServerControllerDAO.getInstance().createOrUpdateTasks( ehTasksToSend );
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
	private OrderedTaskTreeController			treeController;
}
