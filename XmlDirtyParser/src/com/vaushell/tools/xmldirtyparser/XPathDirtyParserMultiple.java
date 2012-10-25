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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Fabien Vauchelles <fabien at vauchelles dot com>
 */
public class XPathDirtyParserMultiple
	extends A_XPathDirtyParser
{
	// PUBLIC
	public XPathDirtyParserMultiple()
	{
		super();
		init();
	}

	public void addAction(
		I_ParserAction action ) {
		actions.add( action );
	}

	@Override
	public void after() {
		for ( I_ParserAction action : actions )
		{
			action.after();
		}
	}

	@Override
	public void before() {
		for ( I_ParserAction action : actions )
		{
			action.before();
		}
	}

	public void removeAction(
		I_ParserAction action ) {
		actions.remove( action );
	}

	@Override
	public void tagEnd(
		XMLPath path ) {
		for ( I_ParserAction action : actions )
		{
			action.tagEnd( path );
		}
	}

	@Override
	public void tagStart(
		XMLPath path ) {
		for ( I_ParserAction action : actions )
		{
			action.tagStart( path );
		}
	}

	@Override
	public void tagText(
		XMLPath path,
		String value ) {
		for ( I_ParserAction action : actions )
		{
			action.tagText( path, value );
		}
	}

	private void init() {
		actions = new ArrayList<I_ParserAction>();
	}

	// PRIVATE
	private List<I_ParserAction>	actions;
}
