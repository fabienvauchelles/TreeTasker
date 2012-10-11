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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;

import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import android.content.Context;

import com.google.gson.Gson;
import com.vaushell.treetasker.application.storage.TaskDB;
import com.vaushell.treetasker.client.E_BadResponseStatus;
import com.vaushell.treetasker.client.SimpleJsonClient;
import com.vaushell.treetasker.module.SyncingFinalRequest;
import com.vaushell.treetasker.module.SyncingFinalResponse;
import com.vaushell.treetasker.module.SyncingStartRequest;
import com.vaushell.treetasker.module.SyncingStartResponse;
import com.vaushell.treetasker.module.TaskStamp;
import com.vaushell.treetasker.module.UserSession;
import com.vaushell.treetasker.module.WS_Task;

public class TreeTaskerControllerDAO
{
	private static class TreeTaskerControllerDAOHolder
	{
		private static final TreeTaskerControllerDAO	INSTANCE	= new TreeTaskerControllerDAO();
	}

	// PUBLIC
	public static final String				TEST_RESOURCE	= "http://10.0.2.2:8888/";
	public static final String				WEB_RESOURCE	= "https://vsh2-test.appspot.com/";

	public static final String				RESOURCE		= WEB_RESOURCE;

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

	public static List<TT_Task> getMockTaskList1() {
		List<TT_Task> mockList = new ArrayList<TT_Task>();

		TT_Task construireMaison = new TT_Task( UUID.randomUUID().toString(), "Construire la maison", "", new Date(),
			TT_Task.TODO );

		construireMaison.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Acheter terrain", "", new Date(),
			TT_Task.DONE ) );

		TT_Task acheterMatos = new TT_Task( UUID.randomUUID().toString(), "Acheter mat�riel", "", new Date(),
			TT_Task.TODO );
		acheterMatos.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Acheter brique", "", new Date(),
			TT_Task.TODO ) );
		acheterMatos.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Acheter brouette", "", new Date(),
			TT_Task.TODO ) );
		construireMaison.addChildTask( acheterMatos );

		construireMaison.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Construire les murs", "",
			new Date(), TT_Task.TODO ) );
		construireMaison.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Construire le toit", "", new Date(),
			TT_Task.TODO ) );

		TT_Task passerPermis = new TT_Task( UUID.randomUUID().toString(), "Passer le permis", "", new Date(),
			TT_Task.TODO );
		passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(), "S'inscrire � l'auto-�cole", "",
			new Date(), TT_Task.DONE ) );
		passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Obtenir le code", "", new Date(),
			TT_Task.TODO ) );
		passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Faire 20h de conduite", "", new Date(),
			TT_Task.TODO ) );

		TT_Task accederAuLevelDesPoneys = new TT_Task( UUID.randomUUID().toString(), "Acc�der au niveau cach�", "",
			new Date(), TT_Task.TODO );

		TT_Task rassemblerIngredients = new TT_Task( UUID.randomUUID().toString(), "Trouver les ingr�dients", "",
			new Date(), TT_Task.TODO );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Trouver champignon noir", "",
			new Date(), TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Trouver tibia de l�oric", "",
			new Date(), TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Trouver arc-en-ciel liquide",
			"", new Date(), TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Trouver pierre baragouinante",
			"", new Date(), TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Acheter cloche de Wirt", "",
			new Date(), TT_Task.TODO ) );
		accederAuLevelDesPoneys.addChildTask( rassemblerIngredients );

		accederAuLevelDesPoneys.addChildTask( new TT_Task( UUID.randomUUID().toString(),
			"Construire le baton de bouvier", "", new Date(), TT_Task.DONE ) );
		accederAuLevelDesPoneys.addChildTask( new TT_Task( UUID.randomUUID().toString(), "Ouvrir le portail", "",
			new Date(), TT_Task.TODO ) );

		mockList.add( construireMaison );
		mockList.add( passerPermis );
		mockList.add( accederAuLevelDesPoneys );

		return mockList;
	}

	private TreeTaskerControllerDAO()
	{
		init();
	}

	public void addRootTask(
		TT_Task rootTask ) {
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		rootTasksList.add( rootTask );
		treeBuilder.sequentiallyAddNextNode( rootTask, 0 );
	}

	public void addSubTask(
		TT_Task parentTask,
		TT_Task childTask ) {
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		childTask.setParent( parentTask );
		treeBuilder.addRelation( parentTask, childTask );
	}

	public void buildTreeRecursively(
		TT_Task parent,
		TreeBuilder<TT_Task> builder ) {
		for ( TT_Task child : parent.getChildrenTask() )
		{
			builder.addRelation( parent, child );
			buildTreeRecursively( child, builder );
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
		rootTasksList.remove( task );
		rootDeletedTasksList.add( task );
		task.setStatusRecursively( TT_Task.DELETED );
		task.setLastModificationDateRecursively( new Date() );
		task.setParent( null );
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

	public ArrayList<TT_Task> getRootDeletedTasksList() {
		return rootDeletedTasksList;
	}

	public ArrayList<TT_Task> getRootTasksList() {
		return rootTasksList;
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

		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		TaskDB taskDB = new TaskDB( applicationContext );
		taskDB.open();
		taskDB.readTasksInfo();
		rootTasksList.addAll( taskDB.getRootTasks() );
		rootDeletedTasksList.addAll( taskDB.getRootDeletedTasks() );
		taskDB.close();

		for ( TT_Task task : rootTasksList )
		{
			treeBuilder.sequentiallyAddNextNode( task, 0 );
			buildTreeRecursively( task, treeBuilder );
		}

		for ( TT_Task task : taskDB.getExpandedMap().keySet() )
		{
			if ( treeManager.isInTree( task ) )
			{
				if ( !taskDB.getExpandedMap().get( task ) )
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
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>( treeManager );
		treeBuilder.addRelation( destParentTask, childTask );
		childTask.setParent( destParentTask );
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
		for ( TT_Task taskToSave : rootTasksList )
		{
			saveRecursively( taskToSave, taskDB );
		}
		for ( TT_Task deletedTaskToSave : rootDeletedTasksList )
		{
			saveRecursively( deletedTaskToSave, taskDB );
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

	public void setRootDeletedTasksList(
		ArrayList<TT_Task> rootDeletedTasksList ) {
		this.rootDeletedTasksList = rootDeletedTasksList;
	}

	public void setRootTasksList(
		ArrayList<TT_Task> rootTasksList ) {
		this.rootTasksList = rootTasksList;
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

		HashMap<String, TT_Task> tasksMap = new HashMap<String, TT_Task>();
		HashSet<TT_Task> expandedSet = new HashSet<TT_Task>();

		for ( TT_Task task : unrecursifyTasks( rootTasksList ) )
		{
			tasksMap.put( task.getID(), task );
			if ( treeManager.getNodeInfo( task ).isExpanded() )
			{
				expandedSet.add( task );
			}
		}

		for ( TT_Task task : tasksMap.values() )
		{
			request.addId( new TaskStamp( task.getID(), task.getLastModificationDate() ) );
		}

		for ( TT_Task task : unrecursifyTasks( rootDeletedTasksList ) )
		{
			request.addRemovedId( task.getID() );
		}

		try
		{
			SyncingStartResponse response = SYNCING_CLIENT1.post( SyncingStartResponse.class, request );

			// On vide l'arbre
			treeManager.clear();

			// On se d�barasse des noeuds supprim�s
			for ( String deletedId : response.getDeletedIds() )
			{
				tasksMap.get( deletedId ).setParent( null );
				tasksMap.remove( deletedId );
			}

			rootDeletedTasksList.clear();

			// On met tout l'arbre � jour
			for ( WS_Task taskToAdd : response.getTasksToAdd() )
			{
				TT_Task currentNewTask = new TT_Task();
				taskToAdd.update( currentNewTask );
				tasksMap.put( currentNewTask.getID(), currentNewTask );
			}

			for ( WS_Task wsTask : response.getMoreRecentTasks() )
			{
				wsTask.update( tasksMap.get( wsTask.getId() ), tasksMap );
			}

			for ( WS_Task taskToAdd : response.getTasksToAdd() )
			{
				tasksMap.get( taskToAdd.getId() ).setParent( tasksMap.get( taskToAdd.getParentId() ) );
			}

			// On reconstruit l'arbre
			TreeBuilder<TT_Task> builder = new TreeBuilder<TT_Task>( getTreeManager() );
			for ( TT_Task task : tasksMap.values() )
			{
				if ( task.getParent() == null )
				{
					addRootTask( task );
					buildTreeRecursively( task, builder );
				}
			}

			for ( TT_Task expandedTask : expandedSet )
			{
				treeManager.expandDirectChildren( expandedTask );
			}

			// S'il y'a des informations � envoyer�
			if ( !response.getNeedUpdateIds().isEmpty() )
			{
				SyncingFinalRequest finalRequest = new SyncingFinalRequest( userSession,
					TT_UserTaskContainer.DEFAULT_NAME );

				for ( String needUpdateId : response.getNeedUpdateIds() )
				{
					finalRequest.addUpToDateTask( new WS_Task( tasksMap.get( needUpdateId ) ) );
				}

				SYNCING_CLIENT2.post( SyncingFinalResponse.class, finalRequest );
			}
			else
			{
				return;
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

	private void init() {
		userSession = null;
		copiedTask = null;
		rootTasksList = new ArrayList<TT_Task>();
		rootDeletedTasksList = new ArrayList<TT_Task>();
		treeManager = new InMemoryTreeStateManager<TT_Task>();
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

	private void saveRecursively(
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
		for ( TT_Task child : taskToSave.getChildrenTask() )
		{
			saveRecursively( child, taskDB );
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

	private Set<TT_Task> unrecursifyTasks(
		Collection<TT_Task> rootTasksList ) {
		HashSet<TT_Task> tasksSet = new HashSet<TT_Task>();

		for ( TT_Task rootTask : rootTasksList )
		{
			unrecursifyTasksRec( rootTask, tasksSet );
		}

		return tasksSet;
	}

	private void unrecursifyTasksRec(
		TT_Task task,
		HashSet<TT_Task> tasksSet ) {
		tasksSet.add( task );

		for ( TT_Task childTask : task.getChildrenTask() )
		{
			unrecursifyTasksRec( childTask, tasksSet );
		}
	}

	private UserSession					userSession;

	private TreeStateManager<TT_Task>	treeManager;

	private TT_Task						copiedTask;

	private ArrayList<TT_Task>			rootTasksList;

	private ArrayList<TT_Task>			rootDeletedTasksList;
}
