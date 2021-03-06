/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.net;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserSession
	implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID					= 1L;
	// PUBLIC
	public static final int		SESSION_NOK							= 0;
	public static final int		SESSION_OK							= 1;
	public static final int		MESSAGE_REGISTERED					= 0;
	public static final int		MESSAGE_REGISTRATION_NOT_VALIDATED	= 1;
	public static final int		MESSAGE_BAD_AUTHENTICATION			= 2;
	public static final int		MESSAGE_NO_SESSION_AVAILABLE		= 3;
	public static final int		MESSAGE_USER_ALREADY_EXISTS			= 4;
	public static final int		MESSAGE_UNREACHABLE_SERVER			= 5;

	public UserSession()
	{
		sessionState = SESSION_NOK;
		init();
	}

	public UserSession(
		String userName,
		String userSessionID )
	{
		this.userName = userName;
		this.userSessionID = userSessionID;
		sessionState = SESSION_OK;
		sessionMessage = MESSAGE_REGISTERED;
		init();
	}

	public int getSessionMessage() {
		return sessionMessage;
	}

	public int getSessionState() {
		return sessionState;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserSessionID() {
		return userSessionID;
	}

	public boolean isValid() {
		return sessionState == SESSION_OK;
	}

	public void setSessionMessage(
		int sessionMessage ) {
		this.sessionMessage = sessionMessage;
	}

	public void setSessionState(
		int sessionState ) {
		this.sessionState = sessionState;
	}

	public void setUserName(
		String userName ) {
		this.userName = userName;
	}

	public void setUserSessionID(
		String userSessionID ) {
		this.userSessionID = userSessionID;
	}

	private void init() {

	}

	// PROTECTED
	// PRIVATE
	private String	userName;
	private String	userSessionID;
	private int		sessionState;

	private int		sessionMessage;
}
