/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.module;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserAuthenticationRequest
{
	// PUBLIC
	public UserAuthenticationRequest()
	{
		this( null, null );
	}

	public UserAuthenticationRequest( String login,
	                                  String password )
	{
		this.login = login;
		this.password = password;
		init();
	}

	public String getLogin()
	{
		return login;
	}

	public void setLogin( String login )
	{
		this.login = login;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword( String password )
	{
		this.password = password;
	}

	// PROTECTED
	// PRIVATE
	private String	login;
	private String	password;

	private void init()
	{

	}
}
