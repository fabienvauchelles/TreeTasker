/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.content;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import com.vaushell.treetasker.application.tree.node.TaskNode;

/**
 *
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class EditTaskLayout
        extends VerticalLayout
{
    // PUBLIC
    public EditTaskLayout( TaskNode taskNode ,
                           TreeTaskerWebApplicationController controller )
    {
        this.controller = controller;
        this.taskNode = taskNode;
        init();
    }
    
    public void onExit()
    {
        saveAll();
    }
    // PROTECTED
    // PRIVATE
    private TaskNode taskNode;
    private TreeTaskerWebApplicationController controller;
    private TextField vTFtaskTitleValue;
    private TextArea vTAtaskDescriptionValue;
    
    private void init()
    {
        setSizeFull();
        setMargin( true );
        setSpacing( true );
        
        vTFtaskTitleValue = new TextField( "Titre :" );
        vTFtaskTitleValue.setWidth( "100%" );
        vTFtaskTitleValue.setValue( taskNode.getTask().getTitle() );
        vTFtaskTitleValue.setReadOnly( true );
        
        vTAtaskDescriptionValue = new TextArea( "Description" );
        vTAtaskDescriptionValue.setValue( taskNode.getTask().getDescription() );
        vTAtaskDescriptionValue.setReadOnly( true );
        vTAtaskDescriptionValue.setRows( 20 );
        vTAtaskDescriptionValue.setWidth( "100%" );
        vTAtaskDescriptionValue.setNullRepresentation( "" );
        
        Button okButton = new Button( "Enregistrer" ,
                                      new Button.ClickListener()
        {
            public void buttonClick( ClickEvent event )
            {
                saveAll();
            }
        } );
        
        addComponent( vTFtaskTitleValue );
        addComponent( vTAtaskDescriptionValue );
        addComponent( okButton );
        
        setExpandRatio( okButton ,
                        1 );
        setComponentAlignment( okButton ,
                               Alignment.TOP_RIGHT );
        
        addListener( new LayoutClickListener()
        {
            public void layoutClick( LayoutClickEvent event )
            {
                if ( event.getChildComponent() == vTFtaskTitleValue )
                {
                    if ( vTFtaskTitleValue.isReadOnly() )
                    {
                        saveAll();
                        vTFtaskTitleValue.setReadOnly( false );
                    }
                }
                else if ( event.getChildComponent() == vTAtaskDescriptionValue )
                {
                    
                    if ( vTAtaskDescriptionValue.isReadOnly() )
                    {
                        saveAll();
                        vTAtaskDescriptionValue.setReadOnly( false );
                    }
                }
                else
                {
                    saveAll();
                }
            }
        } );
        
        
    }
    
    private void saveAll()
    {
        vTFtaskTitleValue.setReadOnly( true );
        vTAtaskDescriptionValue.setReadOnly( true );
        taskNode.getTask().setTitle( (String) vTFtaskTitleValue.getValue() );
        taskNode.getTask().setDescription( (String) vTAtaskDescriptionValue.getValue() );
        controller.getTree().refreshNodeCaption( taskNode );
    }
}
