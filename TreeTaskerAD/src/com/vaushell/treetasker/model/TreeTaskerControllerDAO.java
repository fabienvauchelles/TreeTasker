package com.vaushell.treetasker.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeStateManager;
import android.content.Context;

import com.vaushell.treetasker.application.storage.TaskDB;

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
		                                    "Acheter mat�riel", "", new Date(),
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
		                                        "S'inscrire � l'auto-�cole",
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
		                                               "Acc�der au niveau cach�",
		                                               "", new Date(),
		                                               TT_Task.TODO );

		TT_Task rassemblerIngredients = new TT_Task( UUID.randomUUID()
		                                                 .toString(),
		                                             "Trouver les ingr�dients",
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
		                                                 "Trouver tibia de l�oric",
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
		task.setStatus( TT_Task.DONE );
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

	// PRIVATE
	private TreeStateManager<TT_Task>	treeManager;
	private TT_Task	                  copiedTask;
	private ArrayList<TT_Task>	      rootTasksList;
	private ArrayList<TT_Task>	      rootDeletedTasksList;

	private void init()
	{
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
}
