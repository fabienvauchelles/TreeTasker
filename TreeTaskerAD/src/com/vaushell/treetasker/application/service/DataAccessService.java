package com.vaushell.treetasker.application.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vaushell.treetasker.R;
import com.vaushell.treetasker.application.storage.TaskDB;
import com.vaushell.treetasker.client.E_BadResponseStatus;
import com.vaushell.treetasker.client.SimpleJsonClient;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.model.TT_UserTaskContainer;
import com.vaushell.treetasker.model.TreeTaskerControllerDAO;
import com.vaushell.treetasker.net.SyncingFinalRequest;
import com.vaushell.treetasker.net.SyncingFinalResponse;
import com.vaushell.treetasker.net.SyncingStartRequest;
import com.vaushell.treetasker.net.SyncingStartResponse;
import com.vaushell.treetasker.net.TaskStamp;
import com.vaushell.treetasker.net.UserSession;
import com.vaushell.treetasker.net.WS_Task;

public class DataAccessService
	extends Service
	implements I_DataAccessService
{
	public class DataAccessBinder
		extends Binder
	{
		public DataAccessBinder(
			I_DataAccessService service )
		{
			this.service = service;
		}

		public I_DataAccessService getService() {
			return service;
		}

		private I_DataAccessService	service	= null;
	}

	public interface DataAccessServiceListener
	{
		public void allTasksRetrieved(
			Set<WS_Task> tasks,
			Set<String> expandedSet );

		public void syncFinalized();

		public void syncStarted();
	}

	// PUBLIC
	public static final String	LOG_TAG_SERVICE			= "DataAccessService";
	public static final String	LOG_TAG_SERVICE_CRUD	= "DAS@CRUD";
	public static final String	LOG_TAG_THREAD			= "DataAccessService.Thread";
	public static final String	PROPERTY_LAST_SYNC_DATE	= "last-sync-date";
	public static final String	PROPERTY_USER_SESSION	= "user-session";
	public static final String	PROPERTY_ONLY_SYNC		= "only-sync";

	// PRIVATE
	private static final int	DELAY_USER				= 1;

	@Override
	public void createOrUpdateTask(
		final TT_Task task,
		final boolean isExpanded ) {
		new Thread( new Runnable()
		{
			@Override
			public void run() {
				Log.i( LOG_TAG_SERVICE_CRUD, "createOrUpdateTask:" + task.getID() );
				taskDataBase.insertOrUpdateTask( task, isExpanded );
				requestSynchronization();
			}
		} ).start();
	}

	@Override
	public void createOrUpdateTasks(
		final Collection<TT_Task> tasks,
		final Set<String> expandedSet ) {
		new Thread( new Runnable()
		{
			@Override
			public void run() {
				Log.i( LOG_TAG_SERVICE_CRUD, "createOrUpdateTasks:" + tasks.size() );
				taskDataBase.insertOrUpdateTasks( tasks, expandedSet );
				requestSynchronization();
			}
		} ).start();
	}

	@Override
	public void deleteTask(
		final String taskId ) {
		new Thread( new Runnable()
		{
			@Override
			public void run() {
				Log.i( LOG_TAG_SERVICE_CRUD, "deleteTask:" + taskId );
				taskDataBase.deleteTask( taskId );
				requestSynchronization();
			}
		} ).start();
	}

	@Override
	public void deleteTasks(
		final Collection<String> taskIds ) {
		new Thread( new Runnable()
		{
			@Override
			public void run() {
				Log.i( LOG_TAG_SERVICE_CRUD, "deleteTasks:" + taskIds.size() );
				taskDataBase.deleteTasks( taskIds );
				requestSynchronization();
			}
		} ).start();
	}

	@Override
	public void forceSynchronization(
		final UserSession userSession ) {
		new Thread( new Runnable()
		{
			@Override
			public void run() {
				synchronizeWithDB( userSession );
			}
		} ).start();
	}

	public SimpleJsonClient getSync1Client(
		String endpointValue ) {
		return new SimpleJsonClient().resource( endpointValue ).path( "resources/syncing1" );
	}

	public SimpleJsonClient getSync2Client(
		String endpointValue ) {
		return new SimpleJsonClient().resource( endpointValue ).path( "resources/syncing2" );
	}

	public UserSession getUserSession() {
		return userSession;
	}

	@Override
	public IBinder onBind(
		Intent intent ) {
		Log.i( LOG_TAG_SERVICE, "Service bind requested." );
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		prefs = PreferenceManager.getDefaultSharedPreferences( getApplicationContext() );

		long lastSyncDateLong = prefs.getLong( PROPERTY_LAST_SYNC_DATE, -1 );
		if ( lastSyncDateLong != -1 )
		{
			lastSyncDate = new Date( lastSyncDateLong );
		}

		taskDataBase = new TaskDB( getApplicationContext() );
		taskDataBase.open();

		binder = new DataAccessBinder( this );
		listeners = new HashSet<DataAccessService.DataAccessServiceListener>();

		userSyncRequested = false;
		syncInQueue = false;

		Log.i( LOG_TAG_SERVICE, "Service created." );
	}

	@Override
	public void onDestroy() {
		Log.i( LOG_TAG_SERVICE, "Destroying service…" );
		super.onDestroy();
		taskDataBase.close();
		listeners.clear();
		Log.i( LOG_TAG_SERVICE, "Service destroyed." );
	}

	@Override
	public int onStartCommand(
		Intent intent,
		int flags,
		int startId ) {

		if ( intent != null && intent.hasExtra( PROPERTY_ONLY_SYNC )
			&& intent.getExtras().getBoolean( PROPERTY_ONLY_SYNC ) && intent.hasExtra( PROPERTY_USER_SESSION ) )
		{
			setUserSession( (UserSession) intent.getExtras().getSerializable( PROPERTY_USER_SESSION ) );
			requestSynchronization();
		}

		return super.onStartCommand( intent, flags, startId );
	}

	@Override
	public boolean onUnbind(
		Intent intent ) {
		Log.i( LOG_TAG_THREAD, "Unbind requested." );

		return false;
	}

	@Override
	public void registerListener(
		DataAccessServiceListener listener ) {
		listeners.add( listener );
	}

	@Override
	public void requestAllTasks() {
		new Thread( new Runnable()
		{
			@Override
			public void run() {
				taskDataBase.readTasksInfo();
				fireAllTasksRetrieved( Collections.unmodifiableSet( taskDataBase.getTasks() ),
					Collections.unmodifiableSet( taskDataBase.getExpandedSet() ) );
			}
		} ).start();
	}

	@Override
	public void requestSynchronization() {
		Log.i( LOG_TAG_SERVICE, "Sync requested." );

		if ( userSession != null )
		{
			if ( !isBusy() )
			{
				Log.i( LOG_TAG_SERVICE, "Sync request accepted. Waiting for " + DELAY_USER + " seconds to post." );
				userSyncRequested = true;

				new Timer().schedule( new TimerTask()
				{
					@Override
					public void run() {
						postSynchronizationRequested( userSession );
						userSyncRequested = false;
					}
				}, DELAY_USER * 1000 );
			}
			else
			{
				Log.i( LOG_TAG_SERVICE, "Sync request skipped. Reason: Sync already in queue." );
			}
		}
		else
		{
			Log.e( LOG_TAG_SERVICE, "Sync request skipped. Reason: user session is missing." );
		}
	}

	public void setUserSession(
		UserSession userSession ) {
		this.userSession = userSession;
	}

	@Override
	public void unregisterListener(
		DataAccessServiceListener listener ) {
		listeners.remove( listener );

		if ( listeners.isEmpty() )
		{
			Log.i( LOG_TAG_SERVICE, "No more listeners." );
			if ( !isBusy() )
			{
				Log.i( LOG_TAG_SERVICE, "No more task in queue => Hara-kiri planned." );
				stopSelf();
			}
			else
			{
				Log.i( LOG_TAG_SERVICE, "Task in queue => waiting to finish job." );
			}
		}
	}

	private void fireAllTasksRetrieved(
		Set<WS_Task> tasks,
		Set<String> expandedSet ) {
		Log.i( LOG_TAG_SERVICE, "Tasks retrieved!" );
		for ( DataAccessServiceListener listener : listeners )
		{
			listener.allTasksRetrieved( tasks, expandedSet );
		}
	}

	private void fireSyncFinalized() {
		Log.i( LOG_TAG_SERVICE, "Synchronization successful!" );
		for ( DataAccessServiceListener listener : listeners )
		{
			listener.syncFinalized();
		}
	}

	private void fireSyncStarted() {
		Log.i( LOG_TAG_SERVICE, "Synchronization started..." );
		for ( DataAccessServiceListener listener : listeners )
		{
			listener.syncStarted();
		}
	}

	private boolean isBusy() {
		return syncInQueue || userSyncRequested;
	}

	private void planSyncCheckIn(
		long seconds ) {
		new Timer().schedule( new TimerTask()
		{
			@Override
			public void run() {
				if ( syncInQueue )
				{
					synchronizeWithDB( userSession );
					lastSyncDate = new Date();
					prefs.edit().putLong( PROPERTY_LAST_SYNC_DATE, lastSyncDate.getTime() ).commit();
					syncInQueue = false;
				}
				if ( listeners.isEmpty() )
				{
					stopSelf();
				}
			}
		}, seconds * 1000 );
	}

	private void postSynchronizationRequested(
		final UserSession userSession ) {
		Log.i( LOG_TAG_SERVICE, "Sync request posted." );

		if ( !syncInQueue )
		{
			int userSynctime = prefs.getInt( getString( R.string.synctime ), 10 );

			if ( lastSyncDate != null && new Date().getTime() - lastSyncDate.getTime() < userSynctime * 60 * 1000 )
			{
				Log.i( LOG_TAG_SERVICE, "Sync request post accepted. Sync placed in queue to respect a " + userSynctime
					+ " minutes delay." );

				syncInQueue = true;
				planSyncCheckIn( lastSyncDate.getTime() / 1000 + userSynctime * 60 - System.currentTimeMillis() / 1000 );
			}
			else
			{
				Log.i( LOG_TAG_SERVICE, "Sync request post accepted. Sync will launch immediately." );
				synchronizeWithDB( userSession );

				lastSyncDate = new Date();
				prefs.edit().putLong( PROPERTY_LAST_SYNC_DATE, lastSyncDate.getTime() ).commit();

				if ( listeners.isEmpty() )
				{
					stopSelf();
				}
			}
		}
		else
		{
			Log.i( LOG_TAG_SERVICE, "Sync request post skipped. Reason: Sync already in queue." );
		}
	}

	private void synchronizeWithDB(
		final UserSession userSession ) {
		fireSyncStarted();

		// Reading local base
		taskDataBase.readTasksInfo();

		// Retrieving endpoint adress from user parameters
		String endPointValue = prefs.getString( getString( R.string.endpoint ),
			TreeTaskerControllerDAO.DEFAULT_WEB_RESOURCE );

		// Initializing sync request
		SyncingStartRequest request = new SyncingStartRequest( userSession, TT_UserTaskContainer.DEFAULT_NAME );

		// Local vars
		HashMap<String, WS_Task> wsTasksMap = new HashMap<String, WS_Task>();
		HashSet<String> expandedSet = new HashSet<String>( taskDataBase.getExpandedSet() );

		// Adding local tasks ids to request
		for ( WS_Task task : taskDataBase.getTasks() )
		{
			wsTasksMap.put( task.getId(), task.update( new WS_Task() ) );
			request.addId( new TaskStamp( task.getId(), task.getLastModificationDate() ) );
		}

		// Adding locally deleted ids to request
		for ( WS_Task task : taskDataBase.getDeletedTasks() )
		{
			request.addRemovedId( task.getId() );
		}

		try
		{
			// Requesting and receiving response from endpoint
			SyncingStartResponse response = getSync1Client( endPointValue ).post( SyncingStartResponse.class, request );

			// Erasing deleted tasks in endpoint
			for ( String deletedId : response.getDeletedIds() )
			{
				wsTasksMap.remove( deletedId );
				expandedSet.remove( deletedId );
			}

			// Flushing locally deleted tasks
			taskDataBase.clearDeletedTasks();

			// Adding new endpoint-side tasks
			for ( WS_Task taskToAdd : response.getTasksToAdd() )
			{
				wsTasksMap.put( taskToAdd.getId(), taskToAdd );
			}

			// Updating more recent tasks from endpoint
			for ( WS_Task wsTask : response.getMoreRecentTasks() )
			{
				wsTask.update( wsTasksMap.get( wsTask.getId() ) );
			}

			// Sending locally more recent tasks if there is
			if ( !response.getNeedUpdateIds().isEmpty() )
			{
				// Initializing final sync request
				SyncingFinalRequest finalRequest = new SyncingFinalRequest( userSession,
					TT_UserTaskContainer.DEFAULT_NAME );

				// Adding locally more recent tasks to the
				// request
				for ( String needUpdateId : response.getNeedUpdateIds() )
				{
					finalRequest.addUpToDateTask( wsTasksMap.get( needUpdateId ) );
				}

				// Requesting and receiving response from
				// endpoint
				SyncingFinalResponse finalResponse = getSync2Client( endPointValue ).post( SyncingFinalResponse.class,
					finalRequest );

				// The endpoint may have more recent
				// modifications to send back
				// Applying them
				for ( WS_Task wsTask : finalResponse.getUpToDateTasks() )
				{
					wsTask.update( wsTasksMap.get( wsTask.getId() ) );
				}
			}

			taskDataBase.reinit( wsTasksMap.values(), expandedSet );

			fireAllTasksRetrieved( new HashSet<WS_Task>( wsTasksMap.values() ), expandedSet );
		}
		catch ( IOException e )
		{
			Log.e( LOG_TAG_THREAD, "IOException", e );
		}
		catch ( E_BadResponseStatus e )
		{
			Log.e( LOG_TAG_THREAD, "Bad net response code", e );
		}

		fireSyncFinalized();
	}

	private DataAccessBinder					binder;
	private HashSet<DataAccessServiceListener>	listeners;
	private TaskDB								taskDataBase;
	private SharedPreferences					prefs;
	private boolean								userSyncRequested;
	private boolean								syncInQueue;
	private Date								lastSyncDate;
	private UserSession							userSession;
}
