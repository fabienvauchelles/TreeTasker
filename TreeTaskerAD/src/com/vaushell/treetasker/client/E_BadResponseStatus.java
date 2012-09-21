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
