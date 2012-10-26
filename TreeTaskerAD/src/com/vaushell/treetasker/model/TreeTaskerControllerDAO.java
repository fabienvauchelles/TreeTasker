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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import android.content.Context;

import com.google.gson.Gson;
import com.vaushell.treetasker.application.storage.TaskDB;
import com.vaushell.treetasker.client.E_BadResponseStatus;
import com.vaushell.treetasker.client.SimpleJsonClient;
import com.vaushell.treetasker.net.SyncingFinalRequest;
import com.vaushell.treetasker.net.SyncingFinalResponse;
import com.vaushell.treetasker.net.SyncingStartRequest;
import com.vaushell.treetasker.net.SyncingStartResponse;
import com.vaushell.treetasker.net.TaskStamp;
import com.vaushell.treetasker.net.UserSession;
import com.vaushell.treetasker.net.WS_Task;

public class TreeTaskerControllerDAO
{
	private static class TreeTaskerControllerDAOHolder
	{
		private static final TreeTaskerControllerDAO	INSTANCE	= new TreeTaskerControllerDAO();
	}

	// PUBLIC
	public static final String				TEST_RESOURCE	= "http://10.0.2.2:8888/";
	public static final String				WEB_RESOURCE	= "https://vsh2-test.appspot.com/";

	public static final String				RESOURCE		= TEST_RESOURCE;

	// PRIVATE
	private static final Gson				GSON_SERIALIZER	= new Gson();

	private static final String				CACHE_FILENAME	= "user_cache.json";

	private static final SimpleJsonClient	SYNCING_CLIENT1	= new SimpleJsonClient().resource(
																TreeTaskerControllerDAO.RESOURCE ).path(
																"resources/syncing1" );

	private static final SimpleJsonClient	SYNCING_CLIENT2	= new SimpleJsonClient().resource(
																TreeTaskerControllerDAO.RESOURCE ).path(
																"resources/syncing2" );

	public static TreeTaskerControllerDAO getInstance() {
		return TreeTaskerControllerDAOHolder.INSTANCE;
	}

	//
	// public static List<TT_Task> getMockTaskList1() {
	// List<TT_Task> mockList = new ArrayList<TT_Task>();
	//
	// TT_Task construireMaison = new TT_Task( UUID.randomUUID().toString(),
	// "Construire la maison", "", new Date(),
	// TT_Task.TODO );
	//
	// construireMaison.addChildTask( new TT_Task( UUID.randomUUID().toString(),
	// "Acheter terrain", "", new Date(),
	// TT_Task.DONE ) );
	//
	// TT_Task acheterMatos = new TT_Task( UUID.randomUUID().toString(),
	// "Acheter matï¿½riel", "", new Date(),
	// TT_Task.TODO );
	// acheterMatos.addChildTask( new TT_Task( UUID.randomUUID().toString(),
	// "Acheter brique", "", new Date(),
	// TT_Task.TODO ) );
	// acheterMatos.addChildTask( new TT_Task( UUID.randomUUID().toString(),
	// "Acheter brouette", "", new Date(),
	// TT_Task.TODO ) );
	// construireMaison.addChildTask( acheterMatos );
	//
	// construireMaison.addChildTask( new TT_Task( UUID.randomUUID().toString(),
	// "Construire les murs", "",
	// new Date(), TT_Task.TODO ) );
	// construireMaison.addChildTask( new TT_Task( UUID.randomUUID().toString(),
	// "Construire le toit", "", new Date(),
	// TT_Task.TODO ) );
	//
	// TT_Task passerPermis = new TT_Task( UUID.randomUUID().toString(),
	// "Passer le permis", "", new Date(),
	// TT_Task.TODO );
	// passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(),
	// "S'inscrire ï¿½ l'auto-ï¿½cole", "",
	// new Date(), TT_Task.DONE ) );
	// passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(),
	// "Obtenir le code", "", new Date(),
	// TT_Task.TODO ) );
	// passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(),
	// "Faire 20h de conduite", "", new Date(),
	// TT_Task.TODO ) );
	//
	// TT_Task accederAuLevelDesPoneys = new TT_Task(
	// UUID.randomUUID().toString(), "Accï¿½der au niveau cachï¿½", "",
	// new Date(), TT_Task.TODO );
	//
	// TT_Task rassemblerIngredients = new TT_Task(
	// UUID.randomUUID().toString(), "Trouver les ingrï¿½dients", "",
	// new Date(), TT_Task.TODO );
	// rassemblerIngredients.addChildTask( new TT_Task(
	// UUID.randomUUID().toString(), "Trouver champignon noir", "",
	// new Date(), TT_Task.TODO ) );
	// rassemblerIngredients.addChildTask( new TT_Task(
	// UUID.randomUUID().toString(), "Trouver tibia de lï¿½oric", "",
	// new Date(), TT_Task.TODO ) );
	// rassemblerIngredients.addChildTask( new TT_Task(
	// UUID.randomUUID().toString(), "Trouver arc-en-ciel liquide",
	// "", new Date(), TT_Task.TODO ) );
	// rassemblerIngredients.addChildTask( new TT_Task(
	// UUID.randomUUID().toString(), "Trouver pierre baragouinante",
	// "", new Date(), TT_Task.TODO ) );
	// rassemblerIngredients.addChildTask( new TT_Task(
	// UUID.randomUUID().toString(), "Acheter cloche de Wirt", "",
	// new Date(), TT_Task.TODO ) );
	// accederAuLevelDesPoneys.addChildTask( rassemblerIngredients );
	//
	// accederAuLevelDesPoneys.addChildTask( new TT_Task(
	// UUID.randomUUID().toString(),
	// "Construire le baton de bouvier", "", new Date(), TT_Task.DONE ) );
	// accederAuLevelDesPoneys.addChildTask( new TT_Task(
	// UUID.randomUUID().toString(), "Ouvrir le portail", "",
	// new Date(), TT_Task.TODO ) );
	//
	// mockList.add( construireMaison );
	// mockList.add( passerPermis );
	// mockList.add( accederAuLevelDesPoneys );
	//
	// return mockList;
	// }

	private TreeTaskerControllerDAO()
	{
		init();
	}

	public void addRootTask(
		TT_Task rootTask ) {
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		treeController.addRootTask( rootTask );
		treeBuilder.sequentiallyAddNextNode( rootTask, 0 );
	}

	public void addSubTask(
		TT_Task parentTask,
		TT_Task childTask ) {
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		treeController.addTask( childTask, parentTask.getID() );
		treeBuilder.addRelation( parentTask, childTask );
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
		for ( TT_Task deletedTask : treeController.removeTask( task.getID() ) )
		{
			deletedTasksList.add( new WS_Task( deletedTask ) );
		}

		treeManager.removeNodeRecursively( task );
	}

	public void edit(
		TT_Task task,
		String title,
		String description ) {
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( new Date() );
	}

	public ArrayList<WS_Task> getDeletedTasksList() {
		return deletedTasksList;
	}

	public List<TT_Task> getRootTasksList() {
		return treeController.getRootTasksList();
	}

	public TreeStateManager<TT_Task> getTreeManager() {
		return treeManager;
	}

	public UserSession getUserSession() {
		return userSession;
	}

	public void load(
		Context applicationContext ) {
		loadUserSession( applicationContext );
		treeController = new OrderedTaskTreeController( userSession.getUserName() );

		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		TaskDB taskDB = new TaskDB( applicationContext );
		taskDB.open();
		taskDB.readTasksInfo();

		treeController.reinit( taskDB.getTasks() );
		deletedTasksList.addAll( taskDB.getDeletedTasks() );

		taskDB.close();

		for ( TT_Task task : treeController.getRootTasksList() )
		{
			treeBuilder.sequentiallyAddNextNode( task, 0 );
			buildTreeRecursively( task, treeBuilder );
		}

		for ( TT_Task task : treeController.getTaskMap().values() )
		{
			if ( treeManager.isInTree( task ) )
			{
				if ( !taskDB.getExpandedSet().contains( task ) )
				{
					treeManager.collapseChildren( task );
				}
				else
				{
					treeManager.expandDirectChildren( task );
				}
			}
		}
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

	public void pasteTask(
		TT_Task destParentTask ) {
		TT_Task childTask = copiedTask.getCopy();

		treeController.addTask( childTask, destParentTask.getID() );

		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		treeBuilder.addRelation( destParentTask, childTask );
		buildTreeRecursively( childTask, treeBuilder );
	}

	public void reset() {
		init();
	}

	public void save(
		Context applicationContext ) {
		saveUserSession( applicationContext );

		TaskDB taskDB = new TaskDB( applicationContext );
		taskDB.open();
		taskDB.resetTable();

		for ( TT_Task taskToSave : treeController.getTaskMap().values() )
		{
			saveTaskToDB( taskToSave, taskDB );
		}

		for ( WS_Task deletedTaskToSave : deletedTasksList )
		{
			saveTaskToDB( deletedTaskToSave.update( new TT_Task() ), taskDB );
		}

		taskDB.close();
	}

	public void saveUserSession(
		Context appContext,
		UserSession userSession ) {
		File cacheFile = new File( appContext.getCacheDir(), CACHE_FILENAME );

		if ( userSession == null )
		{
			if ( cacheFile.exists() )
			{
				cacheFile.delete();
			}
		}
		else
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

	public void setDeletedTasksList(
		ArrayList<WS_Task> deletedTasksList ) {
		this.deletedTasksList = deletedTasksList;
	}

	public void setRootTasksList(
		ArrayList<TT_Task> rootTasksList ) {
		treeController.getRootContainer().setRootTasks( rootTasksList );
	}

	public void setStatus(
		TT_Task task,
		int status ) {
		task.setStatus( status );
		task.setLastModificationDate( new Date() );
	}

	public void setTreeManager(
		TreeStateManager<TT_Task> treeManager ) {
		this.treeManager = treeManager;
	}

	public void setUserSession(
		UserSession userSession ) {
		this.userSession = userSession;
	}

	public void synchronizeWithDatastore() {
		SyncingStartRequest request = new SyncingStartRequest( userSession, TT_UserTaskContainer.DEFAULT_NAME );

		HashMap<String, WS_Task> wsTasksMap = new HashMap<String, WS_Task>();
		HashSet<String> collapsedSet = new HashSet<String>();

		for ( TT_Task task : treeController.getTaskMap().values() )
		{
			wsTasksMap.put( task.getID(), new WS_Task( task ) );
			request.addId( new TaskStamp( task.getID(), task.getLastModificationDate() ) );
			if ( !task.getChildrenTask().isEmpty() && !treeManager.getNodeInfo( task ).isExpanded() )
			{
				collapsedSet.add( task.getID() );
			}
		}

		for ( WS_Task task : deletedTasksList )
		{
			request.addRemovedId( task.getId() );
		}

		try
		{
			SyncingStartResponse response = SYNCING_CLIENT1.post( SyncingStartResponse.class, request );

			// On vide l'arbre
			treeManager.clear();

			// On se débarasse des noeuds supprimés
			for ( String deletedId : response.getDeletedIds() )
			{
				// tasksMap.get( deletedId ).setParent( null );
				wsTasksMap.remove( deletedId );
				collapsedSet.remove( deletedId );
			}

			deletedTasksList.clear();

			// On met tout l'arbre à jour
			for ( WS_Task taskToAdd : response.getTasksToAdd() )
			{
				wsTasksMap.put( taskToAdd.getId(), taskToAdd );
			}

			for ( WS_Task wsTask : response.getMoreRecentTasks() )
			{
				wsTask.update( wsTasksMap.get( wsTask.getId() ) );
			}

			// S'il y'a des informations à envoyer…
			if ( !response.getNeedUpdateIds().isEmpty() )
			{
				SyncingFinalRequest finalRequest = new SyncingFinalRequest( userSession,
					TT_UserTaskContainer.DEFAULT_NAME );

				for ( String needUpdateId : response.getNeedUpdateIds() )
				{
					finalRequest.addUpToDateTask( wsTasksMap.get( needUpdateId ) );
				}

				SyncingFinalResponse finalResponse = SYNCING_CLIENT2.post( SyncingFinalResponse.class, finalRequest );

				for ( WS_Task wsTask : finalResponse.getUpToDateTasks() )
				{
					wsTask.update( wsTasksMap.get( wsTask.getId() ) );
				}
			}

			// On reconstruit l'arbre…
			treeController.reinit( wsTasksMap.values() );

			TreeBuilder<TT_Task> builder = new TreeBuilder<TT_Task>( getTreeManager() );
			for ( TT_Task task : treeController.getRootTasksList() )
			{
				builder.sequentiallyAddNextNode( task, 0 );
				buildTreeRecursively( task, builder );
			}

			// … sans oublier de replier les noeuds déjà pliés.
			for ( String collapsedId : collapsedSet )
			{
				if ( treeManager.isInTree( treeController.getTaskMap().get( collapsedId ) ) )
				{
					treeManager.collapseChildren( treeController.getTaskMap().get( collapsedId ) );
				}
			}
		}
		catch ( ClientProtocolException e )
		{
			return;
		}
		catch ( E_BadResponseStatus e )
		{
			e.printStackTrace();
			return;
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
		userSession = null;
		copiedTask = null;
		deletedTasksList = new ArrayList<WS_Task>();
		treeManager = new InMemoryTreeStateManager<TT_Task>();
		treeController = null;
	}

	private void loadUserSession(
		Context appContext ) {
		File cacheFile = new File( appContext.getCacheDir(), CACHE_FILENAME );

		if ( cacheFile.exists() )
		{
			try
			{
				InputStreamReader isr = new InputStreamReader( new FileInputStream( cacheFile ) );
				userSession = GSON_SERIALIZER.fromJson( isr, UserSession.class );
				isr.close();
			}
			catch ( FileNotFoundException e )
			{
				userSession = null;
				e.printStackTrace();
			}
			catch ( IOException e )
			{
				userSession = null;
				e.printStackTrace();
			}
		}
	}

	private void saveTaskToDB(
		TT_Task taskToSave,
		TaskDB taskDB ) {
		if ( taskToSave == null )
		{
			return;
		}

		if ( treeManager.isInTree( taskToSave ) )
		{
			taskDB.insertTask( taskToSave, treeManager.getNodeInfo( taskToSave ).isExpanded() );
		}
		else
		{
			taskDB.insertTask( taskToSave, false );
		}
	}

	private void saveUserSession(
		Context appContext ) {
		File cacheFile = new File( appContext.getCacheDir(), CACHE_FILENAME );

		if ( userSession == null )
		{
			if ( cacheFile.exists() )
			{
				cacheFile.delete();
			}
		}
		else
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

	// private Set<TT_Task> unrecursifyTasks(
	// Collection<TT_Task> rootTasksList ) {
	// HashSet<TT_Task> tasksSet = new HashSet<TT_Task>();
	//
	// for ( TT_Task rootTask : rootTasksList )
	// {
	// unrecursifyTasksRec( rootTask, tasksSet );
	// }
	//
	// return tasksSet;
	// }
	//
	// private void unrecursifyTasksRec(
	// TT_Task task,
	// HashSet<TT_Task> tasksSet ) {
	// tasksSet.add( task );
	//
	// for ( TT_Task childTask : task.getChildrenTask() )
	// {
	// unrecursifyTasksRec( childTask, tasksSet );
	// }
	// }

	private UserSession					userSession;

	private TreeStateManager<TT_Task>	treeManager;

	private TT_Task						copiedTask;

	private ArrayList<WS_Task>			deletedTasksList;

	private OrderedTaskTreeController	treeController;
}
