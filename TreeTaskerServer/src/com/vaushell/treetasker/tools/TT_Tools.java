package com.vaushell.treetasker.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

	public static String encryptPassword( String username,
	                                      String password )
	{
		try
		{
			byte[] cryptedUsername = MessageDigest.getInstance( "MD5" )
			                                      .digest( username.getBytes() );
			byte[] cryptedPassword = MessageDigest.getInstance( "MD5" )
			                                      .digest( password.getBytes() );

			byte[] cryptedConcat = new byte[cryptedUsername.length
			                                + cryptedPassword.length];

			int i;
			for ( i = 0; i < cryptedUsername.length; ++i )
			{
				cryptedConcat[ i ] = cryptedUsername[ i ];
			}
			for ( ; i < cryptedUsername.length + cryptedPassword.length; ++i )
			{
				cryptedConcat[ i ] = cryptedPassword[ i
				                                      - cryptedUsername.length ];
			}

			cryptedConcat = MessageDigest.getInstance( "SHA-256" )
			                             .digest( cryptedConcat );

			StringBuilder builder = new StringBuilder();
			for ( int j = 0; j < cryptedConcat.length; ++j )
			{
				String hex = Integer.toHexString( cryptedConcat[ j ] );
				if ( hex.length() == 1 )
				{
					builder.append( '0' );
					builder.append( hex.charAt( hex.length() - 1 ) );
				}
				else
				{
					builder.append( hex.substring( hex.length() - 2 ) );
				}
			}

			return builder.toString();
		}
		catch ( NoSuchAlgorithmException e )
		{
			e.printStackTrace();
		}

		return null;
	}

	// PROTECTED
	// PRIVATE

	private void init()
	{

	}
}
