/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.actionbar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.vaadin.dialogs.ConfirmDialog;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;

/**
 * This component is the action bar. Buttons are directly added here.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 * 
 */
public class TTWActionBar
	extends CustomComponent
{

	@AutoGenerated
	private AbsoluteLayout					mainLayout;

	@AutoGenerated
	private VerticalLayout					contentLayout;

	@AutoGenerated
	private HorizontalLayout				buttonsLayout;

	@AutoGenerated
	private Upload							vUPimport;

	@AutoGenerated
	private Button							vBTlogout;

	@AutoGenerated
	private Button							vBTpaste;

	@AutoGenerated
	private Button							vBTcopy;

	@AutoGenerated
	private Button							vBTremove;

	@AutoGenerated
	private Button							vBTrefresh;

	@AutoGenerated
	private Button							vBTaddSubtask;

	@AutoGenerated
	private Button							vBTnewTask;

	@AutoGenerated
	private Embedded						vBGactionbar;

	private transient ByteArrayOutputStream	os;

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	private static final long				serialVersionUID	= 1L;

	/**
	 * The constructor should first build the main layout, set the composition
	 * root and then do any custom initialization.
	 * 
	 * The constructor will not be automatically regenerated by the visual
	 * editor.
	 */
	public TTWActionBar(
		TreeTaskerWebApplicationController controller )
	{
		this.controller = controller;
		buildMainLayout();
		setCompositionRoot( mainLayout );

		initComponents();
	}

	@AutoGenerated
	private HorizontalLayout buildButtonsLayout() {
		// common part: create layout
		buttonsLayout = new HorizontalLayout();
		buttonsLayout.setImmediate( false );
		buttonsLayout.setWidth( "1000px" );
		buttonsLayout.setHeight( "100.0%" );
		buttonsLayout.setMargin( false );
		buttonsLayout.setSpacing( true );

		// vBTnewTask
		vBTnewTask = new Button();
		vBTnewTask.setCaption( "Ins�rer une t�che" );
		vBTnewTask.setIcon( new ThemeResource( "icons/newTask.png" ) );
		vBTnewTask.setImmediate( true );
		vBTnewTask.setDescription( "Ins�rer une t�che" );
		vBTnewTask.setWidth( "-1px" );
		vBTnewTask.setHeight( "-1px" );
		buttonsLayout.addComponent( vBTnewTask );
		buttonsLayout.setComponentAlignment( vBTnewTask, new Alignment( 20 ) );

		// vBTaddSubtask
		vBTaddSubtask = new Button();
		vBTaddSubtask.setCaption( "Ajouter une sous-t�che" );
		vBTaddSubtask.setIcon( new ThemeResource( "icons/newSubtask.png" ) );
		vBTaddSubtask.setImmediate( true );
		vBTaddSubtask.setDescription( "Ajouter une sous-t�che" );
		vBTaddSubtask.setWidth( "-1px" );
		vBTaddSubtask.setHeight( "-1px" );
		buttonsLayout.addComponent( vBTaddSubtask );
		buttonsLayout.setComponentAlignment( vBTaddSubtask, new Alignment( 20 ) );

		// vBTrefresh
		vBTrefresh = new Button();
		vBTrefresh.setCaption( "Actualiser" );
		vBTrefresh.setIcon( new ThemeResource( "icons/refresh.png" ) );
		vBTrefresh.setImmediate( true );
		vBTrefresh.setDescription( "Actualiser la liste des t�ches" );
		vBTrefresh.setWidth( "-1px" );
		vBTrefresh.setHeight( "-1px" );
		buttonsLayout.addComponent( vBTrefresh );
		buttonsLayout.setComponentAlignment( vBTrefresh, new Alignment( 20 ) );

		// vBTremove
		vBTremove = new Button();
		vBTremove.setCaption( "Supprimer" );
		vBTremove.setIcon( new ThemeResource( "icons/delete.png" ) );
		vBTremove.setImmediate( true );
		vBTremove.setDescription( "Supprimer les t�ches s�lectionn�es" );
		vBTremove.setWidth( "-1px" );
		vBTremove.setHeight( "-1px" );
		buttonsLayout.addComponent( vBTremove );
		buttonsLayout.setComponentAlignment( vBTremove, new Alignment( 20 ) );

		// vBTcopy
		vBTcopy = new Button();
		vBTcopy.setCaption( "Copier" );
		vBTcopy.setIcon( new ThemeResource( "icons/copy.png" ) );
		vBTcopy.setImmediate( true );
		vBTcopy.setDescription( "Copier les t�ches s�lectionn�es" );
		vBTcopy.setWidth( "-1px" );
		vBTcopy.setHeight( "-1px" );
		buttonsLayout.addComponent( vBTcopy );
		buttonsLayout.setComponentAlignment( vBTcopy, new Alignment( 20 ) );

		// vBTpaste
		vBTpaste = new Button();
		vBTpaste.setCaption( "Coller" );
		vBTpaste.setIcon( new ThemeResource( "icons/paste.png" ) );
		vBTpaste.setImmediate( true );
		vBTpaste.setDescription( "Coller les t�ches s�lectionn�es" );
		vBTpaste.setWidth( "-1px" );
		vBTpaste.setHeight( "-1px" );
		buttonsLayout.addComponent( vBTpaste );
		buttonsLayout.setComponentAlignment( vBTpaste, new Alignment( 20 ) );

		// vUPimport
		vUPimport = new Upload();
		vUPimport.setIcon( new ThemeResource( "icons/import.png" ) );
		vUPimport.setImmediate( true );
		vUPimport.setDescription( "Importer depuis TaskCoach" );
		vUPimport.setWidth( "-1px" );
		vUPimport.setHeight( "-1px" );
		buttonsLayout.addComponent( vUPimport );

		// vBTlogout
		vBTlogout = new Button();
		vBTlogout.setCaption( "Se d�connecter" );
		vBTlogout.setIcon( new ThemeResource( "icons/logout.png" ) );
		vBTlogout.setImmediate( true );
		vBTlogout.setDescription( "Se d�connecter" );
		vBTlogout.setWidth( "-1px" );
		vBTlogout.setHeight( "-1px" );
		buttonsLayout.addComponent( vBTlogout );
		buttonsLayout.setExpandRatio( vBTlogout, 1.0f );
		buttonsLayout.setComponentAlignment( vBTlogout, new Alignment( 6 ) );

		return buttonsLayout;
	}

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

	@AutoGenerated
	private VerticalLayout buildContentLayout() {
		// common part: create layout
		contentLayout = new VerticalLayout();
		contentLayout.setStyleName( "action-bar" );
		contentLayout.setImmediate( false );
		contentLayout.setWidth( "100.0%" );
		contentLayout.setHeight( "100.0%" );
		contentLayout.setMargin( false );

		// buttonsLayout
		buttonsLayout = buildButtonsLayout();
		contentLayout.addComponent( buttonsLayout );
		contentLayout.setComponentAlignment( buttonsLayout, new Alignment( 20 ) );

		return contentLayout;
	}

	@AutoGenerated
	private AbsoluteLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new AbsoluteLayout();
		mainLayout.setImmediate( false );
		mainLayout.setWidth( "100%" );
		mainLayout.setHeight( "70px" );
		mainLayout.setMargin( false );

		// top-level component properties
		setWidth( "100.0%" );
		setHeight( "70px" );

		// vBGactionbar
		vBGactionbar = new Embedded();
		vBGactionbar.setImmediate( false );
		vBGactionbar.setWidth( "100.0%" );
		vBGactionbar.setHeight( "100.0%" );
		vBGactionbar.setSource( new ThemeResource( "icons/toolbar-bg.png" ) );
		vBGactionbar.setType( 1 );
		vBGactionbar.setMimeType( "image/png" );
		mainLayout.addComponent( vBGactionbar, "top:0.0px;left:0.0px;" );

		// contentLayout
		contentLayout = buildContentLayout();
		mainLayout.addComponent( contentLayout, "top:0.0px;left:0.0px;" );

		return mainLayout;
	}

	private void initComponents() {
		vBTnewTask.addListener( new Button.ClickListener()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void buttonClick(
				ClickEvent event ) {
				controller.addNewTask();
			}
		} );

		vBTaddSubtask.addListener( new Button.ClickListener()
		{
			/**
										 * 
										 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void buttonClick(
				ClickEvent event ) {
				controller.addNewSubTask();
			}
		} );

		vBTremove.addListener( new Button.ClickListener()
		{
			/**
										 * 
										 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void buttonClick(
				ClickEvent event ) {
				// Popup confirmation
				ConfirmDialog.show( controller.getApplication().getMainWindow(), "Confirmation",
					"�tes-vous s�r de vouloir supprimer d�finitivement les t�ches s�lectionn�s ?", "Supprimer",
					"Annuler", new ConfirmDialog.Listener()
					{

						/**
														 * 
														 */
						private static final long	serialVersionUID	= 1L;

						@Override
						public void onClose(
							ConfirmDialog dialog ) {
							if ( dialog.isConfirmed() )
							{
								controller.deleteTasks();
							}
						}
					} );
			}
		} );

		vBTcopy.addListener( new Button.ClickListener()
		{
			/**
		 * 
		 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void buttonClick(
				ClickEvent event ) {
				controller.copyTask();
			}
		} );

		vBTpaste.addListener( new Button.ClickListener()
		{
			/**
		 * 
		 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void buttonClick(
				ClickEvent event ) {
				controller.pasteTask();
			}
		} );

		vBTrefresh.addListener( new Button.ClickListener()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void buttonClick(
				ClickEvent event ) {
				controller.refresh();
			}
		} );

		vBTlogout.addListener( new Button.ClickListener()
		{

			private static final long	serialVersionUID	= 1L;

			@Override
			public void buttonClick(
				ClickEvent event ) {
				controller.getApplication().close();
			}
		} );

		vUPimport.setReceiver( new Upload.Receiver()
		{

			private static final long	serialVersionUID	= 1L;

			@Override
			public OutputStream receiveUpload(
				String filename,
				String mimeType ) {
				os = new ByteArrayOutputStream();
				return os;
			}
		} );
		vUPimport.setButtonCaption( "Importer" );

		vUPimport.addListener( new Upload.SucceededListener()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void uploadSucceeded(
				SucceededEvent event ) {
				controller.importFromTaskCoach( new ByteArrayInputStream( os.toByteArray() ) );
			}

		} );
	}

	private final TreeTaskerWebApplicationController	controller;

}
