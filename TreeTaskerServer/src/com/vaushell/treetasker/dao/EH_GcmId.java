/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.dao;

import com.google.appengine.api.datastore.Entity;

public class EH_GcmId
	extends A_EntityHandler
{
	// PUBLIC
	public static final String	KIND	= "GcmId";

	public EH_GcmId(
		Entity entity )
	{
		super( KIND, entity );
	}

	public EH_GcmId(
		String gcmId,
		String userName )
	{
		super( KIND, gcmId );
		this.gcmId = gcmId;
		this.userName = userName;
		init();
	}

	@Override
	public Entity getEntity() {
		entity.setProperty( "userName", getUserName() );

		return entity;
	}

	public String getGcmId() {
		return gcmId;
	}

	public String getUserName() {
		return userName;
	}

	@Override
	public void handleEntity(
		Entity entity )
		throws E_InvalidEntityHandling {
		checkKindsDoMatch( entity );

		gcmId = entity.getKey().getName();
		userName = (String) entity.getProperty( "userName" );
	}

	public void setGcmId(
		String gcmId ) {
		this.gcmId = gcmId;
	}

	public void setUserName(
		String userName ) {
		this.userName = userName;
	}

	private void init() {
	}

	// PROTECTED
	// PRIVATE
	private String	gcmId;

	private String	userName;

}
