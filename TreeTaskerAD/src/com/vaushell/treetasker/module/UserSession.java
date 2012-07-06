package com.vaushell.treetasker.module;

public class UserSession
{
	// PUBLIC
	public static final int SESSION_NOK = 0;
	public static final int SESSION_OK = 1;
	
	public UserSession()
	{
		this.sessionState = SESSION_NOK;
		init();
	}

	public UserSession( String userName,
	                    String userSessionID )
	{
		this.userName = userName;
		this.userSessionID = userSessionID;
		this.sessionState = SESSION_OK;
		init();
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName( String userName )
	{
		this.userName = userName;
	}

	public String getUserSessionID()
	{
		return userSessionID;
	}

	public void setUserSessionID( String userSessionID )
	{
		this.userSessionID = userSessionID;
	}
	
	public int getSessionState()
    {
    	return sessionState;
    }

	public void setSessionState( int sessionState )
    {
    	this.sessionState = sessionState;
    }

	// PROTECTED
	// PRIVATE
	private String	userName;
	private String	userSessionID;
	private int sessionState;

	private void init()
	{

	}
}
