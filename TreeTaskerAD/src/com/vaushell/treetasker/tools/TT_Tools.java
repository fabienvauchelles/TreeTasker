package com.vaushell.treetasker.tools;

public class TT_Tools
{
	// PUBLIC
	public TT_Tools()
	{
		init();
	}

	public static String convertNullStringToEmpty( String string )
	{
		if ( string == null )
			return "";
		else
			return string.trim();
	}

	// PROTECTED
	// PRIVATE

	private void init()
	{

	}
}
