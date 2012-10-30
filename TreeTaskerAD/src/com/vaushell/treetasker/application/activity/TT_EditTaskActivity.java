/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.model.TT_Task;

public class TT_EditTaskActivity
	extends FragmentActivity
{
	public void onCancel(
		View view ) {
		setResult( RESULT_CANCELED );
		finish();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(
		Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.edit_task_activity_view );
		task = (TT_Task) getIntent().getExtras().getSerializable( "task" );
		( (EditText) findViewById( R.id.aTFtitleValue ) ).setText( task.getTitle() );
		( (EditText) findViewById( R.id.aTFdescriptionValue ) ).setText( task.getDescription() );
	}

	public void onSave(
		View view ) {
		task.setTitle( ( (EditText) findViewById( R.id.aTFtitleValue ) ).getText().toString().trim() );
		task.setDescription( ( (EditText) findViewById( R.id.aTFdescriptionValue ) ).getText().toString().trim() );
		Intent resultData = new Intent();
		Bundle resultBundle = new Bundle();
		resultBundle.putSerializable( "task", task );
		resultData.putExtras( resultBundle );
		setResult( RESULT_OK, resultData );
		finish();
	}

	private TT_Task	task;
}
