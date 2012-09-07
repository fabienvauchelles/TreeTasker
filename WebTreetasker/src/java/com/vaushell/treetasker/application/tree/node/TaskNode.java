/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.tree.node;

import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import com.vaushell.treetasker.model.TT_Task;

/**
 *
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TaskNode
        extends A_NavigationNode
{
    // PUBLIC
    public TaskNode( TT_Task task ,
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
    }

    @Override
    public void onExit()
    {
    }
    // PROTECTED
    // PRIVATE
    private TT_Task task;

    private void init()
    {
    }
}
