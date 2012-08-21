package com.vaushell.treetasker.application.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskOpenHelper
    extends SQLiteOpenHelper
{

	public TaskOpenHelper( Context context,
	                       String name,
	                       CursorFactory factory,
	                       int version )
	{
		super( context, name, factory, version );
	}

	@Override
	public void onCreate( SQLiteDatabase db )
	{
		db.execSQL( CREATE_BDD );
	}

	@Override
	public void onUpgrade( SQLiteDatabase db,
	                       int oldVersion,
	                       int newVersion )
	{
		db.execSQL( "DROP TABLE IF EXISTS " + TASK_TABLE_NAME + ";" );
		onCreate( db );
	}

	static final String	        TASK_TABLE_NAME	    = "TT_TASK";
	static final String	        COL_ID	            = "ID";
	static final String	        COL_PARENT_ID	    = "P_ID";
	static final String	        COL_TITLE	        = "title";
	static final String	        COL_DESCRIPTION	    = "description";
	static final String	        COL_STATUS	        = "status";
	static final String	        COL_EXPANDED	    = "expanded";
	static final String	        COL_MODIF_DATE	    = "modif_date";
	static final int	        NUM_COL_ID	        = 0;
	static final int	        NUM_COL_PARENT_ID	= 1;
	static final int	        NUM_COL_TITLE	    = 2;
	static final int	        NUM_COL_DESCRIPTION	= 3;
	static final int	        NUM_COL_STATUS	    = 4;
	static final int	        NUM_COL_EXPANDED	= 5;
	static final int	        NUM_COL_MODIF_DATE	= 6;

	private static final String	CREATE_BDD	        = "CREATE TABLE "
	                                                  + TASK_TABLE_NAME
	                                                  + " ("
	                                                  + COL_ID
	                                                  + " VARCHAR(36) NOT NULL, "
	                                                  + COL_PARENT_ID
	                                                  + " VARCHAR(36), "
	                                                  + COL_TITLE
	                                                  + " VARCHAR(512) NOT NULL, "
	                                                  + COL_DESCRIPTION
	                                                  + " VARCHAR(1024) NOT NULL, "
	                                                  + COL_STATUS
	                                                  + " INTEGER, "
	                                                  + COL_EXPANDED
	                                                  + " BOOLEAN, "
	                                                  + COL_MODIF_DATE
	                                                  + " DATE); ";
}
