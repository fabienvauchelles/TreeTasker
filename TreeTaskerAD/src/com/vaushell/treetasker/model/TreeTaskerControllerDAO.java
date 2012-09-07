package com.vaushell.treetasker.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;

import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import android.content.Context;

import com.google.gson.Gson;
import com.vaushell.treetasker.application.storage.TaskDB;
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
	// PUBLIC
	public static TreeTaskerControllerDAO getInstance()
	{
		return TreeTaskerControllerDAOHolder.INSTANCE;
	}

	public static List<TT_Task> getMockTaskList1()
	{
		List<TT_Task> mockList = new ArrayList<TT_Task>();

		TT_Task construireMaison = new TT_Task( UUID.randomUUID().toString(),
		                                        "Construire la maison", "",
		                                        new Date(), TT_Task.TODO );

		construireMaison.addChildTask( new TT_Task( UUID.randomUUID()
		                                                .toString(),
		                                            "Acheter terrain", "",
		                                            new Date(), TT_Task.DONE ) );

		TT_Task acheterMatos = new TT_Task( UUID.randomUUID().toString(),
		                                    "Acheter matériel", "", new Date(),
		                                    TT_Task.TODO );
		acheterMatos.addChildTask( new TT_Task( UUID.randomUUID().toString(),
		                                        "Acheter brique", "",
		                                        new Date(), TT_Task.TODO ) );
		acheterMatos.addChildTask( new TT_Task( UUID.randomUUID().toString(),
		                                        "Acheter brouette", "",
		                                        new Date(), TT_Task.TODO ) );
		construireMaison.addChildTask( acheterMatos );

		construireMaison.addChildTask( new TT_Task( UUID.randomUUID()
		                                                .toString(),
		                                            "Construire les murs", "",
		                                            new Date(), TT_Task.TODO ) );
		construireMaison.addChildTask( new TT_Task( UUID.randomUUID()
		                                                .toString(),
		                                            "Construire le toit", "",
		                                            new Date(), TT_Task.TODO ) );

		TT_Task passerPermis = new TT_Task( UUID.randomUUID().toString(),
		                                    "Passer le permis", "", new Date(),
		                                    TT_Task.TODO );
		passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(),
		                                        "S'inscrire à l'auto-école",
		                                        "", new Date(), TT_Task.DONE ) );
		passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(),
		                                        "Obtenir le code", "",
		                                        new Date(), TT_Task.TODO ) );
		passerPermis.addChildTask( new TT_Task( UUID.randomUUID().toString(),
		                                        "Faire 20h de conduite", "",
		                                        new Date(), TT_Task.TODO ) );

		TT_Task accederAuLevelDesPoneys = new TT_Task(
		                                               UUID.randomUUID()
		                                                   .toString(),
		                                               "Accéder au niveau caché",
		                                               "", new Date(),
		                                               TT_Task.TODO );

		TT_Task rassemblerIngredients = new TT_Task( UUID.randomUUID()
		                                                 .toString(),
		                                             "Trouver les ingrédients",
		                                             "", new Date(),
		                                             TT_Task.TODO );
		rassemblerIngredients.addChildTask( new TT_Task(
		                                                 UUID.randomUUID()
		                                                     .toString(),
		                                                 "Trouver champignon noir",
		                                                 "", new Date(),
		                                                 TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task(
		                                                 UUID.randomUUID()
		                                                     .toString(),
		                                                 "Trouver tibia de léoric",
		                                                 "", new Date(),
		                                                 TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task(
		                                                 UUID.randomUUID()
		                                                     .toString(),
		                                                 "Trouver arc-en-ciel liquide",
		                                                 "", new Date(),
		                                                 TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task(
		                                                 UUID.randomUUID()
		                                                     .toString(),
		                                                 "Trouver pierre baragouinante",
		                                                 "", new Date(),
		                                                 TT_Task.TODO ) );
		rassemblerIngredients.addChildTask( new TT_Task(
		                                                 UUID.randomUUID()
		                                                     .toString(),
		                                                 "Acheter cloche de Wirt",
		                                                 "", new Date(),
		                                                 TT_Task.TODO ) );
		accederAuLevelDesPoneys.addChildTask( rassemblerIngredients );

		accederAuLevelDesPoneys.addChildTask( new TT_Task(
		                                                   UUID.randomUUID()
		                                                       .toString(),
		                                                   "Construire le baton de bouvier",
		                                                   "", new Date(),
		                                                   TT_Task.DONE ) );
		accederAuLevelDesPoneys.addChildTask( new TT_Task( UUID.randomUUID()
		                                                       .toString(),
		                                                   "Ouvrir le portail",
		                                                   "", new Date(),
		                                                   TT_Task.TODO ) );

		mockList.add( construireMaison );
		mockList.add( passerPermis );
		mockList.add( accederAuLevelDesPoneys );

		return mockList;
	}

	public void edit( TT_Task task,
	                  String title,
	                  String description )
	{
		task.setTitle( title );
		task.setDescription( description );
		task.setLastModificationDate( new Date() );
	}

	public void addSubTask( TT_Task parentTask,
	                        TT_Task childTask )
	{
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>(
		                                                             treeManager );
		childTask.setParent( parentTask );
		treeBuilder.addRelation( parentTask, childTask );
	}

	public void addRootTask( TT_Task rootTask )
	{
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>(
		                                                             treeManager );
		rootTasksList.add( rootTask );
		treeBuilder.sequentiallyAddNextNode( rootTask, 0 );
	}

	public void deleteTask( TT_Task task )
	{
		rootTasksList.remove( task );
		rootDeletedTasksList.add( task );
		task.setStatusRecursively( TT_Task.DELETED );
		task.setLastModificationDateRecursively( new Date() );
		task.setParent( null );
		treeManager.removeNodeRecursively( task );
	}

	public void copyTask( TT_Task srcTask )
	{
		copiedTask = srcTask.getCopy();
	}

	public void pasteTask( TT_Task destParentTask )
	{
		TT_Task childTask = copiedTask.getCopy();
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>(
		                                                             treeManager );
		treeBuilder.addRelation( destParentTask, childTask );
		childTask.setParent( destParentTask );
		buildTreeRecursively( childTask, treeBuilder );
	}

	public void setStatus( TT_Task task,
	                       int status )
	{
		task.setStatus( status );
		task.setLastModificationDate( new Date() );

	}

	public void buildTreeRecursively( TT_Task parent,
	                                  TreeBuilder<TT_Task> builder )
	{
		for ( TT_Task child : parent.getChildrenTask() )
		{
			builder.addRelation( parent, child );
			buildTreeRecursively( child, builder );
		}
	}

	public TreeStateManager<TT_Task> getTreeManager()
	{
		return treeManager;
	}

	public void setTreeManager( TreeStateManager<TT_Task> treeManager )
	{
		this.treeManager = treeManager;
	}

	public ArrayList<TT_Task> getRootTasksList()
	{
		return rootTasksList;
	}

	public void setRootTasksList( ArrayList<TT_Task> rootTasksList )
	{
		this.rootTasksList = rootTasksList;
	}

	public ArrayList<TT_Task> getRootDeletedTasksList()
	{
		return rootDeletedTasksList;
	}

	public void setRootDeletedTasksList( ArrayList<TT_Task> rootDeletedTasksList )
	{
		this.rootDeletedTasksList = rootDeletedTasksList;
	}

	public void setUserSession( UserSession userSession )
	{
		this.userSession = userSession;
	}

	public UserSession getUserSession()
	{
		return this.userSession;
	}

	public boolean canPaste()
	{
		return copiedTask != null;
	}

	public void reset()
	{
		init();
	}

	public void save( Context applicationContext )
	{
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

	public void load( Context applicationContext )
	{
		loadUserSession( applicationContext );

		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>(
		                                                             treeManager );
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

	public void synchronizeWithDatastore()
	{

		SyncingStartRequest request = new SyncingStartRequest(
		                                                       userSession,
		                                                       TT_UserTaskContainer.DEFAULT_NAME );

		HashMap<String, TT_Task> tasksMap = new HashMap<String, TT_Task>();

		for ( TT_Task task : unrecursifyTasks( rootTasksList ) )
		{
			tasksMap.put( task.getID(), task );
		}

		for ( TT_Task task : tasksMap.values() )
		{
			request.addId( new TaskStamp(
			                              task.getID(),
			                              task.getLastModificationDate() ) );
		}

		for ( TT_Task task : unrecursifyTasks( rootDeletedTasksList ) )
		{
			request.addRemovedId( task.getID() );
		}

		try
		{
			SyncingStartResponse response = SYNCING_CLIENT1.post( SyncingStartResponse.class,
			                                                      request );

			for ( WS_Task wsTask : response.getMoreRecentTasks() )
			{
				wsTask.update( tasksMap.get( wsTask.getID() ), tasksMap );
			}

			TreeSet<WS_Task> sortedTasksToAdd = new TreeSet<WS_Task>(
			                                                          new Comparator<WS_Task>()
			                                                          {
				                                                          @Override
				                                                          public int compare( WS_Task lhs,
				                                                                              WS_Task rhs )
				                                                          {
					                                                          return lhs.getLastModificationDate()
					                                                                    .compareTo( rhs.getLastModificationDate() );
				                                                          }
			                                                          } );
			sortedTasksToAdd.addAll( response.getTasksToAdd() );

			for ( WS_Task taskToAdd : sortedTasksToAdd )
			{
				TT_Task currentNewTask = new TT_Task();
				taskToAdd.update( currentNewTask );
				tasksMap.put( currentNewTask.getID(), currentNewTask );

				if ( taskToAdd.getParentId() == null )
				{
					addRootTask( currentNewTask );
				}
				else
				{
					addSubTask( tasksMap.get( taskToAdd.getParentId() ),
					            currentNewTask );
				}
			}

			for ( String deletedId : response.getDeletedIds() )
			{
				if ( tasksMap.containsKey( deletedId ) )
				{
					deleteTask( tasksMap.get( deletedId ) );
				}
			}

			rootDeletedTasksList.clear();

			if ( !response.getNeedUpdateIds().isEmpty() )
			{
				SyncingFinalRequest finalRequest = new SyncingFinalRequest(
				                                                            userSession,
				                                                            TT_UserTaskContainer.DEFAULT_NAME );

				for ( String needUpdateId : response.getNeedUpdateIds() )
				{
					finalRequest.addUpToDateTask( new WS_Task(
					                                           tasksMap.get( needUpdateId ) ) );
				}

				SYNCING_CLIENT2.post( SyncingFinalResponse.class,
				                      finalRequest );
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
	}

	// PRIVATE
	private static final Gson	          GSON_SERIALIZER	= new Gson();
	private static final String	          CACHE_FILENAME	= "user_cache.json";
	private static final SimpleJsonClient	SYNCING_CLIENT1	= new SimpleJsonClient().resource( "http://vsh2-test.appspot.com/resources/syncing1" );
	private static final SimpleJsonClient	SYNCING_CLIENT2	= new SimpleJsonClient().resource( "http://vsh2-test.appspot.com/resources/syncing2" );

	private UserSession	                  userSession;
	private TreeStateManager<TT_Task>	  treeManager;
	private TT_Task	                      copiedTask;
	private ArrayList<TT_Task>	          rootTasksList;
	private ArrayList<TT_Task>	          rootDeletedTasksList;

	private void init()
	{
		this.userSession = null;
		this.copiedTask = null;
		this.rootTasksList = new ArrayList<TT_Task>();
		this.rootDeletedTasksList = new ArrayList<TT_Task>();
		this.treeManager = new InMemoryTreeStateManager<TT_Task>();
	}

	private TreeTaskerControllerDAO()
	{
		init();
	}

	private static class TreeTaskerControllerDAOHolder
	{
		private static final TreeTaskerControllerDAO	INSTANCE	= new TreeTaskerControllerDAO();
	}

	private void saveRecursively( TT_Task taskToSave,
	                              TaskDB taskDB )
	{
		if ( taskToSave == null )
		{
			return;
		}

		if ( treeManager.isInTree( taskToSave ) )
		{
			taskDB.insertTask( taskToSave, treeManager.getNodeInfo( taskToSave )
			                                          .isExpanded() );
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

	private Set<TT_Task> unrecursifyTasks( Collection<TT_Task> rootTasksList )
	{
		HashSet<TT_Task> tasksSet = new HashSet<TT_Task>();

		for ( TT_Task rootTask : rootTasksList )
		{
			unrecursifyTasksRec( rootTask, tasksSet );
		}

		return tasksSet;
	}

	private void unrecursifyTasksRec( TT_Task task,
	                                  HashSet<TT_Task> tasksSet )
	{
		tasksSet.add( task );

		for ( TT_Task childTask : task.getChildrenTask() )
		{
			unrecursifyTasksRec( childTask, tasksSet );
		}
	}

	private void saveUserSession( Context appContext )
	{
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

	private void loadUserSession( Context appContext )
	{
		File cacheFile = new File( appContext.getCacheDir(), CACHE_FILENAME );

		if ( cacheFile.exists() )
		{
			try
			{
				InputStreamReader isr = new InputStreamReader(
				                                               new FileInputStream(
				                                                                    cacheFile ) );
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
}
