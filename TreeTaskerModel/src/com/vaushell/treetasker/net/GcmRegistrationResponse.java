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
public class GcmRegistrationResponse
{
	// PUBLIC
	public GcmRegistrationResponse()
	{
		this( null );
	}

	public GcmRegistrationResponse(
		String responseText )
	{
		this.responseText = responseText;
		init();
	}

	public String getResponseText() {
		return responseText;
	}

	public void setResponseText(
		String responseText ) {
		this.responseText = responseText;
	}

	private void init() {
	}

	// PROTECTED
	// PRIVATE
	private String	responseText;
}
