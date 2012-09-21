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
