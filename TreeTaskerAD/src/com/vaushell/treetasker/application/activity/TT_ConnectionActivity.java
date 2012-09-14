package com.vaushell.treetasker.application.activity;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.client.SimpleJsonClient;
import com.vaushell.treetasker.module.UserAuthenticationRequest;
import com.vaushell.treetasker.module.UserSession;
import com.vaushell.treetasker.tools.TT_Tools;

public class TT_ConnectionActivity
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
	protected void onActivityResult( int requestCode,
	                                 int resultCode,
	                                 Intent data )
	{
		if ( resultCode == RESULT_OK )
		{
			// Utilisateur authentifié, charger les données. Utiliser un Loader
			// apparemment
			( (EditText) findViewById( R.id.aTXTloginValue ) ).setText( data.getStringExtra( TT_TaskListActivity.USERNAME ) );
		}
	}

	@Override
	protected Dialog onCreateDialog( int id )
	{
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( this );
		switch ( id )
		{
			case UserSession.MESSAGE_BAD_AUTHENTICATION:
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
			case UserSession.MESSAGE_REGISTRATION_NOT_VALIDATED:
				dialogBuilder.setTitle( R.string.error )
				             .setMessage( R.string.registration_not_validated )
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
				throw new RuntimeException( "ID de dialog inconnu: " + id );
		}

		return dialogBuilder.create();
	}

	// PRIVATE
	private static final int	          SERVER_KO_DIALOG	= -1;

	private static final SimpleJsonClient	client	       = new SimpleJsonClient().resource( "http://10.0.2.2:8888/resources/login" );

	private void initListeners()
	{
		findViewById( R.id.aBTconnect ).setOnClickListener( new OnClickListener()
		{
			public void onClick( View v )
			{
				login();
			}
		} );
		findViewById( R.id.aBTregister ).setOnClickListener( new OnClickListener()
		{
			public void onClick( View v )
			{
				launchRegisterActivity();
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
			showDialog( UserSession.MESSAGE_BAD_AUTHENTICATION );
		}
		else
		{
			checkForUserAuthentication( username, password );
		}
	}

	private void checkForUserAuthentication( String username,
	                                         String password )
	{
		try
		{
			UserSession session = client.post( UserSession.class,
			                                   new UserAuthenticationRequest(
			                                                                  username,
			                                                                  TT_Tools.encryptPassword( username,
			                                                                                            password ) ) );

			boolean authenticated = session.getSessionState() == UserSession.SESSION_OK;
			if ( authenticated )
			{
				Intent okIntent = new Intent();
				okIntent.putExtra( TT_TaskListActivity.USERNAME, username );
				okIntent.putExtra( TT_TaskListActivity.SESSIONID,
				                   session.getUserSessionID() );
				setResult( RESULT_OK, okIntent );
				finish();
			}
			else
			{
				showDialog( session.getSessionMessage() );
			}
		}
		catch ( ClientProtocolException e )
		{
			showDialog( SERVER_KO_DIALOG );
		}
	}

	private void launchRegisterActivity()
	{
		Intent intent = new Intent( this, TT_RegisterActivity.class );
		startActivityForResult( intent, 0 );
	}
}
