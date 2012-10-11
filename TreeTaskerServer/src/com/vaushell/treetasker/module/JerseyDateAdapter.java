/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.module;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JerseyDateAdapter
	extends XmlAdapter<String, Date>
{
	// PROTECTED
	// PRIVATE
	// PUBLIC
	@Override
	public String marshal(
		Date date )
		throws Exception {
		return String.valueOf( date.getTime() );
	}

	@Override
	public Date unmarshal(
		String stringValue )
		throws Exception {
		return new Date( Long.parseLong( stringValue ) );
	}
}
