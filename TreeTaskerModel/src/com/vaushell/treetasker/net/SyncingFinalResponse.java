package com.vaushell.treetasker.net;

import java.util.ArrayList;
import java.util.List;

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

	public SyncingFinalResponse(
		int syncState )
	{
		this( syncState, null );
	}

	public SyncingFinalResponse(
		int syncState,
		String message )
	{
		this.syncState = syncState;
		this.message = message;
		init();
	}

	public SyncingFinalResponse(
		String message )
	{
		this( SYNC_KO, message );
	}

	public String getMessage() {
		return message;
	}

	public int getSyncState() {
		return syncState;
	}

	public List<WS_Task> getUpToDateTasks() {
		return upToDateTasks;
	}

	public void setMessage(
		String message ) {
		this.message = message;
	}

	public void setSyncState(
		int syncState ) {
		this.syncState = syncState;
	}

	public void setUpToDateTasks(
		List<WS_Task> upToDateTasks ) {
		this.upToDateTasks = upToDateTasks;
	}

	private void init() {
		upToDateTasks = new ArrayList<WS_Task>();
	}

	// PROTECTED
	// PRIVATE
	private int				syncState;
	private String			message;

	private List<WS_Task>	upToDateTasks;
}
