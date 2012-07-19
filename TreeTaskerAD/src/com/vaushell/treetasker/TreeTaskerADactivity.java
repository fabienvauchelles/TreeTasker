package com.vaushell.treetasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sun.jersey.api.client.Client;
import com.vaushell.treetasker.module.UserSession;

public class TreeTaskerADactivity
    extends Activity
{
	// PUBLIC
	public static final Client client = Client.create();
	public static final String	USERNAME	= "USERNAME";
	public static final String	SESSIONID	= "SESSIONID";

	/** Called when the activity is first created. */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setContentView( R.layout.main );

		requestAuthentication();
	}

	// PROTECTED
	@Override
	protected void onActivityResult( int requestCode,
	                                 int resultCode,
	                                 Intent data )
	{
		if ( resultCode == RESULT_OK )
		{
			// Utilisateur authentifié, charger les données. Utiliser un Loader apparemment
			session = new UserSession( data.getStringExtra( USERNAME ),
			                           data.getStringExtra( SESSIONID ) );
		}
		else
		{
			finish();
		}
	}

	// PRIVATE
	private UserSession	session	= null;

	private void requestAuthentication() // Afficher l'activity de connexion
	{
		Intent intent = new Intent( this, TreeTaskerConnectionActivity.class );
		startActivityForResult( intent, 0 );
	}
}
