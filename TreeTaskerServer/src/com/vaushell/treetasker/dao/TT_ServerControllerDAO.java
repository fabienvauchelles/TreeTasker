/*******************************************************************************
 * Copyright (c) 2012 - VAUSHELL - contact@vaushell.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package com.vaushell.treetasker.dao;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
import com.vaushell.treetasker.module.UserSession;

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

	public UserSession registerUser( String username,
	                                 String passwordHash )
	{
		EH_User alreadyExistingUser = null;
		try
		{
			alreadyExistingUser = new EH_User(
			                                   DATASTORE.get( KeyFactory.createKey( EH_User.KIND,
			                                                                        username ) ) );
		}
		catch ( EntityNotFoundException e )
		{
			EH_User newUser = new EH_User( username,
			                               passwordHash );
			DATASTORE.put( newUser.getEntity() );

			EH_RegisterValidationPending rvp = new EH_RegisterValidationPending(
			                                                                     username,
			                                                                     UUID.randomUUID()
			                                                                         .toString() );
			DATASTORE.put( rvp.getEntity() );

			// Mail
			Properties props = new Properties();
			Session session = Session.getDefaultInstance( props, null );

			String msgBody = "Bonjour,\n\n"
			                 +
			                 "Vous recevez ce message car vous vous êtes inscrit sur TreeTasker.\n"
			                 +
			                 "Afin de valider votre inscription, veuillez vous rendre à l'adresse suivante : http://vsh2-test.appspot.com/resources/valid?username="
			                 + username
			                 + "&valid-key="
			                 + rvp.getValidKey()
			                 + "\n\nUne fois votre adresse validée, vous pourrez vous connecter depuis votre mobile grâce à l'application TreeTasker."
			                 +
			                 "\n\n(Vous avez 48 heures pour valider votre inscription. Passé ce délai, vous devrez vous réinscrire.)\n\n"
			                 +
			                 "Toute l'équipe de TreeTasker vous remercie !";

			try
			{
				Message msg = new MimeMessage( session );
				msg.setFrom( new InternetAddress(
				                                  "donotreply@vsh2-test.appspotmail.com",
				                                  "TreeTasker Registration Mail" ) );
				msg.addRecipient( Message.RecipientType.TO,
				                  new InternetAddress( username,
				                                       "User "
				                                           + username ) );
				msg.setSubject( "Mail d'activation de compte TreeTasker" );
				msg.setText( msgBody );
				Transport.send( msg );
			}
			catch ( UnsupportedEncodingException ex )
			{

			}
			catch ( AddressException ex )
			{
				// ...
			}
			catch ( MessagingException ex )
			{
				// ...
			}

			return new UserSession( username, UUID.randomUUID()
			                                      .toString() );
		}

		if ( alreadyExistingUser.isValidatedUser() )
		{
			UserSession session = new UserSession();
			session.setSessionMessage( UserSession.MESSAGE_USER_ALREADY_EXISTS );
			return session;
		}
		else
		{
			UserSession session = new UserSession();
			session.setSessionMessage( UserSession.MESSAGE_REGISTRATION_NOT_VALIDATED );
			return session;
		}
	}

	/**
	 * Try to authenticate the user identity. If authenticated, put a session in
	 * datastore with a UUID and returns it. Otherwise, returns a session with a
	 * "bad authentication" message.
	 * 
	 * @param username
	 *            ID of the user
	 * @param passwordHash
	 *            Password hash of the user
	 * @return A session indicating whether authenticated or not
	 */
	public UserSession authenticateUser( String username,
	                                     String passwordHash )
	{
		try
		{
			EH_User user = new EH_User(
			                            DATASTORE.get( KeyFactory.createKey( EH_User.KIND,
			                                                                 username ) ) );

			if ( user.isValidatedUser() )
			{
				if ( user.getPassword().equals( passwordHash ) )
				{
					UserSession userSession = new UserSession(
					                                           user.getLogin(),
					                                           UUID.randomUUID()
					                                               .toString() );
					Entity datastoreUserSession = new Entity(
					                                          "UserSession",
					                                          userSession.getUserSessionID() );
					datastoreUserSession.setProperty( "username",
					                                  userSession.getUserName() );

					synchronized ( DATASTORE )
					{
						DATASTORE.put( datastoreUserSession );
					}

					return userSession;
				}
			}
			else
			{
				UserSession session = new UserSession();
				session.setSessionMessage( UserSession.MESSAGE_BAD_AUTHENTICATION );
				return session;
			}
		}
		catch ( EntityNotFoundException e )
		{
			// Do nothing
		}

		UserSession session = new UserSession();
		session.setSessionMessage( UserSession.MESSAGE_BAD_AUTHENTICATION );
		return session;
	}

	public boolean checkUserSession( UserSession userSession )
	{
		try
		{
			Entity datastoreUserSession = DATASTORE.get( KeyFactory.createKey( "UserSession",
			                                                                   userSession.getUserSessionID() ) );

			if ( userSession.getUserName() != null
			     && userSession.getUserName()
			                   .equals( datastoreUserSession.getProperty( "username" ) ) )
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch ( EntityNotFoundException e )
		{
			return false;
		}
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
		catch ( RuntimeException ex )
		{
			tx.rollback();
			throw ex;
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
