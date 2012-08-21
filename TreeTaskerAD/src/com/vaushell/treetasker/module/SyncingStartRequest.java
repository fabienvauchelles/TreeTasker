package com.vaushell.treetasker.module;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SyncingStartRequest
{
	// PUBLIC
	public SyncingStartRequest()
	{
		this( null );
	}

	public SyncingStartRequest( UserSession userSession )
	{
		this( userSession, null );
	}

	public SyncingStartRequest( UserSession userSession,
	                       String containerId )
	{
		this.userSession = userSession;
		this.containerId = containerId;
		init();
	}

	public UserSession getUserSession()
	{
		return userSession;
	}

	public void setUserSession( UserSession userSession )
	{
		this.userSession = userSession;
	}

	public String getContainerId()
	{
		return containerId;
	}

	public void setContainerId( String containerId )
	{
		this.containerId = containerId;
	}

	public ArrayList<TaskStamp> getIdList()
	{
		return idList;
	}

	public void setIdList( ArrayList<TaskStamp> idList )
	{
		this.idList = idList;
	}

	public void addId( TaskStamp stamp )
	{
		idList.add( stamp );
	}

	public void addAllIds( Collection<TaskStamp> stamps )
	{
		idList.addAll( stamps );
	}

	public ArrayList<String> getRemovedIdList()
	{
		return removedIdList;
	}

	public void setRemovedIdList( ArrayList<String> removedIdList )
	{
		this.removedIdList = removedIdList;
	}

	public void addRemovedId( String removedId )
	{
		this.removedIdList.add( removedId );
	}

	public void addAllRemovedIds( Collection<String> removedIds )
	{
		this.removedIdList.addAll( removedIds );
	}

	// PROTECTED
	// PRIVATE
	private UserSession	         userSession;
	private String	             containerId;
	private ArrayList<TaskStamp>	idList;
	private ArrayList<String>	 removedIdList;

	private void init()
	{
		this.idList = new ArrayList<TaskStamp>();
		this.removedIdList = new ArrayList<String>();
	}
}
