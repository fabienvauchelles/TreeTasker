package com.vaushell.treetasker.resources;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.UUID;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.vaushell.treetasker.dao.EH_RegisterValidationPending;
import com.vaushell.treetasker.dao.EH_User;
import com.vaushell.treetasker.module.UserAuthenticationRequest;
import com.vaushell.treetasker.module.UserSession;

@Path( "/register" )
public class RegisterResource
{
	public static final DatastoreService	datastore	= DatastoreServiceFactory.getDatastoreService();

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	@Produces( MediaType.APPLICATION_JSON )
	public UserSession register( UserAuthenticationRequest request )
	{
		try
		{
			datastore.get( KeyFactory.createKey( EH_User.KIND,
			                                     request.getLogin() ) );
		}
		catch ( EntityNotFoundException e )
		{
			EH_User newUser = new EH_User( request.getLogin(),
			                               request.getPassword() );
			datastore.put( newUser.getEntity() );

			EH_RegisterValidationPending rvp = new EH_RegisterValidationPending(
			                                                                     request.getLogin(),
			                                                                     UUID.randomUUID()
			                                                                         .toString() );
			datastore.put( rvp.getEntity() );

			// Mail
			Properties props = new Properties();
			Session session = Session.getDefaultInstance( props, null );

			String msgBody = "Bonjour,\n\n"
			                 +
			                 "Vous recevez ce message car vous vous êtes inscrit sur TreeTasker.\n"
			                 +
			                 "Afin de valider votre inscription, veuillez vous rendre à l'adresse suivante : http://vsh2-test.appspot.com/resources/valid?username="
			                 + request.getLogin()
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
				                  new InternetAddress( request.getLogin(),
				                                       "User "
				                                           + request.getLogin() ) );
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

			return new UserSession( request.getLogin(), UUID.randomUUID()
			                                                .toString() );
		}

		UserSession session = new UserSession();
		session.setSessionMessage( UserSession.MESSAGE_BAD_AUTHENTICATION );
		return session;
	}
}
