/*******************************************************************************
 * Copyright 2012 - VAUSHELL - contact@vaushell.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.tools.xmldirtyparser;

import java.io.IOException;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Fabien Vauchelles <fabien at vauchelles dot com>
 */
public abstract class A_XPathDirtyParser
	implements I_ParserAction
{
	// PRIVATE
	private final static String[]	HTML_SKIPPED	= new String[] { "html", "body", "head" };

	private static boolean skip(
		String qName ) {
		String rQname = qName.toLowerCase();

		for ( String tag : HTML_SKIPPED )
		{
			if ( rQname.equals( tag ) )
			{
				return true;
			}
		}

		return false;
	}

	// PUBLIC
	public A_XPathDirtyParser()
	{
		init();
	}

	public boolean isHTMLskipped() {
		return htmlSkipped;
	}

	public void parse(
		InputSource is )
		throws IOException, SAXException {
		before();

		final XMLPath path = new XMLPath();
		Parser parser = new Parser();
		DefaultHandler handler = new DefaultHandler()
		{
			@Override
			public void characters(
				char[] ch,
				int start,
				int length )
				throws SAXException {
				tagText( path, new String( ch, start, length ).trim() );
			}

			@Override
			public void endElement(
				String uri,
				String localName,
				String qName )
				throws SAXException {
				if ( htmlSkipped && skip( qName ) )
				{
					return;
				}

				tagEnd( path );
				path.removeElementLast( new XMLPathElement( qName ) );
			}

			@Override
			public void startElement(
				String uri,
				String localName,
				String qName,
				Attributes attributes )
				throws SAXException {
				if ( htmlSkipped && skip( qName ) )
				{
					return;
				}

				XMLPathElement element = new XMLPathElement( qName );

				path.addElementLast( element );
				tagStart( path );

				for ( int i = 0; i < attributes.getLength(); i++ )
				{
					XMLPathProperty property = new XMLPathProperty( attributes.getQName( i ) );

					path.addElementLast( property );
					tagText( path, attributes.getValue( i ) );
					path.removeElementLast( property );
				}
			}
		};

		parser.setContentHandler( handler );
		parser.parse( is );
		after();
	}

	public void setHTMLskipped(
		boolean value ) {
		htmlSkipped = value;
	}

	private void init() {
		htmlSkipped = true;
	}

	private boolean	htmlSkipped;
}
