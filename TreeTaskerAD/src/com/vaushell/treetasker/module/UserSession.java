package com.vaushell.treetasker.module;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserSession
{
	// PUBLIC
	public static final int	SESSION_NOK	                       = 0;
	public static final int	SESSION_OK	                       = 1;
	public static final int	MESSAGE_REGISTERED	               = 0;
	public static final int	MESSAGE_REGISTRATION_NOT_VALIDATED	= 1;
	public static final int	MESSAGE_BAD_AUTHENTICATION	       = 2;
	public static final int	MESSAGE_NO_SESSION_AVAILABLE	   = 3;

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
		this.sessionMessage = MESSAGE_REGISTERED;
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

	public int getSessionMessage()
	{
		return sessionMessage;
	}

	public void setSessionMessage( int sessionMessage )
	{
		this.sessionMessage = sessionMessage;
	}

	@Override
	public String toString()
	{
		return "UserSession [userName=" + userName + ", userSessionID="
		       + userSessionID + ", sessionState=" + sessionState
		       + ", sessionMessage=" + sessionMessage + "]";
	}

	// PROTECTED
	// PRIVATE
	private String	userName;
	private String	userSessionID;
	private int	   sessionState;
	private int	   sessionMessage;

	private void init()
	{

	}
}
