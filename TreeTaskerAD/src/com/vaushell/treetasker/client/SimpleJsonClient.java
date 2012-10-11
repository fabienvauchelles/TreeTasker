/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.vaushell.treetasker.module.GsonDateAdapter;
import com.vaushell.treetasker.tools.TT_Tools;

public class SimpleJsonClient
{
	// PUBLIC
	public SimpleJsonClient()
	{
		init();
	}

	public SimpleJsonClient path(
		String path ) {
		this.path = path;
		return this;
	}

	public <T> T post(
		Class<T> responseClass,
		Object objectToSend )
		throws ClientProtocolException, E_BadResponseStatus {
		HttpPost request = new HttpPost( cleanURI( TT_Tools.convertNullStringToEmpty( resource )
			+ TT_Tools.convertNullStringToEmpty( path ) ) );

		StringEntity se = null;
		try
		{
			String sending = gson.toJson( objectToSend );
			System.out.println( "[SENDING]" );
			System.out.println( sending );
			se = new StringEntity( sending, "UTF-8" );
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

		StatusLine statusLine = response.getStatusLine();

		if ( statusLine.getStatusCode() / 100 != 2 ) // status code no success
		{
			throw new E_BadResponseStatus( statusLine.getStatusCode(), statusLine.getReasonPhrase() );
		}

		try
		{
			BufferedReader buffReader = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

			try
			{
				StringBuilder builder = new StringBuilder();

				System.out.println( "[RECEIVING]" );
				String line = buffReader.readLine();
				while ( line != null )
				{
					builder.append( line );
					System.out.println( line );
					line = buffReader.readLine();
				}

				buffReader.close();

				return gson.fromJson( builder.toString(), responseClass );
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
			finally
			{
				buffReader.close();
			}
		}
		catch ( IOException e1 )
		{
			e1.printStackTrace();
		}

		return null;
	}

	public SimpleJsonClient resource(
		String resource ) {
		this.resource = resource;
		return this;
	}

	private String cleanURI(
		String uri ) {
		int index = uri.indexOf( "//" ) + 2;
		StringBuilder builder = new StringBuilder( uri.substring( 0, index ) );
		builder.append( uri.substring( index ).replaceAll( "/+", "/" ) );
		return builder.toString();
	}

	private void init() {
		resource = null;
		path = null;
	}

	// PROTECTED
	// PRIVATE
	private final Gson				gson	= new GsonBuilder().registerTypeHierarchyAdapter( Date.class,
												new GsonDateAdapter() ).create();
	private final DefaultHttpClient	client	= new DefaultHttpClient();

	private String					resource;

	private String					path;
}
