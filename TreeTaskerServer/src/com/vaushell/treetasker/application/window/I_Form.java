/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.window;

/**
 * Interface for a form with ok and cancel buttons.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 * 
 */
public interface I_Form
{

	/**
	 * Called when cancel button is pressed.
	 */
	public void cancel();

	/**
	 * Called when ok button is pressed.
	 */
	public void ok();
}
