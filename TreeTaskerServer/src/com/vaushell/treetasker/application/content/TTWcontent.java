/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
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
