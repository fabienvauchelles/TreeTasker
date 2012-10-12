/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
	private static final int	DB_VERSION	= 1;

	private static final String	DB_NAME		= "task.db";

	public TaskDB(
		Context context )
	{
		dbHelper = new TaskOpenHelper( context, DB_NAME, null, DB_VERSION );
		init();
	}

	public void close() {
		db.close();
	}

	public SQLiteDatabase getDB() {
		return db;
	}

	public HashMap<TT_Task, Boolean> getExpandedMap() {
		return expandedMap;
	}

	public ArrayList<TT_Task> getRootDeletedTasks() {
		return rootDeletedTasksList;
	}

	public ArrayList<TT_Task> getRootTasks() {
		return rootTasksList;
	}

	public long insertTask(
		TT_Task task,
		boolean isExpanded ) {
		ContentValues values = new ContentValues();

		values.put( TaskOpenHelper.COL_ID, task.getID() );
		if ( task.getParent() != null )
		{
			values.put( TaskOpenHelper.COL_PARENT_ID, task.getParent().getID() );
		}
		values.put( TaskOpenHelper.COL_TITLE, task.getTitle() );
		values.put( TaskOpenHelper.COL_DESCRIPTION, task.getDescription() );
		values.put( TaskOpenHelper.COL_STATUS, task.getStatus() );
		values.put( TaskOpenHelper.COL_EXPANDED, isExpanded );
		values.put( TaskOpenHelper.COL_MODIF_DATE, String.valueOf( task.getLastModificationDate().getTime() ) );
		if ( task.getPreviousTask() != null )
		{
			values.put( TaskOpenHelper.COL_PREVIOUS_ID, task.getPreviousTask().getID() );
		}

		return db.insert( TaskOpenHelper.TASK_TABLE_NAME, null, values );
	}

	public void open() {
		db = dbHelper.getWritableDatabase();
	}

	public void readTasksInfo() {
		rootTasksList.clear();
		rootDeletedTasksList.clear();
		expandedMap.clear();
		Cursor cursor = db.query( TaskOpenHelper.TASK_TABLE_NAME, null, null, null, null, null, null );

		cursor.moveToFirst();

		// Instanciation des TT_Task et metadonnées
		HashMap<String, TT_Task> idToTaskMap = new HashMap<String, TT_Task>();
		LinkedHashMap<TT_Task, String> childrenToParentIdMap = new LinkedHashMap<TT_Task, String>();
		HashMap<String, String> precedenceMap = new HashMap<String, String>();
		while ( !cursor.isAfterLast() )
		{
			TT_Task taskToLoad = new TT_Task();
			taskToLoad.setID( cursor.getString( TaskOpenHelper.NUM_COL_ID ) );
			taskToLoad.setLastModificationDate( new Date( cursor.getLong( TaskOpenHelper.NUM_COL_MODIF_DATE ) ) );
			taskToLoad.setStatus( cursor.getInt( TaskOpenHelper.NUM_COL_STATUS ) );

			taskToLoad.setTitle( cursor.getString( TaskOpenHelper.NUM_COL_TITLE ) );
			taskToLoad.setDescription( cursor.getString( TaskOpenHelper.NUM_COL_DESCRIPTION ) );
			expandedMap.put( taskToLoad, cursor.getInt( TaskOpenHelper.NUM_COL_EXPANDED ) > 0 );
			idToTaskMap.put( taskToLoad.getID(), taskToLoad );

			String previousId = cursor.getString( TaskOpenHelper.NUM_COL_PREVIOUS_ID );
			if ( previousId != null )
			{
				precedenceMap.put( taskToLoad.getID(), previousId );
			}

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

		// Affectation des précédences
		for ( String taskId : precedenceMap.keySet() )
		{
			idToTaskMap.get( taskId ).setPreviousTask( idToTaskMap.get( precedenceMap.get( taskId ) ) );
		}

		// Affectation des parents
		for ( TT_Task childTask : childrenToParentIdMap.keySet() )
		{
			childTask.setParent( idToTaskMap.get( childrenToParentIdMap.get( childTask ) ) );
		}
	}

	public void resetTable() {
		dbHelper.onUpgrade( db, 1, 1 );
	}

	private void init() {
		rootTasksList = new ArrayList<TT_Task>();
		rootDeletedTasksList = new ArrayList<TT_Task>();
		expandedMap = new HashMap<TT_Task, Boolean>();
	}

	private SQLiteDatabase				db;
	private final TaskOpenHelper		dbHelper;
	private ArrayList<TT_Task>			rootTasksList;
	private ArrayList<TT_Task>			rootDeletedTasksList;

	private HashMap<TT_Task, Boolean>	expandedMap;
}
