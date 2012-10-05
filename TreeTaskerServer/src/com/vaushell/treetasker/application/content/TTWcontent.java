/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.content;

import com.vaadin.ui.VerticalLayout;
import com.vaushell.treetasker.application.content.layout.EditTaskLayout;

/**
 * This layout is where content can be put. The layout is on the right side of
 * the tree of tasks.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TTWcontent
	extends VerticalLayout
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public TTWcontent()
	{
		init();
	}

	public EditTaskLayout getView() {
		return currentView;
	}

	public void setView(
		EditTaskLayout view ) {
		removeAllComponents();
		if ( view != null )
		{
			addComponent( view );
		}
		currentView = view;
	}

	private void init() {
		setSizeFull();
	}

	// PROTECTED
	// PRIVATE
	private EditTaskLayout	currentView;
}
