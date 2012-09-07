/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.window.subwindow;

import com.vaadin.ui.Layout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 *
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class EditTaskWindow
        extends Window
{
    // PUBLIC
    public EditTaskWindow( String caption )
    {
        super( caption );
        init();
    }
    // PROTECTED
    // PRIVATE
    private TextField vTXTtitleValue;
    private TextField vTXTdescriptionValue;

    private void init()
    {
        getContent().addComponent( getContentLayout() );
        getContent().setSizeUndefined();
        setModal( true );
        center();
    }

    private Layout getContentLayout()
    {
        vTXTtitleValue = new TextField( "Titre" );
        vTXTdescriptionValue = new TextField( "Description" );
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin( true );
        layout.setSpacing( true );

        layout.addComponent( vTXTtitleValue );
        layout.addComponent( vTXTdescriptionValue );
        return layout;
    }
}
