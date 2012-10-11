/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.client;

public class E_BadResponseStatus
	extends Exception
{
	/**
     * 
     */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public E_BadResponseStatus(
		int statusCode,
		String message )
	{
		super( statusCode + " ERROR: " + message );
		this.statusCode = statusCode;
		init();
	}

	public int getStatusCode() {
		return statusCode;
	}

	private void init() {
	}

	// PROTECTED
	// PRIVATE
	private final int	statusCode;
}
