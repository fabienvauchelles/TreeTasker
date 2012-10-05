/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.content.layout;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import com.vaushell.treetasker.application.tree.TaskNode;
import com.vaushell.treetasker.model.TT_Task;

/**
 * This layout is related to one task. It has an editable title and description.
 * The background color depends on the task status.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class EditTaskLayout
	extends VerticalLayout
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public EditTaskLayout(
		TaskNode taskNode,
		TreeTaskerWebApplicationController controller )
	{
		this.controller = controller;
		this.taskNode = taskNode;
		init();
	}

	public void onExit() {
		saveAll();
	}

	public void refreshStyle() {
		switch ( taskNode.getTask().getStatus() )
		{
			case TT_Task.DONE:
				getTitleLayout().setStyleName( "title-done-layout" );
				getDescriptionLayout().setStyleName( "description-done-layout" );
				break;
			case TT_Task.TODO:
				getTitleLayout().setStyleName( "title-todo-layout" );
				getDescriptionLayout().setStyleName( "description-todo-layout" );
				break;
		}
	}

	private VerticalLayout getDescriptionLayout() {
		if ( descriptionLayout == null )
		{
			descriptionLayout = new VerticalLayout();
			descriptionLayout.setMargin( true );
			descriptionLayout.setHeight( "100%" );

			vTAtaskDescriptionValue = new TextArea();
			vTAtaskDescriptionValue.setValue( taskNode.getTask().getDescription() );
			vTAtaskDescriptionValue.setReadOnly( true );
			vTAtaskDescriptionValue.setRows( 20 );
			vTAtaskDescriptionValue.setWidth( "100%" );
			vTAtaskDescriptionValue.setNullRepresentation( "Ajouter une description..." );

			descriptionLayout.addComponent( vTAtaskDescriptionValue );
		}
		return descriptionLayout;
	}

	private VerticalLayout getTitleLayout() {
		if ( titleLayout == null )
		{
			titleLayout = new VerticalLayout();
			titleLayout.setMargin( true );

			refreshStyle();

			vTFtaskTitleValue = new TextField();
			vTFtaskTitleValue.setWidth( "100%" );
			vTFtaskTitleValue.setValue( taskNode.getTask().getTitle() );
			vTFtaskTitleValue.setReadOnly( true );

			titleLayout.addComponent( vTFtaskTitleValue );
		}
		return titleLayout;
	}

	private void init() {
		setSizeFull();

		addComponent( getTitleLayout() );
		addComponent( getDescriptionLayout() );
		setExpandRatio( getDescriptionLayout(), 1 );

		getTitleLayout().addListener( new LayoutClickListener()
		{
			/**
			 * 
			 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void layoutClick(
				LayoutClickEvent event ) {
				if ( event.getChildComponent() == vTFtaskTitleValue )
				{
					if ( vTFtaskTitleValue.isReadOnly() )
					{
						saveAll();
						vTFtaskTitleValue.setReadOnly( false );
					}
				}
				else
				{
					saveAll();
				}
			}
		} );

		getDescriptionLayout().addListener( new LayoutClickListener()
		{
			/**
			 * 
			 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void layoutClick(
				LayoutClickEvent event ) {
				if ( event.getChildComponent() == vTAtaskDescriptionValue )
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

	private void saveAll() {
		vTFtaskTitleValue.setReadOnly( true );
		vTAtaskDescriptionValue.setReadOnly( true );
		controller.updateTaskContent( taskNode.getTask(), (String) vTFtaskTitleValue.getValue(),
			(String) vTAtaskDescriptionValue.getValue() );
		controller.getTree().refreshNodeCaption( taskNode );
	}

	// PROTECTED
	// PRIVATE
	private final TaskNode								taskNode;
	private final TreeTaskerWebApplicationController	controller;
	private VerticalLayout								titleLayout;
	private VerticalLayout								descriptionLayout;
	private TextField									vTFtaskTitleValue;
	private TextArea									vTAtaskDescriptionValue;
}
