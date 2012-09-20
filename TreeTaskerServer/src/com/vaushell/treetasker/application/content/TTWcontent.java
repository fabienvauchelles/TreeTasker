/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.content;

import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TTWcontent
        extends VerticalLayout
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// PUBLIC
    public TTWcontent()
    {
        init();
    }

    public void setView( EditTaskLayout view )
    {
        removeAllComponents();
        addComponent( view );
        currentView = view;
    }

    public EditTaskLayout getView()
    {
        return currentView;
    }
    // PROTECTED
    // PRIVATE
    private EditTaskLayout currentView;

    private void init()
    {
        this.setMargin( true );
        setSizeFull();
    }
}
