package com.vaushell.treetasker.application.window;

import com.vaadin.ui.Window;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import com.vaushell.treetasker.application.ui.I_Form;
import com.vaushell.treetasker.application.ui.RegistrationLayout;

/**
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class RegistrationWindow extends Window implements I_Form {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RegistrationWindow(TreeTaskerWebApplicationController controller) {
		super("Enregistrement");
		this.controller = controller;
		init();
	}

	public void ok() {
		if (content.isValid()) {
			showNotification("Enregistrement OK");
		}
		else {
			showNotification(content.getErrorMsg(),
					Notification.TYPE_ERROR_MESSAGE);
		}
	}

	public void cancel() {
		close();
	}

	private RegistrationLayout content;
	private TreeTaskerWebApplicationController controller;

	private void init() {
		this.content = new RegistrationLayout(this);
		setContent(this.content);
		setModal(true);
	}

}
