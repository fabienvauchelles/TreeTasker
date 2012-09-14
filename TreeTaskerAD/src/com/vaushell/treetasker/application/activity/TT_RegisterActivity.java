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
import android.widget.TextView;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.client.SimpleJsonClient;
import com.vaushell.treetasker.module.UserAuthenticationRequest;
import com.vaushell.treetasker.module.UserSession;
import com.vaushell.treetasker.tools.TT_Tools;

public class TT_RegisterActivity
    extends Activity
{
	// PUBLIC
	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.register );
		setResult( RESULT_CANCELED ); // Juste pour �tre s�r que si
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
			case REGISTER_KO_DIALOG:
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
			case REGISTER_OK_DIALOG:
				dialogBuilder.setTitle( R.string.achieved )
				             .setMessage( R.string.register_achieved )
				             .setNeutralButton( R.string.ok,
				                                new DialogInterface.OnClickListener()
				                                {
					                                public void onClick( DialogInterface dialog,
					                                                     int which )
					                                {
						                                TT_RegisterActivity.this.finish();
					                                }
				                                } );
				break;
			default:
				throw new RuntimeException( "ID de dialog inconnu: " + id );
		}

		return dialogBuilder.create();
	}

	// PRIVATE
	private static final int	          REGISTER_KO_DIALOG	= -1;
	private static final int	          REGISTER_OK_DIALOG	= -2;
	private static final int	          SERVER_KO_DIALOG	 = -3;

	private static final SimpleJsonClient	client	         = new SimpleJsonClient().resource( "http://10.0.2.2:8888/resources/register" );

	private void initListeners()
	{
		findViewById( R.id.aBTregisterUser ).setOnClickListener( new OnClickListener()
		{
			public void onClick( View v )
			{
				register();
			}
		} );
	}

	private void register()
	{
		String mail = ( (TextView) findViewById( R.id.aTXTregisterMailValue ) ).getText()
		                                                                       .toString()
		                                                                       .trim();
		String password = ( (TextView) findViewById( R.id.aTXTregisterPasswordValue ) ).getText()
		                                                                               .toString()
		                                                                               .trim();

		if ( mail.length() == 0
		     || password.length() == 0 )
		{
			showDialog( REGISTER_KO_DIALOG );
		}
		else
		{
			checkForUserRegister( mail, password );
		}
	}

	private void checkForUserRegister( String mail,
	                                   String password )
	{
		try
		{
			UserSession session = client.post( UserSession.class,
			                                   new UserAuthenticationRequest(
			                                                                  mail,
			                                                                  TT_Tools.encryptPassword( mail,
			                                                                                            password ) ) );

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
		catch ( ClientProtocolException e )
		{
			showDialog( SERVER_KO_DIALOG );
		}
	}
}
