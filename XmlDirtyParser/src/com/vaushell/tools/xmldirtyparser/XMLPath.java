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

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Fabien Vauchelles <fabien at vauchelles dot com>
 */
public class XMLPath
	implements Comparable<XMLPath>
{
	// PUBLIC
	public XMLPath()
	{
		init();
	}

	public XMLPath(
		String path )
	{
		init();

		for ( String element : StringUtils.split( path, "/" ) )
		{
			if ( element.startsWith( "@" ) )
			{
				addElementLast( new XMLPathProperty( element.substring( 1 ) ) );
			}
			else
			{
				addElementLast( new XMLPathElement( element ) );
			}
		}
	}

	public void addElementLast(
		XMLPathElement element ) {
		elements.add( element );
	}

	@Override
	public int compareTo(
		XMLPath o ) {
		Iterator<XMLPathElement> local = elements.iterator();
		Iterator<XMLPathElement> external = o.elements.iterator();

		while ( true )
		{
			if ( local.hasNext() )
			{
				if ( external.hasNext() )
				{
					XMLPathElement localE = local.next();
					XMLPathElement externalE = external.next();
					int cmp = localE.compareTo( externalE );

					if ( cmp != 0 )
					{
						return cmp;
					}
				}
				else
				{
					return 1;
				}
			}
			else
			{
				if ( external.hasNext() )
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}
		}
	}

	public void removeElementLast() {
		elements.removeLast();
	}

	public void removeElementLast(
		XMLPathElement element ) {
		int ind = elements.size() - 1;

		while ( ind >= 0 )
		{
			if ( elements.get( ind ).compareTo( element ) == 0 )
			{
				break;
			}
		}

		if ( ind > 0 )
		{
			for ( int i = elements.size() - 1; i >= ind; i-- )
			{
				removeElementLast();
			}
		}
	}

	@Override
	public String toString() {
		return StringUtils.join( elements, "/" );
	}

	private void init() {
		elements = new LinkedList<XMLPathElement>();
		addElementLast( new XMLPathElement( "" ) );
	}

	// PRIVATE
	private LinkedList<XMLPathElement>	elements;
}
