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
