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
package com.vaushell.treetasker.application.tree;

import java.io.Serializable;

import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;

/**
 * This is an abstract node used by the navigation tree. Typically,
 * <code>onEnter</code> is called when the node is selected and onExit is called
 * when the node is exited.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public abstract class A_NavigationNode
	implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public A_NavigationNode(
		TreeTaskerWebApplicationController controller )
	{
		this.controller = controller;
	}

	/**
	 * 
	 * @return the caption value to display on the tree
	 */
	public abstract String getCaption();

	public TreeTaskerWebApplicationController getController() {
		return controller;
	}

	/**
	 * Called when the node is selected
	 */
	public abstract void onEnter();

	/**
	 * Called when the node is exited
	 */
	public abstract void onExit();

	// PROTECTED
	protected TreeTaskerWebApplicationController	controller;
}
