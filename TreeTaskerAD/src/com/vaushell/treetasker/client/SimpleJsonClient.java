package com.vaushell.treetasker.client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.vaushell.treetasker.tools.TT_Tools;

public class SimpleJsonClient
{
	// PUBLIC
	public SimpleJsonClient()
	{
		init();
	}

	public SimpleJsonClient resource( String resource )
	{
		this.resource = resource;
		return this;
	}

	public SimpleJsonClient path( String path )
	{
		this.path = path;
		return this;
	}

	public <T> T post( Class<T> classOfT,
	                   Object objectToSend )
	    throws ClientProtocolException
	{
		HttpPost request = new HttpPost(
		                                 cleanURI( TT_Tools.convertNullStringToEmpty( resource )
		                                           + TT_Tools.convertNullStringToEmpty( path ) ) );

		StringEntity se = null;
		try
		{
			se = new StringEntity( gson.toJson( objectToSend ) );
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new RuntimeException( e );
		}
		request.setHeader( "Accept", "application/json" );
		request.setHeader( "Content-type", "application/json" );
		request.setEntity( se );

		HttpResponse response = null;
		try
		{
			response = client.execute( request );
		}
		catch ( ClientProtocolException e )
		{
			throw e;
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}

		try
		{
			return gson.fromJson( new InputStreamReader( response.getEntity()
			                                                     .getContent() ),
			                      classOfT );
		}
		catch ( JsonSyntaxException e )
		{
			throw new RuntimeException( e );
		}
		catch ( JsonIOException e )
		{
			throw new RuntimeException( e );
		}
		catch ( IllegalStateException e )
		{
			throw new RuntimeException( e );
		}
		catch ( IOException e )
		{
			throw new RuntimeException( e );
		}
	}

	// PROTECTED
	// PRIVATE
	private final Gson	            gson	= new Gson();
	private final DefaultHttpClient	client	= new DefaultHttpClient();
	private String	                resource;
	private String	                path;

	private void init()
	{
		this.resource = null;
		this.path = null;
	}

	private String cleanURI( String uri )
	{
		int index = uri.indexOf( "//" ) + 2;
		StringBuilder builder = new StringBuilder( uri.substring( 0, index ) );
		builder.append( uri.substring( index ).replaceAll( "/+", "/" ) );
		return builder.toString();
	}
}
