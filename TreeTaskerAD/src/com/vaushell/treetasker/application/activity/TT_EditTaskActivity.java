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
	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.edit_task_activity_view );
		task = (TT_Task) getIntent().getExtras().getSerializable( "task" );
		( (EditText) findViewById( R.id.aTFtitleValue ) ).setText( task.getTitle() );
	}

	public void onSave( View view )
	{
		task.setTitle( ( (EditText) findViewById( R.id.aTFtitleValue ) ).getText()
		                                                                .toString() );
		Intent resultData = new Intent();
		Bundle resultBundle = new Bundle();
		resultBundle.putSerializable( "task", task );
		resultData.putExtras( resultBundle );
		setResult( RESULT_OK, resultData );
		finish();
	}

	public void onCancel( View view )
	{
		setResult( RESULT_CANCELED );
		finish();
	}

	private TT_Task	task;
}
