/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.net;

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
