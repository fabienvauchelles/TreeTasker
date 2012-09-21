/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.Transferable;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.client.ui.dd.VerticalDropLocation;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.TreeTargetDetails;
import com.vaadin.ui.VerticalLayout;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import com.vaushell.treetasker.application.tree.node.A_NavigationNode;
import com.vaushell.treetasker.application.tree.node.TaskNode;
import com.vaushell.treetasker.model.TT_Task;

/**
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TTWtree extends VerticalLayout implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// PUBLIC
	public TTWtree(TreeTaskerWebApplicationController controller) {
		this.controller = controller;
		init();
	}

	/**
	 * Ajoute un noeud à la racine
	 * 
	 * @param node
	 */
	public void addNode(A_NavigationNode node) {
		navigationTree.addItem(node);
		refreshNodeCaption(node);
		refreshNodeIcon((TaskNode) node);
	}

	public void refreshNodeCaption(A_NavigationNode node) {
		if (navigationTree.containsId(node)) {
			navigationTree.setItemCaption(node, node.getCaption());
		}
	}

	public void refreshNodeIcon(TaskNode node) {
		if (navigationTree.containsId(node)) {
			if (node.getTask().getStatus() == TT_Task.DONE) {
				navigationTree.setItemIcon(node, new ThemeResource(
						"icons/tick.png"));
			} else {
				navigationTree.setItemIcon(node, new ThemeResource(
						"icons/bullet.png"));
			}
		}
	}

	public void unselectNode() {
		if (currentNode != null) {
			currentNode.onExit();
			navigationTree.unselect(currentNode);
			currentNode = null;
		}
	}

	/**
	 * Ajoute childNode à parentNode. parentNode doit être contenu dans le
	 * menu.
	 * 
	 * @param childNode
	 * @param parentNode
	 */
	public void addNode(A_NavigationNode childNode, A_NavigationNode parentNode) {
		addNode(childNode);
		navigationTree.setParent(childNode, parentNode);
	}

	public void removeNode(A_NavigationNode node) {
		if (navigationTree.containsId(node)) {
			navigationTree.removeItem(node);
		}
	}

	public void moveAfterSiblingNode(A_NavigationNode node,
			A_NavigationNode siblingNode) {
		((HierarchicalContainer) navigationTree.getContainerDataSource())
				.moveAfterSibling(node, siblingNode);
	}

	public void expandNode(A_NavigationNode node) {
		navigationTree.expandItem(node);
	}

	public void expandNodeRecursively(A_NavigationNode node) {
		navigationTree.expandItemsRecursively(node);
	}

	@SuppressWarnings("unchecked")
	public void removeNodeRecursively(A_NavigationNode node) {
		if (node != null) {
			Collection<A_NavigationNode> childNodes = new ArrayList<A_NavigationNode>(
					(Collection<A_NavigationNode>) navigationTree
							.getChildren(node));
			for (A_NavigationNode childNode : childNodes) {
				removeNodeRecursively(childNode);
			}
			removeNode(node);
		}
	}

	public A_NavigationNode getParent(A_NavigationNode node) {
		return (A_NavigationNode) navigationTree.getParent(node);
	}

	@SuppressWarnings("unchecked")
	public Collection<A_NavigationNode> getChildren(A_NavigationNode node) {
		return (Collection<A_NavigationNode>) navigationTree.getChildren(node);
	}

	public A_NavigationNode getCurrentNode() {
		return currentNode;
	}

	public void select(Object itemId) {
		select(itemId, true);
	}

	public void select(Object itemId, boolean triggerListeners) {
		if (triggerListeners) {
			navigationTree.select(itemId);
		} else {
			navigationTree.removeListener(changeListener);
			navigationTree.select(itemId);
			navigationTree.addListener(changeListener);
		}
	}

	public void removeAllNodes() {
		navigationTree.removeAllItems();
		currentNode = null;
	}

	public Set<?> getValue() {
		return (Set<?>) navigationTree.getValue();
	}

	// </editor-fold>
	// PROTECTED
	protected TreeTaskerWebApplicationController controller;
	protected Tree navigationTree;
	// PRIVATE
	private Property.ValueChangeListener changeListener;
	private A_NavigationNode currentNode;

	private void init() {
		this.navigationTree = new Tree();
		HierarchicalContainer container = new HierarchicalContainer();
		this.navigationTree.setContainerDataSource(container);
		this.navigationTree.setDragMode(Tree.TreeDragMode.NODE);
		this.navigationTree.setDropHandler(new TreeSortDropHandler(
				navigationTree, controller));
		this.navigationTree.setMultiSelect(true);
		this.currentNode = null;

		setSizeUndefined();
		addComponent(navigationTree);

		initListeners();
	}

	@SuppressWarnings("unchecked")
	protected void initListeners() {
		this.changeListener = new Property.ValueChangeListener() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void valueChange(ValueChangeEvent event) {
				Set<A_NavigationNode> values = (Set<A_NavigationNode>) event
						.getProperty().getValue();

				if (values.size() == 1) {
					A_NavigationNode node = values.iterator().next();

					if (node != currentNode) {
						if (currentNode != null) {
							currentNode.onExit();
						}
						currentNode = node;
						node.onEnter();
					}
				} else if (values.isEmpty()) {
					if (currentNode != null) {
						currentNode.onExit();
					}
					currentNode = null;
				}
			}
		};
		navigationTree.addListener(changeListener);
		this.navigationTree.setImmediate(true);
	}

	private static class TreeSortDropHandler implements DropHandler {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final Tree tree;
		private final TreeTaskerWebApplicationController controller;

		public TreeSortDropHandler(Tree tree,
				TreeTaskerWebApplicationController controller) {
			this.tree = tree;
			this.controller = controller;
		}

		public void drop(DragAndDropEvent dropEvent) {
			// Make sure the drag source is the same tree
			Transferable t = dropEvent.getTransferable();

			if (t.getSourceComponent() != tree
					|| !(t instanceof DataBoundTransferable)) {
				return;
			}

			TreeTargetDetails dropData = ((TreeTargetDetails) dropEvent
					.getTargetDetails());

			Object sourceItemId = ((DataBoundTransferable) t).getItemId();
			Object targetItemId = dropData.getItemIdOver();

			// Location describes on which part of the node the drop took
			// place
			VerticalDropLocation location = dropData.getDropLocation();

			moveNode(sourceItemId, targetItemId, location);

		}

		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}

		private void moveNode(Object sourceItemId, Object targetItemId,
				VerticalDropLocation location) {
			HierarchicalContainer container = (HierarchicalContainer) tree
					.getContainerDataSource();

			// Sorting goes as
			// - If dropped ON a node, we append it as a child
			// - If dropped on the TOP part of a node, we move/add it before
			// the node
			// - If dropped on the BOTTOM part of a node, we move/add it
			// after the node

			if (location == VerticalDropLocation.MIDDLE) {
				if (container.setParent(sourceItemId, targetItemId)
						&& container.hasChildren(targetItemId)) {
					// move first in the container
					container.moveAfterSibling(sourceItemId, null);
					controller.setTaskParent(
							((TaskNode) sourceItemId).getTask(),
							((TaskNode) targetItemId).getTask());
				}
				tree.expandItem(targetItemId);
			} else if (location == VerticalDropLocation.TOP) {
				Object parentId = container.getParent(targetItemId);
				if (container.setParent(sourceItemId, parentId)) {
					// reorder only the two items, moving source above target
					container.moveAfterSibling(sourceItemId, targetItemId);
					container.moveAfterSibling(targetItemId, sourceItemId);
					if (parentId == null) {
						controller.setTaskParent(
								((TaskNode) sourceItemId).getTask(), null);
					} else {
						controller.setTaskParent(
								((TaskNode) sourceItemId).getTask(),
								((TaskNode) parentId).getTask());
					}
				}
			} else if (location == VerticalDropLocation.BOTTOM) {
				Object parentId = container.getParent(targetItemId);
				if (container.setParent(sourceItemId, parentId)) {
					container.moveAfterSibling(sourceItemId, targetItemId);
					if (parentId == null) {
						controller.setTaskParent(
								((TaskNode) sourceItemId).getTask(), null);
					} else {
						controller.setTaskParent(
								((TaskNode) sourceItemId).getTask(),
								((TaskNode) parentId).getTask());
					}
				}
			}
		}
	}
}
