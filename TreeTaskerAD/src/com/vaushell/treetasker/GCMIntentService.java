package com.vaushell.treetasker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.vaushell.treetasker.application.service.DataAccessService;
import com.vaushell.treetasker.model.TreeTaskerControllerDAO;
import com.vaushell.treetasker.net.UserSession;

public class GCMIntentService
	extends GCMBaseIntentService
{
	/**
	 * Google API project id registered to use GCM.
	 */
	public static final String	SENDER_ID	= "1072286232709";

	/**
	 * Tag used on log messages.
	 */
	static final String			TAG			= "GCMTreeTasker";

	@Override
	public void onCreate() {
		super.onCreate();
		GCMRegistrar.checkDevice( this );
		GCMRegistrar.checkManifest( this );
		final String regId = GCMRegistrar.getRegistrationId( this );
		if ( regId.equals( "" ) )
		{
			GCMRegistrar.register( this, SENDER_ID );
		}
		else
		{
			Log.v( TAG, "Already registered" );
		}
	}

	@Override
	protected void onError(
		Context arg0,
		String arg1 ) {
		// TODO Analyser l'erreur.

	}

	@Override
	protected void onMessage(
		Context context,
		Intent intent ) {
		System.out.println( "Message reçu !" );

		UserSession userSession = TreeTaskerControllerDAO.getInstance().loadUserSessionFromCache( this );

		Intent serviceStartIntent = new Intent( this, DataAccessService.class );
		serviceStartIntent.putExtra( "only-sync", true );
		serviceStartIntent.putExtra( "user-session", userSession );

		startService( serviceStartIntent );
	}

	@Override
	protected void onRegistered(
		Context context,
		String regId ) {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		TreeTaskerControllerDAO.getInstance().registerDeviceOnServer( regId,
			prefs.getString( getString( R.string.endpoint ), TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) );
	}

	@Override
	protected void onUnregistered(
		Context context,
		String regId ) {

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		TreeTaskerControllerDAO.getInstance().unregisterDeviceFromServer( regId,
			prefs.getString( getString( R.string.endpoint ), TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE ) );

	}
}
