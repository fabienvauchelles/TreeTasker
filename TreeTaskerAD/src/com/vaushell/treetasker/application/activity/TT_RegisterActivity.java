/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.application.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.client.E_BadResponseStatus;
import com.vaushell.treetasker.model.TreeTaskerControllerDAO;
import com.vaushell.treetasker.net.UserAuthenticationRequest;
import com.vaushell.treetasker.net.UserSession;
import com.vaushell.treetasker.tools.TT_Tools;

public class TT_RegisterActivity
	extends Activity
{
	// PRIVATE
	private static final int	REGISTER_KO_DIALOG	= -1;

	private static final int	REGISTER_OK_DIALOG	= -2;

	private static final int	SERVER_KO_DIALOG	= -3;

	// PUBLIC
	/** Called when the activity is first created. */
	@Override
	public void onCreate(
		Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.register );
		setResult( RESULT_CANCELED ); // Juste pour être sûr que si
										// l'utilisateur ferme l'activity, elle
										// retournera CANCELLED

		prefs = PreferenceManager.getDefaultSharedPreferences( this );

		initListeners();
	}

	// PROTECTED
	@Override
	protected Dialog onCreateDialog(
		int id ) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( this );
		switch ( id )
		{
			case REGISTER_KO_DIALOG:
				dialogBuilder.setTitle( R.string.error ).setMessage( R.string.not_authenticated )
					.setNeutralButton( R.string.ok, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(
							DialogInterface dialog,
							int which ) {
							dialog.dismiss();
						}
					} );
				break;
			case SERVER_KO_DIALOG:
				dialogBuilder.setTitle( R.string.error ).setMessage( R.string.server_not_reachable )
					.setNeutralButton( R.string.ok, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(
							DialogInterface dialog,
							int which ) {
							dialog.dismiss();
						}
					} );
				break;
			case REGISTER_OK_DIALOG:
				dialogBuilder.setTitle( R.string.achieved ).setMessage( R.string.register_achieved )
					.setNeutralButton( R.string.ok, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(
							DialogInterface dialog,
							int which ) {
							TT_RegisterActivity.this.finish();
						}
					} );
				break;
			default:
				throw new RuntimeException( "ID de dialog inconnu: " + id );
		}

		return dialogBuilder.create();
	}

	private void checkForUserRegister(
		String mail,
		String password ) {
		try
		{
			UserSession session = TreeTaskerControllerDAO
				.getInstance()
				.getRegisterClient(
					prefs.getString( getString( R.string.endpoint ), TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) )
				.post( UserSession.class,
					new UserAuthenticationRequest( mail, TT_Tools.encryptPassword( mail, password ) ) );

			boolean authenticated = session.getSessionState() == UserSession.SESSION_OK;
			if ( authenticated )
			{
				Intent okIntent = new Intent();
				okIntent.putExtra( TT_TaskListActivity.USERNAME, mail );
				setResult( RESULT_OK, okIntent );
				showDialog( REGISTER_OK_DIALOG );
			}
			else
			{
				showDialog( REGISTER_KO_DIALOG );
			}
		}
		catch ( IOException e )
		{
			showDialog( SERVER_KO_DIALOG );
		}
		catch ( E_BadResponseStatus e )
		{
			e.printStackTrace();
			showDialog( SERVER_KO_DIALOG );
		}
	}

	private void eraseAll() {
		( (TextView) findViewById( R.id.aTXTregisterMailValue ) ).setText( "" );
		( (TextView) findViewById( R.id.aTXTregisterPasswordValue ) ).setText( "" );
		( (TextView) findViewById( R.id.aTXTconfirmPasswordValue ) ).setText( "" );
	}

	private void initListeners() {
		findViewById( R.id.aBTeraseAll ).setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(
				View v ) {
				eraseAll();
				findViewById( R.id.aTXTregisterMailValue ).requestFocus();
			}
		} );

		( (TextView) findViewById( R.id.aTXTconfirmPasswordValue ) )
			.setOnEditorActionListener( new OnEditorActionListener()
			{

				@Override
				public boolean onEditorAction(
					TextView v,
					int actionId,
					KeyEvent event ) {
					if ( actionId != EditorInfo.IME_ACTION_DONE && actionId != EditorInfo.IME_ACTION_NEXT )
					{
						return false;
					}

					register();
					return true;
				}
			} );

		findViewById( R.id.aBTregisterUser ).setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(
				View v ) {
				register();
			}
		} );
	}

	private void register() {
		String mail = ( (TextView) findViewById( R.id.aTXTregisterMailValue ) ).getText().toString().trim();
		String password = ( (TextView) findViewById( R.id.aTXTregisterPasswordValue ) ).getText().toString().trim();
		String confirmPassword = ( (TextView) findViewById( R.id.aTXTconfirmPasswordValue ) ).getText().toString()
			.trim();

		if ( mail.length() == 0 || password.length() == 0 )
		{
			warn( R.string.not_authenticated );
		}
		else if ( !password.equals( confirmPassword ) )
		{
			warn( R.string.password_not_confirmed_properly );

			( (TextView) findViewById( R.id.aTXTconfirmPasswordValue ) ).setText( "" );
			( (TextView) findViewById( R.id.aTXTregisterPasswordValue ) ).setText( "" );
			findViewById( R.id.aTXTregisterPasswordValue ).requestFocus();
		}
		else
		{
			checkForUserRegister( mail, password );
		}
	}

	private void warn(
		int stringId ) {
		Toast.makeText( getApplicationContext(), getString( stringId ), Toast.LENGTH_SHORT ).show();
	}

	private SharedPreferences	prefs;
}
