/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.storage;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

	public void clearDeletedTasks() {
		deletedTasksSet.clear();

		db.delete( TaskOpenHelper.TASK_TABLE_NAME, TaskOpenHelper.COL_STATUS + "=" + TT_Task.DELETED, null );
	}

	public void close() {
		db.close();
	}

	public void deleteTask(
		String taskId ) {
		db.execSQL( "update " + TaskOpenHelper.TASK_TABLE_NAME + " set " + TaskOpenHelper.COL_STATUS + "="
			+ TT_Task.DELETED + " where " + TaskOpenHelper.COL_ID + "=" + taskId );
	}

	public void deleteTasks(
		Collection<String> taskIds ) {
		db.beginTransaction();
		for ( String taskId : taskIds )
		{
			db.execSQL( "update " + TaskOpenHelper.TASK_TABLE_NAME + " set " + TaskOpenHelper.COL_STATUS + "="
				+ TT_Task.DELETED + " where " + TaskOpenHelper.COL_ID + "=" + taskId );
		}
		db.endTransaction();
	}

	public int eraseTask(
		String taskId ) {
		return db.delete( TaskOpenHelper.TASK_TABLE_NAME, TaskOpenHelper.COL_ID + "=" + taskId, null );
	}

	public SQLiteDatabase getDB() {
		return db;
	}

	public Set<WS_Task> getDeletedTasks() {
		return deletedTasksSet;
	}

	public HashSet<String> getExpandedSet() {
		return expandedSet;
	}

	public Set<WS_Task> getTasks() {
		return taskSet;
	}

	public long insertOrUpdateTask(
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

		if ( taskExists( task.getID() ) )
		{
			return db.replace( TaskOpenHelper.TASK_TABLE_NAME, null, values );
		}
		else
		{
			return db.insert( TaskOpenHelper.TASK_TABLE_NAME, null, values );
		}
	}

	public long insertOrUpdateTask(
		WS_Task task,
		boolean isExpanded ) {
		ContentValues values = new ContentValues();

		values.put( TaskOpenHelper.COL_PARENT_ID, task.getParentId() );
		values.put( TaskOpenHelper.COL_PREVIOUS_ID, task.getPreviousId() );
		values.put( TaskOpenHelper.COL_ID, task.getId() );
		values.put( TaskOpenHelper.COL_TITLE, task.getTitle() );
		values.put( TaskOpenHelper.COL_DESCRIPTION, task.getDescription() );
		values.put( TaskOpenHelper.COL_STATUS, task.getStatus() );
		values.put( TaskOpenHelper.COL_EXPANDED, isExpanded );
		values.put( TaskOpenHelper.COL_MODIF_DATE, String.valueOf( task.getLastModificationDate().getTime() ) );

		if ( taskExists( task.getId() ) )
		{
			return db.replace( TaskOpenHelper.TASK_TABLE_NAME, null, values );
		}
		else
		{
			return db.insert( TaskOpenHelper.TASK_TABLE_NAME, null, values );
		}
	}

	public void insertOrUpdateTasks(
		Collection<TT_Task> tasks,
		Set<String> expandedSet ) {
		db.beginTransaction();
		for ( TT_Task task : tasks )
		{
			insertOrUpdateTask( task, expandedSet.contains( task.getID() ) );
		}
		db.endTransaction();
	}

	public void open() {
		db = dbHelper.getWritableDatabase();
	}

	public void readTasksInfo() {
		taskSet.clear();
		deletedTasksSet.clear();
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
				deletedTasksSet.add( taskToLoad );
			}
			else
			{
				taskSet.add( taskToLoad );
			}

			if ( cursor.getInt( TaskOpenHelper.NUM_COL_EXPANDED ) > 0 )
			{
				expandedSet.add( taskToLoad.getId() );
			}

			cursor.moveToNext();
		}

		cursor.close();
	}

	public void reinit(
		Collection<WS_Task> tasks,
		Set<String> expandedSet ) {
		resetTable();

		// db.beginTransaction();
		for ( WS_Task task : tasks )
		{
			insertOrUpdateTask( task, expandedSet.contains( task.getId() ) );
		}
		// db.endTransaction();
	}

	public void resetTable() {
		dbHelper.onUpgrade( db, 1, 1 );
	}

	public boolean taskExists(
		String taskId ) {
		Cursor cursor = db.rawQuery( "select 1 from " + TaskOpenHelper.TASK_TABLE_NAME + " where "
			+ TaskOpenHelper.COL_ID + "='" + taskId + "'", null );
		boolean exists = cursor.getCount() > 0;
		cursor.close();
		return exists;
	}

	private void init() {
		taskSet = new HashSet<WS_Task>();
		deletedTasksSet = new HashSet<WS_Task>();
		expandedSet = new HashSet<String>();
	}

	private SQLiteDatabase			db;
	private final TaskOpenHelper	dbHelper;
	private Set<WS_Task>			taskSet;
	private Set<WS_Task>			deletedTasksSet;

	private HashSet<String>			expandedSet;
}
