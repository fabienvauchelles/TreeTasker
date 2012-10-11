/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.dao;

public class E_InvalidEntityHandling
    extends RuntimeException
{
	/**
     * 
     */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public E_InvalidEntityHandling()
	{
		init();
	}

	public E_InvalidEntityHandling( String arg0 )
	{
		super( arg0 );
	}

	public E_InvalidEntityHandling( Throwable arg0 )
	{
		super( arg0 );
	}

	public E_InvalidEntityHandling( String arg0,
	                                Throwable arg1 )
	{
		super( arg0, arg1 );
	}

	// PROTECTED
	// PRIVATE

	private void init()
	{

	}
}
