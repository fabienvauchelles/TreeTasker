/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.activity;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import pl.polidea.treeview.AbstractTreeViewAdapter;
import pl.polidea.treeview.TreeNodeInfo;
import pl.polidea.treeview.TreeStateManager;
import pl.polidea.treeview.TreeViewList;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.model.TreeTaskerControllerDAO;
import com.vaushell.treetasker.net.UserSession;

public class TT_TaskListActivity
	extends Activity
{
	// PUBLIC
	public static final String	USERNAME					= "USERNAME";
	public static final String	SESSIONID					= "SESSIONID";

	// PRIVATE
	private final static int	CONNECTION_REQUEST			= 4;

	private final static int	SUB_TASK_CREATION_REQUEST	= 0;

	private final static int	ROOT_TASK_CREATION_REQUEST	= 1;

	private final static int	EDITION_REQUEST				= 2;
	private final static int	PREFERENCE_EDITION			= 5;

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		// Ouvre le menu si pas de tâches
		if ( TreeTaskerControllerDAO.getInstance().getRootTasksList().isEmpty() )
		{
			openOptionsMenu();
		}
	}

	@Override
	public boolean onContextItemSelected(
		MenuItem item ) {
		switch ( item.getItemId() )
		{
			case R.id.addRootTask:
			{
				TT_Task newTask = new TT_Task( UUID.randomUUID().toString(), "", "", new Date(), TT_Task.TODO );

				Intent createIntent = new Intent( this, TT_EditTaskActivity.class );
				Bundle createBundle = new Bundle();
				createBundle.putSerializable( "task", newTask );
				createIntent.putExtras( createBundle );
				startActivityForResult( createIntent, ROOT_TASK_CREATION_REQUEST );
				return true;
			}

			case R.id.synchronizeTasks:
			{
				TreeTaskerControllerDAO.getInstance().synchronizeWithDatastore(
					prefs.getString( getString( R.string.endpoint ), TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) );
				return true;
			}
			case R.id.preferences:
			{
				startActivityForResult( new Intent( this, TT_PreferenceActivity.class ), PREFERENCE_EDITION );
				return true;
			}

			case R.id.edit:
			{
				Intent editIntent = new Intent( this, TT_EditTaskActivity.class );
				Bundle editBundle = new Bundle();
				editBundle.putSerializable( "task", getCurrentTask() );
				editIntent.putExtras( editBundle );
				startActivityForResult( editIntent, EDITION_REQUEST );
				return true;
			}

			case R.id.delete:
			{
				dialogBuilder.setMessage( R.string.confirm_delete_task ).setCancelable( false )
					.setPositiveButton( R.string.delete, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(
							DialogInterface dialog,
							int which ) {
							TT_Task taskToRemove = view2taskMap.get( currentView );

							TreeTaskerControllerDAO.getInstance().deleteTask( taskToRemove );
							view2taskMap.remove( currentView );
							currentView = null;
						}
					} ).setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(
							DialogInterface dialog,
							int which ) {
							// DO NOTHING
						}
					} );
				AlertDialog alert = dialogBuilder.create();
				alert.show();
				return true;
			}

			case R.id.addTask:
			{
				TT_Task newTask = new TT_Task( UUID.randomUUID().toString(), "", "", new Date(), TT_Task.TODO );

				Intent createIntent = new Intent( this, TT_EditTaskActivity.class );
				Bundle createBundle = new Bundle();
				createBundle.putSerializable( "task", newTask );
				createIntent.putExtras( createBundle );
				startActivityForResult( createIntent, SUB_TASK_CREATION_REQUEST );
				return true;
			}

			case R.id.copy:
			{
				TreeTaskerControllerDAO.getInstance().copyTask( getCurrentTask() );
				return true;
			}

			case R.id.paste:
			{
				if ( TreeTaskerControllerDAO.getInstance().canPaste() )
				{
					TreeTaskerControllerDAO.getInstance().pasteTask( getCurrentTask() );
				}
				return true;
			}

			default:
			{
				return super.onContextItemSelected( item );
			}
		}
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(
		Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		prefs = PreferenceManager.getDefaultSharedPreferences( this );
		{
			if ( savedInstanceState != null && savedInstanceState.containsKey( USERNAME )
				&& savedInstanceState.containsKey( SESSIONID ) )
			{
				TreeTaskerControllerDAO.getInstance()
					.setUserSession(
						new UserSession( savedInstanceState.getString( USERNAME ), savedInstanceState
							.getString( SESSIONID ) ) );			loadActivity();

			}
			else
			// Sinon on donne la main à l'activité de connexion
			{
				requestAuthentication();
			}
		}

	}

	@Override
	public void onCreateContextMenu(
		ContextMenu menu,
		View v,
		ContextMenuInfo menuInfo ) {
		super.onCreateContextMenu( menu, v, menuInfo );
		if ( menu.size() == 0 )
		{
			if ( v.getId() == R.id.treeTaskView )
			{
				MenuInflater inflater = getMenuInflater();
				inflater.inflate( R.menu.add_root_task_menu, menu );
			}
			else
			{
				currentView = ( (AdapterContextMenuInfo) menuInfo ).targetView.findViewById( R.id.taskView );
				MenuInflater inflater = getMenuInflater();

				inflater.inflate( R.menu.task_menu, menu );
				if ( !TreeTaskerControllerDAO.getInstance().canPaste() )
				{
					menu.findItem( R.id.paste ).setEnabled( false );
				}
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(
		Menu menu ) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.add_root_task_menu, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(
		MenuItem item ) {
		switch ( item.getItemId() )
		{
			case R.id.addRootTask:
			{
				TT_Task newTask = new TT_Task( UUID.randomUUID().toString(), "", "", new Date(), TT_Task.TODO );

				Intent createIntent = new Intent( this, TT_EditTaskActivity.class );
				Bundle createBundle = new Bundle();
				createBundle.putSerializable( "task", newTask );
				createIntent.putExtras( createBundle );
				startActivityForResult( createIntent, ROOT_TASK_CREATION_REQUEST );
				return true;
			}
			case R.id.synchronizeTasks:
			{
				TreeTaskerControllerDAO.getInstance().synchronizeWithDatastore(
					prefs.getString( getString( R.string.endpoint ), TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) );
				return true;
			}
			case R.id.preferences:
			{
				startActivityForResult( new Intent( this, TT_PreferenceActivity.class ), PREFERENCE_EDITION );
				return true;
			}

			default:
				return super.onOptionsItemSelected( item );
		}
	}

	@Override
	protected void onActivityResult(
		int requestCode,
		int resultCode,
		Intent data ) {
		switch ( requestCode )
		{
			case CONNECTION_REQUEST:
				if ( resultCode == Activity.RESULT_OK )
				{
					TreeTaskerControllerDAO.getInstance().setUserSession(
						new UserSession( data.getStringExtra( USERNAME ), data.getStringExtra( SESSIONID ) ) );
					loadActivity();
				}
				else
				{
					finish();
				}
				break;
			case EDITION_REQUEST:
				// TODO controllerDAO qui copie
				if ( resultCode == Activity.RESULT_OK )
				{
					TT_Task task = (TT_Task) data.getExtras().getSerializable( "task" );
					TreeTaskerControllerDAO.getInstance().edit( getCurrentTask(), task.getTitle(),
						task.getDescription() );
				}
				break;

			case SUB_TASK_CREATION_REQUEST:
				if ( resultCode == Activity.RESULT_OK )
				{
					TT_Task newSubTask = (TT_Task) data.getExtras().getSerializable( "task" );
					TreeTaskerControllerDAO.getInstance().addSubTask( getCurrentTask(), newSubTask );
				}
				break;

			case ROOT_TASK_CREATION_REQUEST:
				if ( resultCode == Activity.RESULT_OK )
				{
					TT_Task newRootTask = (TT_Task) data.getExtras().getSerializable( "task" );
					TreeTaskerControllerDAO.getInstance().addRootTask( newRootTask );
				}
				break;
			default:
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		TreeTaskerControllerDAO.getInstance().save( getApplicationContext() );
	}

	// PROTECTED
	@Override
	protected void onResume() {
		super.onResume();
		if ( currentView != null )
		{
			( (TextView) currentView.findViewById( R.id.aLBLtaskNameValue ) ).setText( getCurrentTask().getTitle() );
		}
	}

	private TT_Task getCurrentTask() {
		return view2taskMap.get( currentView );
	}

	private void loadActivity() {
		view2taskMap = new HashMap<View, TT_Task>();
		dialogBuilder = new AlertDialog.Builder( this );
		TreeTaskerControllerDAO.getInstance().reset();
		TreeStateManager<TT_Task> treeManager = TreeTaskerControllerDAO.getInstance().getTreeManager();

		AbstractTreeViewAdapter<TT_Task> adapter = new AbstractTreeViewAdapter<TT_Task>( this, treeManager, 20 )
		{
			@Override
			public long getItemId(
				int position ) {
				return getTreeId( position ).getID().hashCode();
			}

			@Override
			public View getNewChildView(
				final TreeNodeInfo<TT_Task> treeNodeInfo ) {
				View taskView = getLayoutInflater().inflate( R.layout.task_view, null );
				updateView( taskView, treeNodeInfo );

				return taskView;
			}

			@Override
			public View updateView(
				View view,
				final TreeNodeInfo<TT_Task> treeNodeInfo ) {
				( (TextView) view.findViewById( R.id.aLBLtaskNameValue ) ).setText( treeNodeInfo.getId().getTitle() );
				final CheckBox cbView = (CheckBox) view.findViewById( R.id.aCBtaskDoneValue );
				cbView.setOnCheckedChangeListener( null );
				cbView.setChecked( treeNodeInfo.getId().getStatus() == TT_Task.DONE );
				cbView.setOnCheckedChangeListener( new CheckBox.OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(
						CompoundButton buttonView,
						boolean isChecked ) {
						if ( isChecked )
						{
							TreeTaskerControllerDAO.getInstance().setStatus( treeNodeInfo.getId(), TT_Task.DONE );
						}
						else
						{
							TreeTaskerControllerDAO.getInstance().setStatus( treeNodeInfo.getId(), TT_Task.TODO );
						}
					}
				} );
				view2taskMap.put( view, treeNodeInfo.getId() );
				return view;
			}
		};

		setContentView( R.layout.tree_task_view );
		TreeViewList treeView = (TreeViewList) findViewById( R.id.treeView );
		registerForContextMenu( treeView );
		registerForContextMenu( findViewById( R.id.treeTaskView ) );
		treeView.setAdapter( adapter );

		TreeTaskerControllerDAO.getInstance().load( getApplicationContext() );
	}

	private void requestAuthentication() // Afficher l'activity de connexion
	{
		Intent intent = new Intent( this, TT_ConnectionActivity.class );
		startActivityForResult( intent, CONNECTION_REQUEST );
	}

	private HashMap<View, TT_Task>	view2taskMap;

	private View					currentView;

	private AlertDialog.Builder		dialogBuilder;

	private SharedPreferences		prefs;

}
