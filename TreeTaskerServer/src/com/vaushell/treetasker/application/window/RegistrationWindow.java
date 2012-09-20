package com.vaushell.treetasker.application.window;

import com.vaadin.ui.Window;
import com.vaushell.treetasker.application.ui.I_Form;
import com.vaushell.treetasker.application.ui.RegistrationLayout;
import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.module.UserSession;
import com.vaushell.treetasker.tools.TT_Tools;

/**
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class RegistrationWindow extends Window implements I_Form {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegistrationWindow() {
		super("Enregistrement");
		init();
	}

	public void ok() {
		if (content.isValid()) {
			String userName = content.getUserName();
			String encryptedPassword = TT_Tools.encryptPassword(userName,
					content.getPassword());
			UserSession session = TT_ServerControllerDAO.getInstance()
					.registerUser(userName, encryptedPassword);
			if (session.isValid()) {
				Notification n = new Notification("Enregistrement réussi",
						"Un mail a été envoyé à " + userName
								+ " afin de valider votre enregistrement");
				n.setDelayMsec(Notification.DELAY_FOREVER);
				getParent().showNotification(n);
				close();
			} else {
				showNotification(userName + " est déjà utilisé.",
						Notification.TYPE_ERROR_MESSAGE);
			}
		} else {
			showNotification(content.getErrorMsg(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cancel() {
		close();
	}

	private RegistrationLayout content;

	private void init() {
		this.content = new RegistrationLayout(this);
		setContent(this.content);
		setModal(true);
	}

}
