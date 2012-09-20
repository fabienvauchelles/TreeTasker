/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.window;

import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;

/**
 *
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class UserWindow
        extends Window
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// PUBLIC
    public UserWindow( TreeTaskerWebApplicationController controller )
    {
        super( "TreeTasker WebApplication" );
        this.controller = controller;
        init();
    }
    // PROTECTED
    // PRIVATE
    private TreeTaskerWebApplicationController controller;
    private VerticalLayout mainLayout;

    private void init()
    {
        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setMargin( true );
        mainLayout.setSpacing( true );

        mainLayout.addComponent( controller.getHeader() );
        setContent( mainLayout );
    }

	public void setUserView( String name )
    {
        mainLayout.addComponent( controller.getActionBar() );

        HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
        splitPanel.setSizeFull();
        splitPanel.addComponent( controller.getTree() );
        splitPanel.addComponent( controller.getContent() );

        controller.refresh();
        
        mainLayout.addComponent( splitPanel );
        mainLayout.setExpandRatio( splitPanel ,
                                   1 );	    
    }
}