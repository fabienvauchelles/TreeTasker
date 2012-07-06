package com.vaushell.treetasker;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.sun.org.apache.bcel.internal.generic.ATHROW;
import com.vaushell.treetasker.client.SimpleJsonClient;
import com.vaushell.treetasker.module.UserAuthenticationRequest;
import com.vaushell.treetasker.module.UserSession;

public class TreeTaskerConnectionActivity
    extends Activity
{
	// PUBLIC
	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.connection );
		setResult( RESULT_CANCELED ); // Juste pour être sûr que si
		                              // l'utilisateur ferme l'activity, elle
		                              // retournera CANCELLED

		initListeners();
	}

	// PROTECTED
	@Override
	protected Dialog onCreateDialog( int id )
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( this );
		switch ( id )
		{
			case AUTHENTICATION_KO_DIALOG:
				dialogBuilder.setTitle( R.string.error )
				             .setMessage( R.string.not_authenticated )
				             .setNeutralButton( R.string.ok,
				                                new DialogInterface.OnClickListener()
				                                {
					                                public void onClick( DialogInterface dialog,
					                                                     int which )
					                                {
						                                dialog.dismiss();
					                                }
				                                } );
				break;
			case SERVER_KO_DIALOG:
				dialogBuilder.setTitle( R.string.error )
				             .setMessage( R.string.server_not_reachable )
				             .setNeutralButton( R.string.ok,
				                                new DialogInterface.OnClickListener()
				                                {
					                                public void onClick( DialogInterface dialog,
					                                                     int which )
					                                {
						                                dialog.dismiss();
					                                }
				                                } );
				break;
			default:
				throw new RuntimeException( "ID de dialog impossible." );
		}

		return dialogBuilder.create();
	}

	// PRIVATE
	private static final int	          AUTHENTICATION_KO_DIALOG	= 0;
	private static final int	          SERVER_KO_DIALOG	       = 1;

	private static final SimpleJsonClient	client	               = new SimpleJsonClient().resource( "http://vsh2-test.appspot.com/resources/login" );

	private void initListeners()
	{
		findViewById( R.id.aBTconnect ).setOnClickListener( new OnClickListener()
		{
			public void onClick( View v )
			{
				login();
			}
		} );
	}

	private void login()
	{
		String username = ( (TextView) findViewById( R.id.aTXTloginValue ) ).getText()
		                                                                    .toString()
		                                                                    .trim();
		String password = ( (TextView) findViewById( R.id.aTXTpasswordValue ) ).getText()
		                                                                       .toString()
		                                                                       .trim();

		if ( username.length() == 0
		     || password.length() == 0 )
		{
			showDialog( AUTHENTICATION_KO_DIALOG );
		}
		else
		{
			checkForUserAuthentication( username, password );
		}
	}

	// TODO:
	// - Faire l'authentification sur la base
	// - Si OK, retourner une userSession à l'activity parent
	// - Si NOK, afficher une popup d'avertissement
	private void checkForUserAuthentication( String username,
	                                         String password )
	{

		// TODO: Code pour se connecter à la base et vérifier l'authentification
		// UserSession session = TreeTaskerADactivity.client
		// .resource( "http://vsh2-test.appspot.com/resources/login" )
		// .type( MediaType.APPLICATION_JSON )
		// .post( UserSession.class,
		// new UserAuthenticationRequest(
		// username,
		// password ) );
		try
		{
			UserSession session = client.post( UserSession.class,
			                                   new UserAuthenticationRequest(
			                                                                  username,
			                                                                  password ) );

			boolean authenticated = session.getSessionState() == UserSession.SESSION_OK;
			if ( authenticated )
			{
				Intent okIntent = new Intent();
				okIntent.putExtra( TreeTaskerADactivity.USERNAME, username );
				okIntent.putExtra( TreeTaskerADactivity.SESSIONID,
				                   session.getUserSessionID() );
				setResult( RESULT_OK, okIntent );
				finish();
			}
			else
			{
				showDialog( AUTHENTICATION_KO_DIALOG );
			}
		}
		catch ( ClientProtocolException e )
		{
			showDialog( SERVER_KO_DIALOG );
		}
	}
}
