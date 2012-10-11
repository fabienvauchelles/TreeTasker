/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.tree;

import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import com.vaushell.treetasker.application.content.layout.EditTaskLayout;
import com.vaushell.treetasker.model.TT_Task;

/**
 * This is an implementation of A_NavigationNode for a task.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TaskNode
	extends A_NavigationNode
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public TaskNode(
		TT_Task task,
		TreeTaskerWebApplicationController controller )
	{
		super( controller );
		this.task = task;
		init();
	}

	@Override
	public String getCaption() {
		return task.getTitle();
	}

	public TT_Task getTask() {
		return task;
	}

	@Override
	public void onEnter() {
		controller.getContent().setView( new EditTaskLayout( this, controller ) );
	}

	@Override
	public void onExit() {
		if ( controller.getContent().getView() != null )
		{
			controller.getContent().getView().onExit();
		}
		controller.getTree().refreshNodeCaption( this );
		controller.getTree().refreshNodeIcon( this );
	}

	private void init() {
	}

	// PROTECTED
	// PRIVATE
	private final TT_Task	task;
}
