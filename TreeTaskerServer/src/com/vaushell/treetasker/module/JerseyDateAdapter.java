package com.vaushell.treetasker.module;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class JerseyDateAdapter
    extends XmlAdapter<String, Date>
{
	// PUBLIC
	@Override
	public String marshal( Date date )
	    throws Exception
	{
		return df.format( date );
	}

	@Override
	public Date unmarshal( String stringValue )
	    throws Exception
	{
		return df.parse( stringValue );
	}

	// PROTECTED
	// PRIVATE
	private static final SimpleDateFormat	df	= new SimpleDateFormat(
	                                                                    "yyyy-MM-dd'T'HH:mm:ss.SSSZ" );
}
