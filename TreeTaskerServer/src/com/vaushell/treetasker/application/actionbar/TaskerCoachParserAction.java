/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.actionbar;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.vaushell.tools.xmldirtyparser.I_ParserAction;
import com.vaushell.tools.xmldirtyparser.XMLPath;
import com.vaushell.treetasker.model.OrderedTaskTreeController;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.net.WS_Task;

public class TaskerCoachParserAction
	implements I_ParserAction
{
	private static final int	ROOT_LVL	= 1;

	public TaskerCoachParserAction(
		OrderedTaskTreeController controller )
	{
		this.controller = controller;
	}

	@Override
	public void after() {
		// NOTHING
	}

	@Override
	public void before() {
		tasks = new LinkedList<WS_Task>();
		taskLvlMap = new HashMap<Integer, String>();
	}

	public List<WS_Task> getTasks() {
		return tasks;
	}

	@Override
	public void tagEnd(
		XMLPath path ) {

	}

	@Override
	public void tagStart(
		XMLPath path ) {
		String pathValue = path.toString();
		if ( pathValue.endsWith( "/task" ) )
		{
			currentTask = new TT_Task();
			currentTask.setID( UUID.randomUUID().toString() );
			currentTask.setLastModificationDate( new Date() );
			currentTask.setStatus( TT_Task.TODO );
			int taskLvl = StringUtils.countMatches( pathValue, "/task" );
			if ( taskLvl == ROOT_LVL )
			{
				controller.addRootTask( currentTask );
			}
			else
			{
				controller.addTask( currentTask, taskLvlMap.get( taskLvl - 1 ) );
			}
			// Store the ID of the last node added at taskLvl
			taskLvlMap.put( taskLvl, currentTask.getID() );
		}

	}

	@Override
	public void tagText(
		XMLPath path,
		String value ) {
		String pathValue = path.toString();
		if ( pathValue.contains( "@completiondate" ) )
		{
			currentTask.setStatus( TT_Task.DONE );
		}
		else if ( pathValue.contains( "description" ) )
		{
			currentTask.setDescription( value );
		}
		else if ( pathValue.contains( "@subject" ) )
		{
			currentTask.setTitle( value );
		}

	}

	private HashMap<Integer, String>		taskLvlMap;	// Store the last Id
															// of
															// TT_Task by task
															// lvl.
	private LinkedList<WS_Task>				tasks;
	// 2 is root's child, etc..)
	private final OrderedTaskTreeController	controller;
	private TT_Task							currentTask;

}
