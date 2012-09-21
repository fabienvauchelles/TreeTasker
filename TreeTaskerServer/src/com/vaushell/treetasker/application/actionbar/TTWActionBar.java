/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.actionbar;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import java.util.Collection;

import org.vaadin.dialogs.ConfirmDialog;

/**
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TTWActionBar extends HorizontalLayout {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// PUBLIC
	public static final String ACTION_BAR_BUTTON_STYLE = "ttw-action-bar";

	public TTWActionBar(TreeTaskerWebApplicationController controller) {
		this.controller = controller;
		init();
	}

	public void setLeftButtons(Collection<Button> buttons) {
		for (Button button : buttons) {
			addLeftButton(button);
		}
	}

	public void addLeftButton(Button button) {
		leftLayout.addComponent(button);
	}

	public void addLeftButton(Button button, int index) {
		leftLayout.addComponent(button, index);
	}

	public void setRightButtons(Collection<Button> buttons) {
		for (Button button : buttons) {
			addRightButton(button);
		}
	}

	public void addRightButton(Button button) {
		rightLayout.addComponent(button);
		rightLayout.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
	}

	public void addRightButton(Button button, int index) {
		rightLayout.addComponent(button, index);
		rightLayout.setComponentAlignment(button, Alignment.MIDDLE_RIGHT);
	}

	public void clear() {
		leftLayout.removeAllComponents();
		rightLayout.removeAllComponents();
	}

	// PROTECTED
	// PRIVATE
	private HorizontalLayout leftLayout;
	private HorizontalLayout rightLayout;
	private TreeTaskerWebApplicationController controller;

	private void init() {
		setWidth("100%");
		this.leftLayout = new HorizontalLayout();
		leftLayout.setSpacing(true);
		leftLayout.setHeight("100%");
		this.rightLayout = new HorizontalLayout();
		rightLayout.setSpacing(true);
		rightLayout.setSizeFull();
		this.addComponent(leftLayout);
		this.addComponent(rightLayout);
		setStyleName(ACTION_BAR_BUTTON_STYLE);
		setMargin(true, true, false, true);

		Button vBTaddTask = new Button("Ajouter une tâche",
				new Button.ClickListener() {
					/**
											 * 
											 */
					private static final long serialVersionUID = 1L;

					public void buttonClick(ClickEvent event) {
						controller.addNewTask();
					}
				});
		vBTaddTask.setImmediate(true);
		addLeftButton(vBTaddTask);

		Button vBTaddSubTask = new Button("Ajouter une sous-tâche",
				new Button.ClickListener() {
					/**
											 * 
											 */
					private static final long serialVersionUID = 1L;

					public void buttonClick(ClickEvent event) {
						controller.addNewSubTask();
					}
				});
		vBTaddSubTask.setImmediate(true);
		addLeftButton(vBTaddSubTask);

		Button vBTdeleteTask = new Button("Supprimer",
				new Button.ClickListener() {
					/**
											 * 
											 */
					private static final long serialVersionUID = 1L;

					public void buttonClick(ClickEvent event) {
						// Popup confirmation
						ConfirmDialog
								.show(controller.getApplication()
										.getMainWindow(),
										"Confirmation",
										"Êtes-vous sûr de vouloir définitivement les tâches sélectionnés ?",
										"Supprimer", "Annuler",
										new ConfirmDialog.Listener() {

											/**
															 * 
															 */
											private static final long serialVersionUID = 1L;

											@Override
											public void onClose(
													ConfirmDialog dialog) {
												if (dialog.isConfirmed()) {
													controller.deleteTasks();
												}
											}
										});
					}
				});
		vBTdeleteTask.setImmediate(true);
		addLeftButton(vBTdeleteTask);

		Button vBTcopyTask = new Button("Copier", new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				controller.copyTask();
			}
		});
		vBTcopyTask.setImmediate(true);
		addLeftButton(vBTcopyTask);

		Button vBTpasteTask = new Button("Coller", new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				controller.pasteTask();
			}
		});
		vBTpasteTask.setImmediate(true);
		addLeftButton(vBTpasteTask);

		Button vBTrefresh = new Button("Actualiser",
				new Button.ClickListener() {
					/**
											 * 
											 */
					private static final long serialVersionUID = 1L;

					public void buttonClick(ClickEvent event) {
						controller.refresh();
					}
				});
		vBTrefresh.setImmediate(true);
		addLeftButton(vBTrefresh);

		Button vBTvalidate = new Button("Valider", new Button.ClickListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				controller.validTask();
			}
		});
		vBTvalidate.setImmediate(true);
		addLeftButton(vBTvalidate);
	}
}
