package com.vaushell.treetasker.module;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SyncingFinalResponse
{
	// PUBLIC
	public static final int	SYNC_KO	= 0;
	public static final int	SYNC_OK	= 1;

	public SyncingFinalResponse()
	{
		this( SYNC_OK );
	}

	public SyncingFinalResponse( int syncState )
	{
		this( syncState, null );
	}

	public SyncingFinalResponse( String message )
	{
		this( SYNC_KO, message );
	}

	public SyncingFinalResponse( int syncState,
	                             String message )
	{
		this.syncState = syncState;
		this.message = message;
		init();
	}

	public int getSyncState()
	{
		return syncState;
	}

	public void setSyncState( int syncState )
	{
		this.syncState = syncState;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage( String message )
	{
		this.message = message;
	}

	// PROTECTED
	// PRIVATE
	private int	   syncState;
	private String	message;

	private void init()
	{
	}
}
