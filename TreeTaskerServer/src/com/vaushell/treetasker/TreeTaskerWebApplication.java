/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/*
 * MyApplication.java
 *
 * Created on 6 septembre 2012, 10:24
 */
package com.vaushell.treetasker;

import com.vaadin.Application;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;

/**
 * 
 * @author Fred
 * @version
 */
public class TreeTaskerWebApplication
	extends Application
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Override
	public void init() {
		setTheme( "treetasker" );
		controller = new TreeTaskerWebApplicationController( this );

		controller.showLoginWindow();

	}

	private TreeTaskerWebApplicationController	controller;
}
