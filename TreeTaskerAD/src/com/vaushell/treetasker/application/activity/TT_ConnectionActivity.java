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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.vaushell.treetasker.R;
import com.vaushell.treetasker.client.E_BadResponseStatus;
import com.vaushell.treetasker.model.TreeTaskerControllerDAO;
import com.vaushell.treetasker.net.UserAuthenticationRequest;
import com.vaushell.treetasker.net.UserSession;
import com.vaushell.treetasker.net.UserSessionCheckRequest;
import com.vaushell.treetasker.tools.TT_Tools;

public class TT_ConnectionActivity
	extends Activity
{
	// PUBLIC

	private class CheckSessionActivity
		extends AsyncTask<UserSession, Void, UserSession>
	{

		@Override
		protected UserSession doInBackground(
			UserSession... paramSession ) {
			UserSession session = new UserSession();
			try
			{
				session = DAO.getCheckClient(
					prefs.getString( getString( R.string.endpoint ), TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) )
					.post(
						UserSession.class,
						new UserSessionCheckRequest( paramSession[ 0 ].getUserSessionID(), paramSession[ 0 ]
							.getUserName() ) );
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
			catch ( E_BadResponseStatus e )
			{
				e.printStackTrace();
			}
			return session;
		}

		@Override
		protected void onCancelled() {
			dialog.dismiss();
			finishCheck( null );
		}

		@Override
		protected void onPostExecute(
			UserSession userSession ) {
			dialog.dismiss();
			finishCheck( userSession );
		}

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show( TT_ConnectionActivity.this, "", getString( R.string.wait_session_check ) );
		}

		private ProgressDialog	dialog;
	}

	private class ConnectionActivity
		extends AsyncTask<UserAuthenticationRequest, Void, UserSession>
	{

		@Override
		protected UserSession doInBackground(
			UserAuthenticationRequest... paramRequest ) {
			UserSession session = new UserSession();

			try
			{
				session = DAO.getConnectionClient(
					prefs.getString( getString( R.string.endpoint ), TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) )
					.post( UserSession.class, paramRequest[ 0 ] );
			}
			catch ( IOException e )
			{
				e.printStackTrace();
				session.setSessionMessage( UserSession.MESSAGE_UNREACHABLE_SERVER );
			}
			catch ( E_BadResponseStatus e )
			{
				e.printStackTrace();
				session.setSessionMessage( UserSession.MESSAGE_UNREACHABLE_SERVER );
			}

			if ( GCMRegistrar.isRegistered( TT_ConnectionActivity.this ) )
			{
				DAO.registerDeviceOnServer( GCMRegistrar.getRegistrationId( TT_ConnectionActivity.this ),
					prefs.getString( getString( R.string.endpoint ), TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) );
			}
			else
			{
				DAO.registerDeviceOnGCM( TT_ConnectionActivity.this,
					prefs.getString( getString( R.string.endpoint ), TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) );
			}
			return session;
		}

		@Override
		protected void onCancelled() {
			dialog.dismiss();
			finishConnection( null );
		}

		@Override
		protected void onPostExecute(
			UserSession userSession ) {
			dialog.dismiss();
			finishConnection( userSession );
		}

		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show( TT_ConnectionActivity.this, "", getString( R.string.wait_connection ) );
		}

		private ProgressDialog	dialog;
	}

	// PRIVATE
	private static final TreeTaskerControllerDAO	DAO	= TreeTaskerControllerDAO.getInstance();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(
		Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setResult( RESULT_CANCELED );

		final SharedPreferences appPrefs = getPreferences( MODE_PRIVATE );
		prefs = PreferenceManager.getDefaultSharedPreferences( this );

		// Si premier lancement
		if ( appPrefs.getBoolean( "firstrun", true ) )
		{
			// Ouverture de dialog pour renseignement de l'adresse du serveur
			AlertDialog.Builder webServerAlert = new AlertDialog.Builder( this );
			webServerAlert.setTitle( R.string.endpoint_caption );
			webServerAlert.setMessage( getString( R.string.endpoint_summary ) );

			// Input view
			final EditText input = new EditText( this );
			input.setInputType( InputType.TYPE_TEXT_VARIATION_URI );
			input.setText( prefs.getString( getString( R.string.endpoint ),
				TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) );
			webServerAlert.setView( input );

			webServerAlert.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(
					DialogInterface dialog,
					int which ) {
					prefs.edit().putString( getString( R.string.endpoint ), input.getText().toString() ).commit();
					appPrefs.edit().putBoolean( "firstrun", false ).commit();
				}
			} );

			AlertDialog alert = webServerAlert.create();
			alert.show();
		}

		checkCurrentSessionFromServer( DAO.getUserSession() );
	}

	// PROTECTED
	@Override
	protected void onActivityResult(
		int requestCode,
		int resultCode,
		Intent data ) {
		if ( resultCode == RESULT_OK )
		{
			// Utilisateur authentifié, charger les données. Utiliser un Loader
			// apparemment
			( (EditText) findViewById( R.id.aTXTloginValue ) ).setText( data
				.getStringExtra( TT_TaskListActivity.USERNAME ) );
		}
	}

	@Override
	protected Dialog onCreateDialog(
		int id ) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( this );
		switch ( id )
		{
			case UserSession.MESSAGE_BAD_AUTHENTICATION:
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
			case UserSession.MESSAGE_REGISTRATION_NOT_VALIDATED:
				dialogBuilder.setTitle( R.string.error ).setMessage( R.string.registration_not_validated )
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
			case UserSession.MESSAGE_UNREACHABLE_SERVER:
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
			default:
				throw new RuntimeException( "Unknown dialog ID: " + id );
		}

		return dialogBuilder.create();
	}

	private void checkCurrentSessionFromServer(
		UserSession currentSession ) {
		if ( currentSession != null )
		{
			if ( currentSession.getUserSessionID() != null )
			{
				new CheckSessionActivity().execute( currentSession );
			}
			else
			{
				displayActivity( currentSession.getUserName() );
			}
		}
		else
		{
			displayActivity( null );
		}
	}

	private void connect(
		String username,
		String password ) {
		new ConnectionActivity().execute( new UserAuthenticationRequest( username, TT_Tools.encryptPassword( username,
			password ) ) );
	}

	private void displayActivity(
		String username ) {
		setContentView( R.layout.connection );
		if ( username != null )
		{
			( (EditText) findViewById( R.id.aTXTloginValue ) ).setText( username );
		}
		initListeners();
	}

	private void eraseAll() {
		( (TextView) findViewById( R.id.aTXTloginValue ) ).setText( "" );
		( (TextView) findViewById( R.id.aTXTpasswordValue ) ).setText( "" );
	}

	private void finishCheck(
		UserSession userSession ) {
		if ( userSession != null && userSession.getSessionState() == UserSession.SESSION_OK )
		{
			Intent okIntent = new Intent();
			okIntent.putExtra( TT_TaskListActivity.USERNAME, userSession.getUserName() );
			okIntent.putExtra( TT_TaskListActivity.SESSIONID, userSession.getUserSessionID() );
			setResult( RESULT_OK, okIntent );
			finish();
		}
		else
		{
			displayActivity( userSession.getUserName() );
		}
	}

	private void finishConnection(
		UserSession userSession ) {
		if ( userSession != null )
		{
			if ( userSession.isValid() )
			{
				Intent okIntent = new Intent();
				okIntent.putExtra( TT_TaskListActivity.USERNAME, userSession.getUserName() );
				okIntent.putExtra( TT_TaskListActivity.SESSIONID, userSession.getUserSessionID() );
				setResult( RESULT_OK, okIntent );
				finish();
			}
			else
			{
				if ( userSession.getSessionMessage() == UserSession.MESSAGE_BAD_AUTHENTICATION )
				{
					warn( R.string.not_authenticated );
				}
				else
				{
					showDialog( userSession.getSessionMessage() );
				}
			}
		}
	}

	private void initListeners() {
		( (TextView) findViewById( R.id.aTXTpasswordValue ) ).setOnEditorActionListener( new OnEditorActionListener()
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

				login();
				return true;
			}
		} );

		findViewById( R.id.aBTconnect ).setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(
				View v ) {
				login();
			}
		} );
		findViewById( R.id.aBTerase ).setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(
				View v ) {
				eraseAll();
				findViewById( R.id.aTXTloginValue ).requestFocus();
			}
		} );
		findViewById( R.id.aBTregister ).setOnClickListener( new OnClickListener()
		{
			@Override
			public void onClick(
				View v ) {
				launchRegisterActivity();
			}
		} );
	}

	private void launchRegisterActivity() {
		Intent intent = new Intent( this, TT_RegisterActivity.class );
		startActivityForResult( intent, 0 );
	}

	private void login() {
		String username = ( (TextView) findViewById( R.id.aTXTloginValue ) ).getText().toString().trim();
		String password = ( (TextView) findViewById( R.id.aTXTpasswordValue ) ).getText().toString().trim();

		if ( username.length() == 0 || password.length() == 0 )
		{
			warn( R.string.not_authenticated );
		}
		else
		{
			connect( username, password );
		}
	}

	private void warn(
		int stringId ) {
		Toast.makeText( getApplicationContext(), stringId, Toast.LENGTH_SHORT ).show();
	}

	private SharedPreferences	prefs;
}
