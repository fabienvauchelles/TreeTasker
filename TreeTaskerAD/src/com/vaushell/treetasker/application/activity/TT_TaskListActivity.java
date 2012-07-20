package com.vaushell.treetasker.application.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.model.TreeTaskerControllerDAO;

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

		treeManager = new InMemoryTreeStateManager<TT_Task>();
		TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>(
		                                                             treeManager );

		List<TT_Task> tasks = TreeTaskerControllerDAO.getMockTaskList1();
		for ( TT_Task task : tasks )
		{
			treeBuilder.sequentiallyAddNextNode( task, 0 );
			buildRecursively( task, treeBuilder );
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
				cbView.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
				{

					@Override
					public void onCheckedChanged( CompoundButton buttonView,
					                              boolean isChecked )
					{
						System.out.println( treeNodeInfo.getId().getTitle()
						                    + " " + isChecked );
						if ( isChecked )
						{
							treeNodeInfo.getId().setStatus( TT_Task.DONE );
						}
						else
						{
							treeNodeInfo.getId().setStatus( TT_Task.TODO );
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
				TextView textView = (TextView) taskView.findViewById( R.id.aLBLtaskNameValue );
				textView.setText( treeNodeInfo.getId().getTitle() );
				final CheckBox cbView = (CheckBox) taskView.findViewById( R.id.aCBtaskDoneValue );
				cbView.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener()
				{

					@Override
					public void onCheckedChanged( CompoundButton buttonView,
					                              boolean isChecked )
					{
						if ( isChecked )
						{
							treeNodeInfo.getId().setStatus( TT_Task.DONE );
						}
						else
						{
							treeNodeInfo.getId().setStatus( TT_Task.TODO );
						}

					}
				} );
				registerForContextMenu( taskView );
				view2taskMap.put( taskView, treeNodeInfo.getId() );

				return taskView;
			}
		};

		setContentView( R.layout.tree_task_view );
		TreeViewList test = (TreeViewList) findViewById( R.id.treeView );
		test.setAdapter( adapter );

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
	public void onCreateContextMenu( ContextMenu menu,
	                                 View v,
	                                 ContextMenuInfo menuInfo )
	{
		super.onCreateContextMenu( menu, v, menuInfo );
		this.currentView = v;
		MenuInflater inflater = getMenuInflater();

		inflater.inflate( R.menu.task_menu, menu );
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
				startActivityForResult( createIntent, CREATION_REQUEST );
				return true;

			default:
				return super.onContextItemSelected( item );
		}
	}

	@Override
	protected void onActivityResult( int requestCode,
	                                 int resultCode,
	                                 Intent data )
	{
		if ( resultCode == Activity.RESULT_OK )
		{
			switch ( requestCode )
			{
				case EDITION_REQUEST:
					// TODO controllerDAO qui copie
					TT_Task task = (TT_Task) data.getExtras()
					                             .getSerializable( "task" );
					view2taskMap.get( currentView ).setTitle( task.getTitle() );
					break;

				case CREATION_REQUEST:
					TT_Task newTask = (TT_Task) data.getExtras()
					                                .getSerializable( "task" );
					TreeBuilder<TT_Task> treeBuilder = new TreeBuilder<TT_Task>(
					                                                             treeManager );
					newTask.setParent( view2taskMap.get( currentView ) );
					treeBuilder.addRelation( view2taskMap.get( currentView ),
					                         newTask );
					break;
			}

		}
	}

	private void deleteTaskAndView( View taskView )
	{
		TT_Task taskToRemove = view2taskMap.get( taskView );
		treeManager.removeNodeRecursively( taskToRemove );
		taskToRemove.setParent( null );
		view2taskMap.remove( taskView );
	}

	private final static int	      CREATION_REQUEST	= 0;
	private final static int	      EDITION_REQUEST	= 1;

	private HashMap<View, TT_Task>	  view2taskMap;
	private View	                  currentView;
	private AlertDialog.Builder	      dialogBuilder;
	private TreeStateManager<TT_Task>	treeManager;

}
