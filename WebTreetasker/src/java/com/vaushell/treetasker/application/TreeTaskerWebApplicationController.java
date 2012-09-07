/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application;

import com.vaadin.ui.TreeTable;
import com.vaadin.ui.Window;
import com.vaushell.treetasker.TreeTaskerWebApplication;
import com.vaushell.treetasker.application.actionbar.TTWActionBar;
import com.vaushell.treetasker.application.content.TTWcontent;
import com.vaushell.treetasker.application.header.TTWHeader;
import com.vaushell.treetasker.application.tree.TTWtree;
import com.vaushell.treetasker.application.window.UserWindow;
import com.vaushell.treetasker.application.window.subwindow.EditTaskWindow;
import com.vaushell.treetasker.model.TT_Task;

/**
 *
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TreeTaskerWebApplicationController
{
    // PUBLIC
    public TreeTaskerWebApplicationController( TreeTaskerWebApplication application )
    {
        this.application = application;
        init();
    }

    public TreeTaskerWebApplication getApplication()
    {
        return application;
    }

    public void addTask( TT_Task newTask ,
                         TT_Task parentTask ,
                         TreeTable vTTtable )
    {

        application.getMainWindow().addWindow( new EditTaskWindow( "Ajouter une tâche" ) );

    }
    // PROTECTED
    // PRIVATE
    private TreeTaskerWebApplication application;
    private UserWindow userWindow;
    private TTWActionBar actionBar;
    private TTWHeader header;
    private TTWtree tree;
    private TTWcontent content;

    private void init()
    {
    }

    public void showUserWindow()
    {
        application.setMainWindow( getUserWindow() );
    }

    private Window getUserWindow()
    {
        if ( userWindow == null )
        {
            userWindow = new UserWindow( this );
        }
        return userWindow;
    }

    public TTWActionBar getActionBar()
    {
        if ( actionBar == null )
        {
            actionBar = new TTWActionBar( this );
        }
        return actionBar;
    }

    public TTWcontent getContent()
    {
        if ( content == null )
        {
            content = new TTWcontent();
        }
        return content;
    }

    public TTWHeader getHeader()
    {
        if ( header == null )
        {
            header = new TTWHeader();
        }
        return header;
    }

    public TTWtree getTree()
    {
        if ( tree == null )
        {
            tree = new TTWtree( this );
        }
        return tree;
    }
}
