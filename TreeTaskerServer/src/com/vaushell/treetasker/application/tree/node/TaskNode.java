/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.tree.node;

import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import com.vaushell.treetasker.application.content.EditTaskLayout;
import com.vaushell.treetasker.model.TT_Task;

/**
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TaskNode
    extends A_NavigationNode
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// PUBLIC
	public TaskNode( TT_Task task,
	                 TreeTaskerWebApplicationController controller )
	{
		super( controller );
		this.task = task;
		init();
	}

	@Override
	public String getCaption()
	{
		return task.getTitle();
	}

	public TT_Task getTask()
	{
		return task;
	}

	@Override
	public void onEnter()
	{
		controller.getContent()
		          .setView( new EditTaskLayout( this, controller ) );
	}

	@Override
	public void onExit()
	{
		controller.getContent().getView().onExit();
		controller.getTree().refreshNodeCaption( this );
		controller.getTree().refreshNodeIcon( this );
	}

	// PROTECTED
	// PRIVATE
	private TT_Task	task;

	private void init()
	{
	}
}
