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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.vaushell.treetasker.TreeTaskerWebApplication;
import com.vaushell.treetasker.application.actionbar.TTWActionBar;
import com.vaushell.treetasker.application.content.TTWcontent;
import com.vaushell.treetasker.application.header.TTWHeader;
import com.vaushell.treetasker.application.login.TTWLoginLayout;
import com.vaushell.treetasker.application.tree.TTWtree;
import com.vaushell.treetasker.application.tree.node.TaskNode;
import com.vaushell.treetasker.application.window.RegistrationWindow;
import com.vaushell.treetasker.application.window.UserWindow;
import com.vaushell.treetasker.dao.EH_TT_Task;
import com.vaushell.treetasker.dao.EH_TT_UserTaskContainer;
import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.module.UserSession;
import com.vaushell.treetasker.module.WS_Task;
import com.vaushell.treetasker.tools.TT_Tools;

/**
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

	public void addNewSubTask() {
		TaskNode newNode = new TaskNode( new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null, new Date(),
			TT_Task.TODO ), this );
		TaskNode parentNode = (TaskNode) getTree().getCurrentNode();

		if ( parentNode != null )
		{
			getTree().addNode( newNode, parentNode );
			setTaskParent( newNode.getTask(), parentNode.getTask() );
			getTree().expandNode( parentNode );
		}
		else
		{
			getTree().addNode( newNode );
		}
	}

	public void addNewTask() {
		TaskNode newNode = new TaskNode( new TT_Task( UUID.randomUUID().toString(), "Nouvelle tâche", null, new Date(),
			TT_Task.TODO ), this );
		TaskNode siblingNode = (TaskNode) getTree().getCurrentNode();
		TaskNode parentNode = null;
		if ( siblingNode != null )
		{
			parentNode = (TaskNode) getTree().getParent( siblingNode );
		}

		if ( parentNode != null )
		{
			getTree().addNode( newNode, parentNode );
			getTree().moveAfterSiblingNode( newNode, siblingNode );
			newNode.getTask().setParent( parentNode.getTask() );
		}
		else
		{
			getTree().addNode( newNode );
			if ( siblingNode != null )
			{
				getTree().moveAfterSiblingNode( newNode, siblingNode );
			}
			TT_ServerControllerDAO.getInstance().createOrUpdateTask(
				new EH_TT_Task( new WS_Task( newNode.getTask() ), getUserContainer() ) );
		}
	}

	@SuppressWarnings( "unchecked" )
	public void copyTask() {
		copiedTasks.clear();
		for ( TaskNode taskNode : (Set<TaskNode>) getTree().getValue() )
		{
			copiedTasks.add( taskNode.getTask().getCopy() );
		}
	}

	@SuppressWarnings( "unchecked" )
	public void deleteTasks() {
		Set<EH_TT_Task> tasksToDelete = new HashSet<EH_TT_Task>();
		EH_TT_UserTaskContainer userContainer = getUserContainer();
		for ( TaskNode taskNode : (Set<TaskNode>) getTree().getValue() )
		{
			getTree().removeNode( taskNode );
			addTasksToDeleteRecursively( taskNode.getTask(), tasksToDelete, userContainer );
		}
		TT_ServerControllerDAO.getInstance().deleteTasks( tasksToDelete );
	}

	public TTWActionBar getActionBar() {
		if ( actionBar == null )
		{
			actionBar = new TTWActionBar( this );
		}
		return actionBar;
	}

	public TreeTaskerWebApplication getApplication() {
		return application;
	}

	public TTWcontent getContent() {
		if ( content == null )
		{
			content = new TTWcontent();
		}
		return content;
	}

	public TTWHeader getHeader() {
		if ( header == null )
		{
			header = new TTWHeader();
		}
		return header;
	}

	public TTWLoginLayout getLoginLayout() {
		if ( loginLayout == null )
		{
			loginLayout = new TTWLoginLayout( this );
		}
		return loginLayout;
	}

	public TTWtree getTree() {
		if ( tree == null )
		{
			tree = new TTWtree( this );
		}
		return tree;
	}

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

	public UserWindow getUserWindow() {
		if ( userWindow == null )
		{
			userWindow = new UserWindow( this );
		}
		return userWindow;
	}

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

	public void pasteTask() {
		TaskNode parentNode = (TaskNode) getTree().getCurrentNode();
		for ( TT_Task copiedTask : copiedTasks )
		{
			saveTasksRecursively( copiedTask.getCopy(), parentNode.getTask() );
		}
		refresh();
	}

	public void queryRegistration() {
		RegistrationWindow subWindow = new RegistrationWindow();
		application.getMainWindow().addWindow( subWindow );
		subWindow.center();
	}

	public void refresh() {
		ArrayList<TT_Task> rootTasksList = new ArrayList<TT_Task>();
		HashMap<String, TT_Task> idToTaskMap = new HashMap<String, TT_Task>();
		LinkedHashMap<TT_Task, String> childrenToParentIdMap = new LinkedHashMap<TT_Task, String>();
		for ( EH_TT_Task ehTask : TT_ServerControllerDAO.getInstance().getAllTasks( getUserContainer() ) )
		{
			TT_Task taskToLoad = new TT_Task();
			ehTask.getTask().update( taskToLoad );

			idToTaskMap.put( taskToLoad.getID(), taskToLoad );

			String parentId = ehTask.getTask().getParentId();
			if ( parentId != null )
			{
				childrenToParentIdMap.put( taskToLoad, parentId );
			}
			else
			{
				rootTasksList.add( taskToLoad );
			}

		}
		for ( TT_Task childTask : childrenToParentIdMap.keySet() )
		{
			childTask.setParent( idToTaskMap.get( childrenToParentIdMap.get( childTask ) ) );
		}
		refreshTreeTasks( rootTasksList );
	}

	public void setTaskParent(
		TT_Task child,
		TT_Task parent ) {
		child.setParent( parent );
		child.setLastModificationDate( new Date() );
		WS_Task wsTask = new WS_Task( child );
		TT_ServerControllerDAO.getInstance().createOrUpdateTask( new EH_TT_Task( wsTask, getUserContainer() ) );
	}

	public void showLoginWindow() {
		UserWindow userWindow = getUserWindow();
		application.setMainWindow( userWindow );
	}

	public void showUserWindow() {
		UserWindow userWindow = getUserWindow();
		userWindow.setUserView( userSession.getUserName() );
	}

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
		TT_ServerControllerDAO.getInstance().createOrUpdateTask( new EH_TT_Task( wsTask, getUserContainer() ) );
	}

	@SuppressWarnings( "unchecked" )
	public void validTask(
		Object itemId ) {
		if ( !getTree().getValue().contains( itemId ) )
		{
			getTree().unselectAll();
			getTree().select( itemId );
		}
		int newStatus = TT_Task.TODO;
		List<EH_TT_Task> tasksToUpdate = new ArrayList<EH_TT_Task>();

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
			tasksToUpdate.add( new EH_TT_Task( new WS_Task( selectedTask ), getUserContainer() ) );
			getTree().refreshNodeIcon( node );
		}

		getContent().getView().refreshStyle();
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
		Set<EH_TT_Task> tasksToDelete,
		EH_TT_UserTaskContainer userContainer ) {
		tasksToDelete.add( new EH_TT_Task( new WS_Task( task ), userContainer ) );
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
	private TTWLoginLayout						loginLayout;
	private TTWtree								tree;
	private TTWcontent							content;
	private UserSession							userSession;
	private transient EH_TT_UserTaskContainer	userContainer;
}
