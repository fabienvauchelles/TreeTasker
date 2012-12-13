/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import android.content.Context;

import com.google.android.gcm.GCMRegistrar;
import com.google.gson.Gson;
import com.vaushell.treetasker.GCMIntentService;
import com.vaushell.treetasker.application.service.DataAccessService;
import com.vaushell.treetasker.application.service.DataAccessService.DataAccessServiceListener;
import com.vaushell.treetasker.client.E_BadResponseStatus;
import com.vaushell.treetasker.client.SimpleJsonClient;
import com.vaushell.treetasker.net.GcmRegistrationRequest;
import com.vaushell.treetasker.net.GcmRegistrationResponse;
import com.vaushell.treetasker.net.GcmUnregistrationRequest;
import com.vaushell.treetasker.net.GcmUnregistrationResponse;
import com.vaushell.treetasker.net.UserSession;
import com.vaushell.treetasker.net.WS_Task;

public class TreeTaskerControllerDAO
	implements DataAccessServiceListener
{
	private static class TreeTaskerControllerDAOHolder
	{
		private static final TreeTaskerControllerDAO	INSTANCE	= new TreeTaskerControllerDAO();
	}

	// PUBLIC
	public static final String	DEFAULT_WEB_RESOURCE	= "https://vsh2-test.appspot.com/";

	// PRIVATE
	private static final Gson	GSON_SERIALIZER			= new Gson();

	private static final String	CACHE_FILENAME			= "user_cache.json";

	public static TreeTaskerControllerDAO getInstance() {
		return TreeTaskerControllerDAOHolder.INSTANCE;
	}

	private TreeTaskerControllerDAO()
	{
		init();
	}

	public void addRootTask(
		TT_Task rootTask ) {
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		treeBuilder.sequentiallyAddNextNode( rootTask, 0 );
		treeController.addRootTask( rootTask );
		daoService.createOrUpdateTask( rootTask, isExpanded( rootTask ) );
	}

	public void addSubTask(
		TT_Task parentTask,
		TT_Task childTask ) {
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		treeBuilder.addRelation( parentTask, childTask );

		Set<TT_Task> modifiedTasks = treeController.addTask( childTask, parentTask.getID() );
		daoService.createOrUpdateTasks( modifiedTasks, retrieveExpandedSetFrom( modifiedTasks ) );
	}

	@Override
	public void allTasksRetrieved(
		Set<WS_Task> tasks,
		Set<String> expandedSet ) {
		// TODO Auto-generated method stub
		// Emptying view
		treeManager.clear();

		// Rebuilding the tree
		treeController.reinit( tasks );

		TreeBuilder<TT_Task> builder = new TreeBuilder<TT_Task>( getTreeManager() );
		for ( TT_Task task : treeController.getRootTasksList() )
		{
			builder.sequentiallyAddNextNode( task, 0 );
			buildTreeRecursively( task, builder );
		}

		// Collapsing tasks
		for ( TT_Task task : treeController.getTaskMap().values() )
		{
			if ( !expandedSet.contains( task.getID() )
				&& treeManager.isInTree( treeController.getTaskMap().get( task.getID() ) ) )
			{
				treeManager.collapseChildren( treeController.getTaskMap().get( task.getID() ) );
			}
		}
	}

	public boolean canPaste() {
		return copiedTask != null;
	}

	public void copyTask(
		TT_Task srcTask ) {
		copiedTask = srcTask.getCopy();
	}

	public void deleteTask(
		TT_Task task ) {
		treeManager.removeNodeRecursively( task );

		HashSet<TT_Task> modifiedTasks = new HashSet<TT_Task>();
		HashSet<String> deletedTasksIds = new HashSet<String>();
		for ( TT_Task deletedTask : treeController.removeTask( task.getID(), modifiedTasks ) )
		{
			deletedTasksIds.add( deletedTask.getID() );
		}
		daoService.deleteTasks( deletedTasksIds );
		daoService.createOrUpdateTasks( modifiedTasks, retrieveExpandedSetFrom( modifiedTasks ) );
	}

	public void edit(
		TT_Task task,
		String title,
		String description ) {
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( new Date() );

		daoService.createOrUpdateTask( task, isExpanded( task ) );
	}

	public SimpleJsonClient getCheckClient(
		String endpointValue ) {

		return new SimpleJsonClient().resource( endpointValue ).path( "resources/check" );
	}

	public SimpleJsonClient getConnectionClient(
		String endpointValue ) {

		return new SimpleJsonClient().resource( endpointValue ).path( "resources/login" );
	}

	//
	// public ArrayList<WS_Task> getDeletedTasksList() {
	// return deletedTasksList;
	// }

	public DataAccessService getDaoService() {
		return daoService;
	}

	public SimpleJsonClient getGcmRegistrationClient(
		String endpointValue ) {

		return new SimpleJsonClient().resource( endpointValue ).path( "resources/gcm-register" );
	}

	public SimpleJsonClient getGcmUnregistrationClient(
		String endpointValue ) {

		return new SimpleJsonClient().resource( endpointValue ).path( "resources/gcm-unregister" );
	}

	public SimpleJsonClient getRegisterClient(
		String endpointValue ) {
		return new SimpleJsonClient().resource( endpointValue ).path( "resources/register" );
	}

	public List<TT_Task> getRootTasksList() {
		return treeController.getRootTasksList();
	}

	public SimpleJsonClient getSync1Client(
		String endpointValue ) {
		return new SimpleJsonClient().resource( endpointValue ).path( "resources/syncing1" );
	}

	public SimpleJsonClient getSync2Client(
		String endpointValue ) {
		return new SimpleJsonClient().resource( endpointValue ).path( "resources/syncing2" );
	}

	//
	// public UserSession getUserSession() {
	// return userSession;
	// }

	public TreeStateManager<TT_Task> getTreeManager() {
		return treeManager;
	}

	//
	// public void load(
	// Context applicationContext ) {
	// treeController = new OrderedTaskTreeController( new
	// TT_UserTaskContainer() );
	//
	// TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager
	// );
	// TaskDB taskDB = new TaskDB( applicationContext );
	// taskDB.open();
	// taskDB.readTasksInfo();
	//
	// treeController.reinit( taskDB.getTasks() );
	// // deletedTasksList.addAll( taskDB.getDeletedTasks() );
	//
	// taskDB.close();
	//
	// for ( TT_Task task : treeController.getRootTasksList() )
	// {
	// treeBuilder.sequentiallyAddNextNode( task, 0 );
	// buildTreeRecursively( task, treeBuilder );
	// }
	//
	// for ( TT_Task task : treeController.getTaskMap().values() )
	// {
	// if ( treeManager.isInTree( task ) )
	// {
	// if ( !taskDB.getExpandedSet().contains( task.getID() ) )
	// {
	// treeManager.collapseChildren( task );
	// }
	// else
	// {
	// treeManager.expandDirectChildren( task );
	// }
	// }
	// }
	// }

	public boolean isExpanded(
		TT_Task task ) {
		return treeManager.getNodeInfo( task ).isExpanded();
	}

	public UserSession loadUserSessionFromCache(
		Context appContext ) {
		File cacheFile = new File( appContext.getCacheDir(), CACHE_FILENAME );

		if ( cacheFile.exists() )
		{
			try
			{
				InputStreamReader isr = new InputStreamReader( new FileInputStream( cacheFile ) );
				UserSession userSession = GSON_SERIALIZER.fromJson( isr, UserSession.class );
				isr.close();
				return userSession;
			}
			catch ( FileNotFoundException e )
			{
				e.printStackTrace();
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 
	 * @param destParentTask
	 *            the copied task will be a subtask of destParentTask
	 * @return the copied task
	 */
	public TT_Task pasteTask(
		TT_Task destParentTask ) {
		TT_Task childTask = copiedTask.getCopy();

		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		treeBuilder.addRelation( destParentTask, childTask );
		buildTreeRecursively( childTask, treeBuilder );

		Set<TT_Task> modifiedTasks = treeController.addTask( childTask, destParentTask.getID() );

		daoService.createOrUpdateTasks( modifiedTasks, retrieveExpandedSetFrom( modifiedTasks ) );

		return childTask;
	}

	public void registerDeviceOnGCM(
		Context context,
		String endpoint ) {
		// Tentative d'enregistrement pour le PUSH
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice( context );
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest( context );
		final String regId = GCMRegistrar.getRegistrationId( context );
		if ( regId.equals( "" ) )
		{
			// Automatically registers application on startup.
			GCMRegistrar.register( context, GCMIntentService.SENDER_ID );
		}
		else
		{
			registerDeviceOnServer( regId, endpoint );
		}
	}

	public void registerDeviceOnServer(
		String regId,
		String endpoint ) {
		// Register on app server
		if ( !regId.equals( "" ) && getDaoService().getUserSession() != null )
		{
			String userName = getDaoService().getUserSession().getUserName();
			try
			{
				getGcmRegistrationClient( endpoint ).post( GcmRegistrationResponse.class,
					new GcmRegistrationRequest( userName, regId ) );
			}
			catch ( E_BadResponseStatus e )
			{
				e.printStackTrace();
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	public void reset() {
		init();
	}

	//
	// public void setDeletedTasksList(
	// ArrayList<WS_Task> deletedTasksList ) {
	// this.deletedTasksList = deletedTasksList;
	// }

	public void saveUserSessionToCache(
		Context appContext,
		UserSession userSession ) {
		File cacheFile = new File( appContext.getCacheDir(), CACHE_FILENAME );

		if ( userSession != null )
		{
			try
			{
				FileOutputStream fos = new FileOutputStream( cacheFile );
				String userSessionJson = GSON_SERIALIZER.toJson( userSession );

				fos.write( userSessionJson.getBytes() );
				fos.close();
			}
			catch ( FileNotFoundException e )
			{
				e.printStackTrace();
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	public void setDaoService(
		DataAccessService daoService ) {
		this.daoService = daoService;
	}

	public void setRootTasksList(
		ArrayList<TT_Task> rootTasksList ) {
		treeController.getRootContainer().setRootTasks( rootTasksList );
	}

	public void setStatus(
		TT_Task task,
		int status ) {
		Set<TT_Task> modifiedTasks = null;
		if ( status == TT_Task.TODO )
		{
			modifiedTasks = treeController.unvalidateTask( task.getID() );
		}
		else
		{
			modifiedTasks = treeController.validateTask( task.getID() );
		}

		daoService.createOrUpdateTasks( modifiedTasks, retrieveExpandedSetFrom( modifiedTasks ) );

		treeManager.refresh();
	}

	public void setTreeManager(
		TreeStateManager<TT_Task> treeManager ) {
		this.treeManager = treeManager;
	}

	//
	// public void setUserSession(
	// UserSession userSession ) {
	// this.userSession = userSession;
	// }

	@Override
	public void syncFinalized() {
		// Do nothing
	}

	//
	// public void synchronizeWithDatastore(
	// String endPointValue ) {
	// SyncingStartRequest request = new SyncingStartRequest( userSession,
	// TT_UserTaskContainer.DEFAULT_NAME );
	//
	// HashMap<String, WS_Task> wsTasksMap = new HashMap<String, WS_Task>();
	// HashSet<String> collapsedSet = new HashSet<String>();
	//
	// for ( TT_Task task : treeController.getTaskMap().values() )
	// {
	// wsTasksMap.put( task.getID(), new WS_Task( task ) );
	// request.addId( new TaskStamp( task.getID(),
	// task.getLastModificationDate() ) );
	// if ( !task.getChildrenTask().isEmpty() && !treeManager.getNodeInfo( task
	// ).isExpanded() )
	// {
	// collapsedSet.add( task.getID() );
	// }
	// }
	// //
	// // for ( WS_Task task : deletedTasksList )
	// // {
	// // request.addRemovedId( task.getId() );
	// // }
	//
	// try
	// {
	// SyncingStartResponse response = getSync1Client( endPointValue ).post(
	// SyncingStartResponse.class, request );
	//
	// // On vide l'arbre
	// treeManager.clear();
	//
	// // On se débarasse des noeuds supprimés
	// for ( String deletedId : response.getDeletedIds() )
	// {
	// // tasksMap.get( deletedId ).setParent( null );
	// wsTasksMap.remove( deletedId );
	// collapsedSet.remove( deletedId );
	// }
	// //
	// // deletedTasksList.clear();
	//
	// // On met tout l'arbre à jour
	// for ( WS_Task taskToAdd : response.getTasksToAdd() )
	// {
	// wsTasksMap.put( taskToAdd.getId(), taskToAdd );
	// }
	//
	// for ( WS_Task wsTask : response.getMoreRecentTasks() )
	// {
	// wsTask.update( wsTasksMap.get( wsTask.getId() ) );
	// }
	//
	// // S'il y'a des informations à envoyer…
	// if ( !response.getNeedUpdateIds().isEmpty() )
	// {
	// SyncingFinalRequest finalRequest = new SyncingFinalRequest( userSession,
	// TT_UserTaskContainer.DEFAULT_NAME );
	//
	// for ( String needUpdateId : response.getNeedUpdateIds() )
	// {
	// finalRequest.addUpToDateTask( wsTasksMap.get( needUpdateId ) );
	// }
	//
	// SyncingFinalResponse finalResponse = getSync2Client( endPointValue
	// ).post( SyncingFinalResponse.class,
	// finalRequest );
	//
	// for ( WS_Task wsTask : finalResponse.getUpToDateTasks() )
	// {
	// wsTask.update( wsTasksMap.get( wsTask.getId() ) );
	// }
	// }
	//
	// // On reconstruit l'arbre…
	// treeController.reinit( wsTasksMap.values() );
	//
	// TreeBuilder<TT_Task> builder = new TreeBuilder<TT_Task>( getTreeManager()
	// );
	// for ( TT_Task task : treeController.getRootTasksList() )
	// {
	// builder.sequentiallyAddNextNode( task, 0 );
	// buildTreeRecursively( task, builder );
	// }
	//
	// // … sans oublier de replier les noeuds déjà pliés.
	// for ( String collapsedId : collapsedSet )
	// {
	// if ( treeManager.isInTree( treeController.getTaskMap().get( collapsedId )
	// ) )
	// {
	// treeManager.collapseChildren( treeController.getTaskMap().get(
	// collapsedId ) );
	// }
	// }
	// }
	// catch ( IOException e )
	// {
	// return;
	// }
	// catch ( E_BadResponseStatus e )
	// {
	// e.printStackTrace();
	// return;
	// }
	// }

	@Override
	public void syncStarted() {
		// Do nothing
	}

	public void unregisterDeviceFromServer(
		String regId,
		String endpoint ) {
		// Register on app server
		if ( !regId.equals( "" ) )
		{
			try
			{
				getGcmUnregistrationClient( endpoint ).post( GcmUnregistrationResponse.class,
					new GcmUnregistrationRequest( regId ) );
			}
			catch ( E_BadResponseStatus e )
			{
				e.printStackTrace();
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}

	private void buildTreeRecursively(
		TT_Task parent,
		TreeBuilder<TT_Task> builder ) {
		for ( TT_Task child : parent.getChildrenTask() )
		{
			builder.addRelation( parent, child );
			buildTreeRecursively( child, builder );
		}
	}

	private void init() {
		copiedTask = null;
		// deletedTasksList = new ArrayList<WS_Task>();
		treeManager = new InMemoryTreeStateManager<TT_Task>();
		treeController = new OrderedTaskTreeController( new TT_UserTaskContainer() );
	}

	private Set<String> retrieveExpandedSetFrom(
		Collection<TT_Task> tasks ) {
		HashSet<String> expandedSet = new HashSet<String>();

		for ( TT_Task task : tasks )
		{
			if ( isExpanded( task ) )
			{
				expandedSet.add( task.getID() );
			}
		}

		return expandedSet;
	}

	//
	// private void saveTaskToDB(
	// TT_Task taskToSave,
	// TaskDB taskDB ) {
	// if ( taskToSave == null )
	// {
	// return;
	// }
	//
	// if ( treeManager.isInTree( taskToSave ) )
	// {
	// taskDB.insertOrUpdateTask( taskToSave, treeManager.getNodeInfo(
	// taskToSave ).isExpanded() );
	// }
	// else
	// {
	// taskDB.insertOrUpdateTask( taskToSave, false );
	// }
	// }

	// private UserSession userSession;
	private TreeStateManager<TT_Task>	treeManager;
	private TT_Task						copiedTask;
	// private ArrayList<WS_Task> deletedTasksList;

	private OrderedTaskTreeController	treeController;
	private DataAccessService			daoService;
}
