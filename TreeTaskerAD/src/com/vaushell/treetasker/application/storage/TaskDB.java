package com.vaushell.treetasker.application.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vaushell.treetasker.model.TT_Task;

public class TaskDB
{
	public TaskDB( Context context )
	{
		this.dbHelper = new TaskOpenHelper( context, DB_NAME, null, DB_VERSION );
		init();
	}

	public void open()
	{
		this.db = dbHelper.getWritableDatabase();
	}

	public void close()
	{
		this.db.close();
	}

	public SQLiteDatabase getDB()
	{
		return db;
	}

	public long insertTask( TT_Task task,
	                        boolean isExpanded )
	{
		ContentValues values = new ContentValues();

		values.put( TaskOpenHelper.COL_ID, task.getID() );
		if ( task.getParent() != null )
			values.put( TaskOpenHelper.COL_PARENT_ID, task.getParent().getID() );
		values.put( TaskOpenHelper.COL_TITLE, task.getTitle() );
		values.put( TaskOpenHelper.COL_STATUS, task.getStatus() );
		System.out.println( task.getTitle() + " status is " + task.getStatus() );
		values.put( TaskOpenHelper.COL_EXPANDED, isExpanded );
		values.put( TaskOpenHelper.COL_MODIF_DATE,
		            String.valueOf( task.getLastModificationDate().getTime() ) );

		return db.insert( TaskOpenHelper.TASK_TABLE_NAME, null, values );
	}

	public void resetTable()
	{
		dbHelper.onUpgrade( db, 1, 1 );
	}

	public void readTasksInfo()
	{
		this.rootTasksList.clear();
		this.rootDeletedTasksList.clear();
		this.expandedMap.clear();
		Cursor cursor = db.query( TaskOpenHelper.TASK_TABLE_NAME,
		                          null,
		                          null,
		                          null,
		                          null,
		                          null,
		                          null );

		cursor.moveToFirst();

		// Instanciation des TT_Task et metadonnées
		HashMap<String, TT_Task> idToTaskMap = new HashMap<String, TT_Task>();
		LinkedHashMap<TT_Task, String> childrenToParentIdMap = new LinkedHashMap<TT_Task, String>();
		while ( !cursor.isAfterLast() )
		{
			TT_Task taskToLoad = new TT_Task();
			taskToLoad.setID( cursor.getString( TaskOpenHelper.NUM_COL_ID ) );
			taskToLoad.setLastModificationDate( new Date(
			                                              cursor.getLong( TaskOpenHelper.NUM_COL_MODIF_DATE ) ) );
			taskToLoad.setStatus( cursor.getInt( TaskOpenHelper.NUM_COL_STATUS ) );

			taskToLoad.setTitle( cursor.getString( TaskOpenHelper.NUM_COL_TITLE ) );
			System.out.println( taskToLoad.getTitle() + " status is "
			                    + taskToLoad.getStatus() );
			expandedMap.put( taskToLoad,
			                 cursor.getInt( TaskOpenHelper.NUM_COL_EXPANDED ) > 0 );
			idToTaskMap.put( taskToLoad.getID(), taskToLoad );

			String parentId = cursor.getString( TaskOpenHelper.NUM_COL_PARENT_ID );
			if ( parentId != null )
			{
				childrenToParentIdMap.put( taskToLoad, parentId );
			}
			else if ( taskToLoad.getStatus() == TT_Task.DELETED )

			{
				rootDeletedTasksList.add( taskToLoad );
			}
			else
			{
				rootTasksList.add( taskToLoad );
			}
			cursor.moveToNext();
		}

		cursor.close();

		for ( TT_Task childTask : childrenToParentIdMap.keySet() )
		{
			childTask.setParent( idToTaskMap.get( childrenToParentIdMap.get( childTask ) ) );
		}
	}

	public ArrayList<TT_Task> getRootTasks()
	{
		return rootTasksList;
	}

	public ArrayList<TT_Task> getRootDeletedTasks()
	{
		return rootDeletedTasksList;
	}

	public HashMap<TT_Task, Boolean> getExpandedMap()
	{
		return expandedMap;
	}

	private static final int	      DB_VERSION	= 1;
	private static final String	      DB_NAME	 = "task.db";

	private SQLiteDatabase	          db;
	private TaskOpenHelper	          dbHelper;
	private ArrayList<TT_Task>	      rootTasksList;
	private ArrayList<TT_Task>	      rootDeletedTasksList;
	private HashMap<TT_Task, Boolean>	expandedMap;

	private void init()
	{
		this.rootTasksList = new ArrayList<TT_Task>();
		this.rootDeletedTasksList = new ArrayList<TT_Task>();
		this.expandedMap = new HashMap<TT_Task, Boolean>();
	}
}
