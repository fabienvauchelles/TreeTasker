/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.window;

import com.vaadin.ui.Window;
import com.vaushell.treetasker.application.content.layout.RegistrationLayout;
import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.net.UserSession;
import com.vaushell.treetasker.tools.TT_Tools;

/**
 * This window contains the registration form.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class RegistrationWindow
	extends Window
	implements I_Form
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	public RegistrationWindow()
	{
		super( "Enregistrement" );
		init();
	}

	@Override
	public void cancel() {
		close();
	}

	@Override
	/**
	 * Try to register the new account on the server.
	 */
	public void ok() {
		if ( content.isValid() )
		{
			String userName = content.getUserName();
			String encryptedPassword = TT_Tools.encryptPassword( userName, content.getPassword() );
			UserSession session = TT_ServerControllerDAO.getInstance().registerUser( userName, encryptedPassword );
			if ( session.isValid() )
			{
				Notification n = new Notification( "Enregistrement réussi", "Un mail a été envoyé à " + userName
					+ " afin de valider votre enregistrement" );
				n.setDelayMsec( Notification.DELAY_FOREVER );
				getParent().showNotification( n );
				close();
			}
			else
			{
				showNotification( userName + " est déjà utilisé.", Notification.TYPE_ERROR_MESSAGE );
			}
		}
		else
		{
			showNotification( content.getErrorMsg(), Notification.TYPE_ERROR_MESSAGE );
		}
	}

	private void init() {
		content = new RegistrationLayout( this );
		setContent( content );
		setModal( true );
	}

	private RegistrationLayout	content;

}
