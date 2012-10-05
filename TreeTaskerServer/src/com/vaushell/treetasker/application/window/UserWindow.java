/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.window;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;

/**
 * This is the web application main window. It displays the login layout at
 * first, and the user view once logged in.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class UserWindow
	extends Window
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public UserWindow(
		TreeTaskerWebApplicationController controller )
	{
		super( "TreeTasker WebApplication" );
		this.controller = controller;
		init();
	}

	/**
	 * Displays the user's view.
	 */
	public void setUserView() {
		mainLayout.removeComponent( controller.getLoginLayout() );

		mainLayout.addComponent( controller.getActionBar() );

		HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
		splitPanel.setWidth( "1000px" );
		splitPanel.setHeight( "100%" );
		splitPanel.setSplitPosition( 30 );
		splitPanel.setLocked( true );
		splitPanel.setMargin( true );
		splitPanel.setStyleName( "content" );
		splitPanel.addComponent( controller.getTree() );
		splitPanel.addComponent( controller.getContent() );

		controller.refresh();

		mainLayout.addComponent( splitPanel );
		mainLayout.setExpandRatio( splitPanel, 1 );
		mainLayout.setComponentAlignment( splitPanel, Alignment.TOP_CENTER );
	}

	private void init() {
		mainLayout = (VerticalLayout) getContent();
		mainLayout.setSizeFull();
		mainLayout.setMargin( true );
		mainLayout.setSpacing( true );

		mainLayout.addComponent( controller.getHeader() );
		mainLayout.addComponent( controller.getLoginLayout() );
		mainLayout.setExpandRatio( controller.getLoginLayout(), 1 );
	}

	// PROTECTED
	// PRIVATE
	private final TreeTaskerWebApplicationController	controller;

	private VerticalLayout								mainLayout;
}