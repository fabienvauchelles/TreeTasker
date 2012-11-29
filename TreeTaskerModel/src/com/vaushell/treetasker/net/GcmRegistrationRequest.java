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
public class GcmRegistrationRequest
{
	// PUBLIC
	public GcmRegistrationRequest()
	{
		this( null, null );
	}

	public GcmRegistrationRequest(
		String login,
		String gcmId )
	{
		this.login = login;
		this.gcmId = gcmId;
		init();
	}

	public String getGcmId() {
		return gcmId;
	}

	public String getLogin() {
		return login;
	}

	public void setGcmId(
		String gcmId ) {
		this.gcmId = gcmId;
	}

	public void setLogin(
		String login ) {
		this.login = login;
	}

	private void init() {

	}

	// PROTECTED
	// PRIVATE
	private String	login;

	private String	gcmId;
}
