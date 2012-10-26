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
import java.util.HashSet;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.net.WS_Task;

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

	public ArrayList<WS_Task> getDeletedTasks() {
		return deletedTasksList;
	}

	public HashSet<String> getExpandedSet() {
		return expandedSet;
	}

	public ArrayList<WS_Task> getTasks() {
		return tasksList;
	}

	public long insertTask(
		TT_Task task,
		boolean isExpanded ) {
		ContentValues values = new ContentValues();

		if ( task.getParent() != null )
		{
			values.put( TaskOpenHelper.COL_PARENT_ID, task.getParent().getID() );
		}
		if ( task.getPreviousTask() != null )
		{
			values.put( TaskOpenHelper.COL_PREVIOUS_ID, task.getPreviousTask().getID() );
		}
		values.put( TaskOpenHelper.COL_ID, task.getID() );
		values.put( TaskOpenHelper.COL_TITLE, task.getTitle() );
		values.put( TaskOpenHelper.COL_DESCRIPTION, task.getDescription() );
		values.put( TaskOpenHelper.COL_STATUS, task.getStatus() );
		values.put( TaskOpenHelper.COL_EXPANDED, isExpanded );
		values.put( TaskOpenHelper.COL_MODIF_DATE, String.valueOf( task.getLastModificationDate().getTime() ) );

		return db.insert( TaskOpenHelper.TASK_TABLE_NAME, null, values );
	}

	public void open() {
		db = dbHelper.getWritableDatabase();
	}

	public void readTasksInfo() {
		tasksList.clear();
		deletedTasksList.clear();
		expandedSet.clear();

		Cursor cursor = db.query( TaskOpenHelper.TASK_TABLE_NAME, null, null, null, null, null, null );
		cursor.moveToFirst();

		// Instanciation des WS_Task et metadonnées
		while ( !cursor.isAfterLast() )
		{
			WS_Task taskToLoad = new WS_Task();

			taskToLoad.setPreviousId( cursor.getString( TaskOpenHelper.NUM_COL_PREVIOUS_ID ) );
			taskToLoad.setParentId( cursor.getString( TaskOpenHelper.NUM_COL_PARENT_ID ) );
			taskToLoad.setId( cursor.getString( TaskOpenHelper.NUM_COL_ID ) );
			taskToLoad.setLastModificationDate( new Date( cursor.getLong( TaskOpenHelper.NUM_COL_MODIF_DATE ) ) );
			taskToLoad.setStatus( cursor.getInt( TaskOpenHelper.NUM_COL_STATUS ) );
			taskToLoad.setTitle( cursor.getString( TaskOpenHelper.NUM_COL_TITLE ) );
			taskToLoad.setDescription( cursor.getString( TaskOpenHelper.NUM_COL_DESCRIPTION ) );

			if ( taskToLoad.getStatus() == TT_Task.DELETED )
			{
				deletedTasksList.add( taskToLoad );
			}
			else
			{
				tasksList.add( taskToLoad );
			}

			if ( cursor.getInt( TaskOpenHelper.NUM_COL_EXPANDED ) > 0 )
			{
				expandedSet.add( taskToLoad.getId() );
			}

			cursor.moveToNext();
		}

		cursor.close();
	}

	public void resetTable() {
		dbHelper.onUpgrade( db, 1, 1 );
	}

	private void init() {
		tasksList = new ArrayList<WS_Task>();
		deletedTasksList = new ArrayList<WS_Task>();
		expandedSet = new HashSet<String>();
	}

	private SQLiteDatabase			db;
	private final TaskOpenHelper	dbHelper;
	private ArrayList<WS_Task>		tasksList;
	private ArrayList<WS_Task>		deletedTasksList;

	private HashSet<String>			expandedSet;
}
