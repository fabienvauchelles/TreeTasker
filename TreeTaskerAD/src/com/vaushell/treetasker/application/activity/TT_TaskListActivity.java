package com.vaushell.treetasker.application.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.InMemoryTreeStateManager;
import pl.polidea.treeview.TreeBuilder;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.application.storage.TaskDB;
import com.vaushell.treetasker.model.TT_Task;

public class TT_TaskListActivity
    extends Activity
{
	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		this.view2taskMap = new HashMap<View, TT_Task>();
		this.dialogBuilder = new AlertDialog.Builder( this );
		this.copiedTask = null;
		this.rootTasksList = new ArrayList<TT_Task>();

		treeManager = new InMemoryTreeStateManager<TT_Task>();
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>(
		                                                             treeManager );

		TaskDB taskDB = new TaskDB( getApplicationContext() );
		taskDB.open();
		taskDB.readTasksInfo();
		rootTasksList.addAll( taskDB.getRootTasks() );
		taskDB.close();

		for ( TT_Task task : rootTasksList )
		{
			treeBuilder.sequentiallyAddNextNode( task, 0 );
			buildRecursively( task, treeBuilder );
		}

		for ( TT_Task task : taskDB.getExpandedMap().keySet() )
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

		AbstractTreeViewAdapter<TT_Task> adapter = new AbstractTreeViewAdapter<TT_Task>( this,
		                                                                                 treeManager,
		                                                                                 4 )
		{

			@Override
			public long getItemId( int position )
			{
				return getTreeId( position ).getID().hashCode();
			}

			@Override
			public View updateView( View view,
			                        final TreeNodeInfo<TT_Task> treeNodeInfo )
			{
				( (TextView) view.findViewById( R.id.aLBLtaskNameValue ) ).setText( treeNodeInfo.getId()
				                                                                                .getTitle() );
				final CheckBox cbView = (CheckBox) view.findViewById( R.id.aCBtaskDoneValue );
				cbView.setOnCheckedChangeListener( null );
				cbView.setChecked( treeNodeInfo.getId().getStatus() == TT_Task.DONE );
				cbView.setOnCheckedChangeListener( new CheckBox.OnCheckedChangeListener()
				{

					@Override
					public void onCheckedChanged( CompoundButton buttonView,
					                              boolean isChecked )
					{
						if ( isChecked )
						{
							treeNodeInfo.getId().setStatus( TT_Task.DONE );
							System.out.println( treeNodeInfo.getId().getTitle()
							                    + " is done ! "
							                    + treeNodeInfo.getId()
							                                  .getStatus() );
						}
						else
						{
							treeNodeInfo.getId().setStatus( TT_Task.TODO );
							System.out.println( treeNodeInfo.getId().getTitle()
							                    + " is todo ! "
							                    + treeNodeInfo.getId()
							                                  .getStatus() );
						}

					}
				} );
				view2taskMap.put( view, treeNodeInfo.getId() );
				return view;
			}

			@Override
			public View getNewChildView( final TreeNodeInfo<TT_Task> treeNodeInfo )
			{
				View taskView = getLayoutInflater().inflate( R.layout.task_view,
				                                             null );
				updateView( taskView, treeNodeInfo );
				registerForContextMenu( taskView );

				return taskView;
			}
		};

		setContentView( R.layout.tree_task_view );
		TreeViewList test = (TreeViewList) findViewById( R.id.treeView );
		test.setAdapter( adapter );

	}

	@Override
	public void onCreateContextMenu( ContextMenu menu,
	                                 View v,
	                                 ContextMenuInfo menuInfo )
	{
		super.onCreateContextMenu( menu, v, menuInfo );
		this.currentView = v;
		MenuInflater inflater = getMenuInflater();

		inflater.inflate( R.menu.task_menu, menu );
		if ( copiedTask == null )
		{
			menu.findItem( R.id.paste ).setEnabled( false );
		}
	}

	@Override
	public boolean onContextItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case R.id.rename:
				Intent editIntent = new Intent( this, TT_EditTaskActivity.class );
				Bundle editBundle = new Bundle();
				editBundle.putSerializable( "task",
				                            view2taskMap.get( currentView ) );
				editIntent.putExtras( editBundle );
				startActivityForResult( editIntent, EDITION_REQUEST );
				return true;

			case R.id.delete:
				dialogBuilder.setMessage( R.string.confirm_delete_task )
				             .setCancelable( false )
				             .setPositiveButton( R.string.delete,
				                                 new DialogInterface.OnClickListener()
				                                 {

					                                 @Override
					                                 public void onClick( DialogInterface dialog,
					                                                      int which )
					                                 {
						                                 deleteTaskAndView( currentView );
					                                 }
				                                 } )
				             .setNegativeButton( R.string.cancel,
				                                 new DialogInterface.OnClickListener()
				                                 {

					                                 @Override
					                                 public void onClick( DialogInterface dialog,
					                                                      int which )
					                                 {
						                                 // DO NOTHING
					                                 }
				                                 } );
				AlertDialog alert = dialogBuilder.create();
				alert.show();
				return true;

			case R.id.addTask:
				TT_Task newTask = new TT_Task( UUID.randomUUID().toString(),
				                               "", new Date(), TT_Task.TODO );

				Intent createIntent = new Intent( this,
				                                  TT_EditTaskActivity.class );
				Bundle createBundle = new Bundle();
				createBundle.putSerializable( "task", newTask );
				createIntent.putExtras( createBundle );
				startActivityForResult( createIntent, SUB_TASK_CREATION_REQUEST );
				return true;

			case R.id.copy:
				copiedTask = view2taskMap.get( currentView ).getCopy();
				return true;

			case R.id.paste:
				if ( copiedTask != null )
				{
					TT_Task parentTask = view2taskMap.get( currentView );
					TT_Task childTask = copiedTask.getCopy();
					TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>(
					                                                             treeManager );
					treeBuilder.addRelation( parentTask, childTask );
					childTask.setParent( parentTask );
					buildRecursively( childTask, treeBuilder );
				}
				return true;

			default:
				return super.onContextItemSelected( item );
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.add_root_task_menu, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case R.id.addRootTask:
				TT_Task newTask = new TT_Task( UUID.randomUUID().toString(),
				                               "", new Date(), TT_Task.TODO );

				Intent createIntent = new Intent( this,
				                                  TT_EditTaskActivity.class );
				Bundle createBundle = new Bundle();
				createBundle.putSerializable( "task", newTask );
				createIntent.putExtras( createBundle );
				startActivityForResult( createIntent,
				                        ROOT_TASK_CREATION_REQUEST );
				return true;
			default:
				return super.onOptionsItemSelected( item );
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if ( currentView != null )
		{
			( (TextView) currentView.findViewById( R.id.aLBLtaskNameValue ) ).setText( view2taskMap.get( currentView )
			                                                                                       .getTitle() );
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		TaskDB taskDB = new TaskDB( getApplicationContext() );
		taskDB.open();

		taskDB.resetTable();
		for ( TT_Task taskToSave : rootTasksList )
		{
			saveRecursively( taskToSave, taskDB );
		}

		taskDB.close();
	}

	@Override
	protected void onActivityResult( int requestCode,
	                                 int resultCode,
	                                 Intent data )
	{
		if ( resultCode == Activity.RESULT_OK )
		{
			TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>(
			                                                             treeManager );
			switch ( requestCode )
			{
				case EDITION_REQUEST:
					// TODO controllerDAO qui copie
					TT_Task task = (TT_Task) data.getExtras()
					                             .getSerializable( "task" );
					view2taskMap.get( currentView ).setTitle( task.getTitle() );
					break;

				case SUB_TASK_CREATION_REQUEST:
					TT_Task newSubTask = (TT_Task) data.getExtras()
					                                   .getSerializable( "task" );
					newSubTask.setParent( view2taskMap.get( currentView ) );
					treeBuilder.addRelation( view2taskMap.get( currentView ),
					                         newSubTask );
					break;

				case ROOT_TASK_CREATION_REQUEST:
					TT_Task newRootTask = (TT_Task) data.getExtras()
					                                    .getSerializable( "task" );
					rootTasksList.add( newRootTask );
					treeBuilder.sequentiallyAddNextNode( newRootTask, 0 );
					break;
			}
		}
	}

	private final static int	      SUB_TASK_CREATION_REQUEST	 = 0;
	private final static int	      ROOT_TASK_CREATION_REQUEST	= 1;
	private final static int	      EDITION_REQUEST	         = 2;

	private HashMap<View, TT_Task>	  view2taskMap;
	private View	                  currentView;
	private AlertDialog.Builder	      dialogBuilder;
	private TreeStateManager<TT_Task>	treeManager;
	private TT_Task	                  copiedTask;

	private ArrayList<TT_Task>	      rootTasksList;

	private void deleteTaskAndView( View taskView )
	{
		TT_Task taskToRemove = view2taskMap.get( taskView );
		rootTasksList.remove( taskToRemove );
		treeManager.removeNodeRecursively( taskToRemove );
		taskToRemove.setParent( null );
		view2taskMap.remove( taskView );
	}

	private void buildRecursively( TT_Task parent,
	                               TreeBuilder<TT_Task> builder )
	{
		for ( TT_Task child : parent.getChildrenTask() )
		{
			builder.addRelation( parent, child );
			buildRecursively( child, builder );
		}
	}

	private void saveRecursively( TT_Task taskToSave,
	                              TaskDB taskDB )
	{
		if ( taskToSave == null )
		{
			return;
		}

		taskDB.insertTask( taskToSave, treeManager.getNodeInfo( taskToSave )
		                                          .isExpanded() );
		for ( TT_Task child : taskToSave.getChildrenTask() )
		{
			saveRecursively( child, taskDB );
		}
	}
}
