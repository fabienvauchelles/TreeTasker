package com.vaushell.treetasker.application.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.model.TT_Task;

public class TT_EditTaskFragment
    extends Fragment
{	

	@Override
	public View onCreateView( LayoutInflater inflater,
	                          ViewGroup container,
	                          Bundle savedInstanceState )
	{
		return inflater.inflate( R.layout.edit_task_fragment_view, container, false );
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	public TT_Task getTask()
	{
		return task;
	}

	public void setTask( TT_Task task )
	{
		this.task = task;
		( (EditText) getView().findViewById( R.id.aTFtitleValue ) ).setText( task.getTitle() );
	}

	private TT_Task	                  task;
}
