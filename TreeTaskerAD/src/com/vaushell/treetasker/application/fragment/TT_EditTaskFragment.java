/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

	private TT_Task	                  task;
}
