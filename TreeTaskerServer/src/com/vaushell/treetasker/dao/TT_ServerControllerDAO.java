package com.vaushell.treetasker.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.vaushell.treetasker.model.TT_UserTaskContainer;

public class TT_ServerControllerDAO
{
	// PUBLIC
	/**
	 * Getter Singleton.
	 * 
	 * @return Singleton instance
	 */
	public static TT_ServerControllerDAO getInstance()
	{
		return INSTANCE;
	}

	/**
	 * Retourne le conteneur "default" pour un utilisateur. Si celui-ci n'existe
	 * pas déjà, il est créé.
	 * 
	 * @param user
	 *            L'utilisateur dont on veut le conteneur par défaut
	 * @return Le conteneur "default"
	 */
	public EH_TT_UserTaskContainer getUserContainer( String userId )
	{
		Query containerQuery = new Query(
		                                  EH_TT_UserTaskContainer.KIND,
		                                  KeyFactory.createKey( EH_User.KIND,
		                                                        userId ) );
		containerQuery.setFilter( new Query.FilterPredicate(
		                                                     EH_TT_UserTaskContainer.PROPERTY_NAME,
		                                                     FilterOperator.EQUAL,
		                                                     TT_UserTaskContainer.DEFAULT_NAME ) );

		Entity containerEntity = DATASTORE.prepare( containerQuery )
		                                  .asSingleEntity();

		if ( containerEntity == null )
		{
			containerEntity = new Entity(
			                              EH_TT_UserTaskContainer.KIND,
			                              UUID.randomUUID().toString(),
			                              KeyFactory.createKey( EH_User.KIND,
			                                                    userId ) );
			containerEntity.setProperty( EH_TT_UserTaskContainer.PROPERTY_NAME,
			                             TT_UserTaskContainer.DEFAULT_NAME );

			synchronized ( DATASTORE )
			{
				DATASTORE.put( containerEntity );
			}
		}

		return new EH_TT_UserTaskContainer( containerEntity );
	}

	/**
	 * Retourne la tâche à partir de son identifiant et de son conteneur.
	 * 
	 * @param userContainer
	 *            Le conteneur appartenant à l'utilisateur possesseur de la
	 *            tâche
	 * @param taskId
	 *            L'UUID de la tâche voulue
	 * @return La tâche si elle existe, null sinon.
	 */
	public EH_TT_Task getTask( EH_TT_UserTaskContainer userContainer,
	                           String taskId )
	{
		try
		{
			Entity entityTask = DATASTORE.get( KeyFactory.createKey( userContainer.getEntity()
			                                                                      .getKey(),
			                                                         EH_TT_Task.KIND,
			                                                         taskId ) );
			return new EH_TT_Task( entityTask );
		}
		catch ( EntityNotFoundException e )
		{
			return null;
		}
	}

	/**
	 * Retourne une liste des tâches pour le conteneur donné.
	 * 
	 * @param userContainer
	 *            Le conteneur duquel on veut la liste des tâches
	 * @return La liste des tâches
	 */
	public List<EH_TT_Task> getAllTasks( EH_TT_UserTaskContainer userContainer )
	{
		ArrayList<EH_TT_Task> tasksList = new ArrayList<EH_TT_Task>();

		Query tasksQuery = new Query( EH_TT_Task.KIND,
		                              userContainer.getEntity()
		                                           .getKey() );

		List<Entity> entityTasks = null;
		synchronized ( DATASTORE )
		{
			entityTasks = DATASTORE.prepare( tasksQuery )
			                       .asList( FetchOptions.Builder.withDefaults() );
		}

		for ( Entity taskEntity : entityTasks )
		{
			tasksList.add( new EH_TT_Task( taskEntity ) );
		}

		return tasksList;
	}

	public void createOrUpdateTask( EH_TT_Task task )
	{
		synchronized ( DATASTORE )
		{
			DATASTORE.put( task.getEntity() );
		}
	}

	public void createOrUpdateTasks( Collection<EH_TT_Task> tasks )
	{
		Transaction tx = DATASTORE.beginTransaction();

		try
		{
			synchronized ( DATASTORE )
			{
				for ( EH_TT_Task task : tasks )
				{
					DATASTORE.put( tx, task.getEntity() );
				}
			}
		}
		catch ( Throwable th )
		{
			tx.rollback();
		}

		tx.commit();
	}

	public boolean isTaskDeleted( EH_TT_UserTaskContainer userContainer,
	                              String taskId )
	{
		try
		{
			DATASTORE.get( KeyFactory.createKey( userContainer.getEntity()
			                                                  .getKey(),
			                                     EH_Deleted_Task.KIND,
			                                     taskId ) );

			return true;
		}
		catch ( EntityNotFoundException e )
		{
			return false;
		}
	}

	/**
	 * Supprime la tâche du serveur. Si elle existe, la tâche est supprimé et
	 * ajouté à la table des tâches supprimées. Sinon, il ne se passe rien.
	 * 
	 * @param task
	 *            Tâche à supprimer
	 */
	public void deleteTask( EH_TT_Task task )
	{
		Transaction tx = DATASTORE.beginTransaction();
		try
		{
			Entity taskEntity = DATASTORE.get( tx, task.getEntity()
			                                           .getKey() );

			synchronized ( DATASTORE )
			{
				DATASTORE.delete( tx, task.getEntity().getKey() );

				DATASTORE.put( tx, new Entity( EH_Deleted_Task.KIND,
				                               taskEntity.getKey().getName(),
				                               taskEntity.getParent() ) );
			}

			tx.commit();
		}
		catch ( EntityNotFoundException e )
		{
			tx.rollback();
		}
	}

	public void deleteTasks( Collection<EH_TT_Task> tasks )
	{
		for ( EH_TT_Task task : tasks )
		{
			Transaction tx = DATASTORE.beginTransaction();
			try
			{
				Entity taskEntity = DATASTORE.get( tx, task.getEntity()
				                                           .getKey() );

				synchronized ( DATASTORE )
				{
					DATASTORE.delete( tx, task.getEntity().getKey() );

					DATASTORE.put( tx, new Entity( EH_Deleted_Task.KIND,
					                               taskEntity.getKey()
					                                         .getName(),
					                               taskEntity.getParent() ) );
				}

				tx.commit();
			}
			catch ( EntityNotFoundException e )
			{
				tx.rollback();
			}
		}
	}

	// PROTECTED
	// PRIVATE
	private static final TT_ServerControllerDAO	INSTANCE	= new TT_ServerControllerDAO();
	private static final DatastoreService	    DATASTORE	= DatastoreServiceFactory.getDatastoreService();

	private TT_ServerControllerDAO()
	{
		init();
	}

	private void init()
	{

	}
}
