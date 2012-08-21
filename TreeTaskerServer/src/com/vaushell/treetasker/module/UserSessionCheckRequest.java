package com.vaushell.treetasker.module;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserSessionCheckRequest
{
	// PUBLIC
	public UserSessionCheckRequest()
	{
		this( null, null );
	}

	public UserSessionCheckRequest( String sessionId,
	                                String login )
	{
		this.sessionId = sessionId;
		this.login = login;
		init();
	}

	public String getSessionId()
	{
		return sessionId;
	}

	public void setSessionId( String sessionId )
	{
		this.sessionId = sessionId;
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin( String login )
	{
		this.login = login;
	}

	// PROTECTED
	// PRIVATE
	private String	sessionId;
	private String	login;

	private void init()
	{

	}
}
