/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.content;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TTWcontent
        extends VerticalLayout
{
    // PUBLIC
    public TTWcontent()
    {
        init();
    }

    public void setView( Component view )
    {
        removeAllComponents();
        addComponent( view );
        currentView = view;
    }

    public Component getView()
    {
        return currentView;
    }
    // PROTECTED
    // PRIVATE
    private Component currentView;

    private void init()
    {
        this.setMargin( true );
    }
}
