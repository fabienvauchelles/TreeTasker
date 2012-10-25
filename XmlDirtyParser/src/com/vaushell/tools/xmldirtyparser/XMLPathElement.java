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

/**
 * 
 * @author Fabien Vauchelles <fabien at vauchelles dot com>
 */
public class XMLPathElement
	implements Comparable<XMLPathElement>
{
	// PUBLIC
	public XMLPathElement(
		String ID )
	{
		this.ID = ID;
		init();
	}

	@Override
	public int compareTo(
		XMLPathElement o ) {
		return ID.compareTo( o.ID );
	}

	@Override
	public String toString() {
		return ID;
	}

	// PRIVATE
	private void init() {
	}

	// PROTECTED
	protected String	ID;
}
