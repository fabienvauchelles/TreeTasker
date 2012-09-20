/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import com.vaushell.treetasker.TreeTaskerWebApplication;
import com.vaushell.treetasker.application.actionbar.TTWActionBar;
import com.vaushell.treetasker.application.content.TTWcontent;
import com.vaushell.treetasker.application.header.TTWHeader;
import com.vaushell.treetasker.application.tree.TTWtree;
import com.vaushell.treetasker.application.tree.node.TaskNode;
import com.vaushell.treetasker.application.window.RegistrationWindow;
import com.vaushell.treetasker.application.window.UserWindow;
import com.vaushell.treetasker.dao.TT_ServerControllerDAO;
import com.vaushell.treetasker.model.TT_Task;
import com.vaushell.treetasker.module.UserSession;
import com.vaushell.treetasker.tools.TT_Tools;

/**
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TreeTaskerWebApplicationController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// PUBLIC
	public TreeTaskerWebApplicationController(
			TreeTaskerWebApplication application) {
		this.application = application;
		init();
	}

	public TreeTaskerWebApplication getApplication() {
		return application;
	}

	public void addNewTask() {
		TaskNode newNode = new TaskNode(new TT_Task(UUID.randomUUID()
				.toString(), "Nouvelle t�che", null, new Date(), TT_Task.TODO),
				this);
		TaskNode siblingNode = (TaskNode) getTree().getCurrentNode();
		TaskNode parentNode = null;
		if (siblingNode != null) {
			parentNode = (TaskNode) getTree().getParent(siblingNode);
		}

		if (parentNode != null) {
			getTree().addNode(newNode, parentNode);
			getTree().moveAfterSiblingNode(newNode, siblingNode);
			newNode.getTask().setParent(parentNode.getTask());
		} else {
			getTree().addNode(newNode);
			if (siblingNode != null) {
				getTree().moveAfterSiblingNode(newNode, siblingNode);
			}
		}
	}

	public void addNewSubTask() {
		TaskNode newNode = new TaskNode(new TT_Task(UUID.randomUUID()
				.toString(), "Nouvelle t�che", null, new Date(), TT_Task.TODO),
				this);
		TaskNode parentNode = (TaskNode) getTree().getCurrentNode();

		if (parentNode != null) {
			getTree().addNode(newNode, parentNode);
			newNode.getTask().setParent(parentNode.getTask());
			getTree().expandNode(parentNode);
		} else {
			getTree().addNode(newNode);
		}
	}

	@SuppressWarnings("unchecked")
	public void validTask() {
		int newStatus = TT_Task.TODO;

		for (TaskNode node : (Set<TaskNode>) getTree().getValue()) {
			TT_Task selectedTask = node.getTask();
			if (selectedTask.getStatus() == TT_Task.TODO) {
				newStatus = TT_Task.DONE;
			}
		}
		for (TaskNode node : (Set<TaskNode>) getTree().getValue()) {
			TT_Task selectedTask = node.getTask();
			selectedTask.setStatus(newStatus);
			getTree().refreshNodeIcon(node);
		}
	}

	public void refresh() {
		// TODO Auto-generated method stub

	}

	public void pasteTask() {
		// TODO Auto-generated method stub

	}

	public void copyTask() {
		// TODO Auto-generated method stub

	}

	public void deleteTask() {
		// TODO Auto-generated method stub

	}

	public boolean login(String userName, String password) {
		if (!"".equals(userName) && password != null) {
			this.userSession = TT_ServerControllerDAO.getInstance()
					.authenticateUser(userName,
							TT_Tools.encryptPassword(userName, password));
			return userSession.isValid();
		}
		return false;
	}

	public void showUserWindow() {
		UserWindow userWindow = getUserWindow();
		userWindow.setUserView(userSession.getUserName());
		getHeader().setUserView();
	}

	public void showLoginWindow() {
		UserWindow userWindow = getUserWindow();
		application.setMainWindow(userWindow);
	}

	public UserWindow getUserWindow() {
		if (userWindow == null) {
			userWindow = new UserWindow(this);
		}
		return userWindow;
	}

	public TTWActionBar getActionBar() {
		if (actionBar == null) {
			actionBar = new TTWActionBar(this);
		}
		return actionBar;
	}

	public TTWcontent getContent() {
		if (content == null) {
			content = new TTWcontent();
		}
		return content;
	}

	public TTWHeader getHeader() {
		if (header == null) {
			header = new TTWHeader(this);
		}
		return header;
	}

	public TTWtree getTree() {
		if (tree == null) {
			tree = new TTWtree(this);
		}
		return tree;
	}

	public void queryRegistration() {
		RegistrationWindow subWindow = new RegistrationWindow();
		application.getMainWindow().addWindow(subWindow);
		subWindow.center();
	}

	// PROTECTED
	// PRIVATE
	private TreeTaskerWebApplication application;
	private UserWindow userWindow;
	private TTWActionBar actionBar;
	private TTWHeader header;
	private TTWtree tree;
	private TTWcontent content;
	private UserSession userSession;

	private void init() {
	}

}
