/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.DataBoundTransferable;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.MouseEvents.ClickEvent;
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
import com.vaushell.treetasker.model.TT_Task;

/**
 * This is the navigation tree that displays the task list.
 * 
 * The tree node can be dragged and dropped on the menu.
 * 
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TTWtree
	extends VerticalLayout
	implements Serializable
{
	/**
	 * Handles the drag and drop behavior
	 * 
	 */
	private static class TreeSortDropHandler
		implements DropHandler
	{
		private static final long	serialVersionUID	= 1L;

		public TreeSortDropHandler(
			Tree tree,
			TreeTaskerWebApplicationController controller )
		{
			this.tree = tree;
			this.controller = controller;
		}

		@Override
		public void drop(
			DragAndDropEvent dropEvent ) {
			// Make sure the drag source is the same tree
			Transferable t = dropEvent.getTransferable();

			if ( t.getSourceComponent() != tree || !( t instanceof DataBoundTransferable ) )
			{
				return;
			}

			TreeTargetDetails dropData = (TreeTargetDetails) dropEvent.getTargetDetails();

			Object sourceItemId = ( (DataBoundTransferable) t ).getItemId();
			Object targetItemId = dropData.getItemIdOver();

			// Location describes on which part of the node the drop took
			// place
			VerticalDropLocation location = dropData.getDropLocation();

			moveNode( sourceItemId, targetItemId, location );

		}

		@Override
		public AcceptCriterion getAcceptCriterion() {
			return AcceptAll.get();
		}

		@SuppressWarnings( "unchecked" )
		private void moveNode(
			Object sourceItemId,
			Object targetItemId,
			VerticalDropLocation location ) {
			HierarchicalContainer container = (HierarchicalContainer) tree.getContainerDataSource();

			// Dans un premier temps, on retire le n�ud
			TaskNode taskNode = (TaskNode) sourceItemId;
			// if ( taskNode.getTask().getParent() != null )
			// {
			// int taskIndex =
			// taskNode.getTask().getParent().getChildrenTask().indexOf(
			// taskNode.getTask() );
			// if ( taskIndex !=
			// taskNode.getTask().getParent().getChildrenTask().size() - 1 )
			// {
			// taskNode.getTask().getParent().getChildrenTask().get( taskIndex +
			// 1 )
			// .setPreviousTask( taskNode.getTask().getPreviousTask() );
			// tasksToUpdate.add(
			// taskNode.getTask().getParent().getChildrenTask().get( taskIndex +
			// 1 ) );
			// }
			// }
			// else
			// {
			// int taskIndex =
			// controller.getUserContainer().getContainer().getRootTasks()
			// .indexOf( taskNode.getTask() );
			// if ( taskIndex !=
			// controller.getUserContainer().getContainer().getRootTasks().size()
			// - 1 )
			// {
			// controller.getUserContainer().getContainer().getRootTasks().get(
			// taskIndex + 1 )
			// .setPreviousTask( taskNode.getTask().getPreviousTask() );
			// tasksToUpdate
			// .add(
			// controller.getUserContainer().getContainer().getRootTasks().get(
			// taskIndex + 1 ) );
			// }
			// }
			// taskNode.getTask().setPreviousTask( null );

			// Sorting goes as
			// - If dropped ON a node, we append it as a child
			// - If dropped on the TOP part of a node, we move/add it before
			// the node
			// - If dropped on the BOTTOM part of a node, we move/add it
			// after the node

			if ( location == VerticalDropLocation.MIDDLE )
			{
				TaskNode parentNode = (TaskNode) targetItemId;

				List<TaskNode> parentList = (List<TaskNode>) container.getChildren( parentNode );

				controller.move( taskNode, parentNode, parentList.get( parentList.size() - 1 ) );
				// if ( container.setParent( sourceItemId, targetItemId )/*
				// * &&
				// * container
				// * .
				// * hasChildren
				// * (
				// * targetItemId
				// * )
				// */)
				// {
				// // Mise � jour pr�c�dence
				// TT_Task newParentTask = ( (TaskNode) targetItemId
				// ).getTask();
				// if ( !newParentTask.getChildrenTask().isEmpty() )
				// {
				// newParentTask.getChildrenTask().get( 0 ).setPreviousTask(
				// taskNode.getTask() );
				// tasksToUpdate.add( newParentTask.getChildrenTask().get( 0 )
				// );
				// }
				//
				// // move first in the container
				// container.moveAfterSibling( sourceItemId, null );
				// controller.setTaskParent( ( (TaskNode) sourceItemId
				// ).getTask(),
				// ( (TaskNode) targetItemId ).getTask() );
				// }
				// tree.expandItem( targetItemId );
			}
			else if ( location == VerticalDropLocation.TOP )
			{
				TaskNode parentNode = (TaskNode) container.getParent( targetItemId );

				List<TaskNode> parentList = null;
				if ( parentNode != null )
				{
					parentList = (List<TaskNode>) container.getChildren( parentNode );
				}
				else
				{
					parentList = (List<TaskNode>) container.rootItemIds();
				}

				int targetNodeIndex = parentList.indexOf( targetItemId );
				if ( targetNodeIndex == 0 )
				{
					controller.move( taskNode, parentNode, null );
				}
				else
				{
					controller.move( taskNode, parentNode, parentList.get( targetNodeIndex - 1 ) );
				}
				// Object parentId = container.getParent( targetItemId );
				// if ( container.setParent( sourceItemId, parentId ) )
				// {
				// TT_Task sourceTask = ( (TaskNode) sourceItemId ).getTask();
				// TT_Task targetTask = ( (TaskNode) targetItemId ).getTask();
				// sourceTask.setPreviousTask( targetTask.getPreviousTask() );
				// targetTask.setPreviousTask( sourceTask );
				// tasksToUpdate.add( targetTask );
				//
				// // reorder only the two items, moving source above target
				// container.moveAfterSibling( sourceItemId, targetItemId );
				// container.moveAfterSibling( targetItemId, sourceItemId );
				// if ( parentId == null )
				// {
				// controller.setTaskParent( ( (TaskNode) sourceItemId
				// ).getTask(), null );
				// }
				// else
				// {
				// controller.setTaskParent( ( (TaskNode) sourceItemId
				// ).getTask(),
				// ( (TaskNode) parentId ).getTask() );
				// }
				// }
			}
			else if ( location == VerticalDropLocation.BOTTOM )
			{
				TaskNode parentNode = (TaskNode) container.getParent( targetItemId );
				TaskNode previousNode = (TaskNode) targetItemId;

				controller.move( taskNode, parentNode, previousNode );
				//
				// Object parentId = container.getParent( targetItemId );
				// if ( container.setParent( sourceItemId, parentId ) )
				// {
				// TT_Task sourceTask = ( (TaskNode) sourceItemId ).getTask();
				// TT_Task targetTask = ( (TaskNode) targetItemId ).getTask();
				// if ( parentId == null )
				// {
				// int targetTaskIndex =
				// controller.getUserContainer().getContainer().getRootTasks()
				// .indexOf( targetTask );
				// if ( targetTaskIndex > 0 )
				// {
				// sourceTask.setPreviousTask( targetTask );
				// if ( targetTaskIndex !=
				// controller.getUserContainer().getContainer().getRootTasks().size()
				// - 1 )
				// {
				// controller.getUserContainer().getContainer().getRootTasks().get(
				// targetTaskIndex + 1 )
				// .setPreviousTask( sourceTask );
				// tasksToUpdate.add(
				// controller.getUserContainer().getContainer().getRootTasks()
				// .get( targetTaskIndex + 1 ) );
				// }
				// }
				// }
				// else
				// {
				// int targetTaskIndex =
				// targetTask.getParent().getChildrenTask().indexOf( targetTask
				// );
				// if ( targetTaskIndex > 0 )
				// {
				// sourceTask.setPreviousTask( targetTask );
				// if ( targetTaskIndex !=
				// targetTask.getParent().getChildrenTask().size() - 1 )
				// {
				// targetTask.getParent().getChildrenTask().get( targetTaskIndex
				// + 1 )
				// .setPreviousTask( sourceTask );
				// tasksToUpdate.add(
				// targetTask.getParent().getChildrenTask().get( targetTaskIndex
				// + 1 ) );
				// }
				// }
				// }
				//
				// container.moveAfterSibling( sourceItemId, targetItemId );
				// if ( parentId == null )
				// {
				// controller.setTaskParent( ( (TaskNode) sourceItemId
				// ).getTask(), null );
				// }
				// else
				// {
				// controller.setTaskParent( ( (TaskNode) sourceItemId
				// ).getTask(),
				// ( (TaskNode) parentId ).getTask() );
				// }
				// }
			}
		}

		private final Tree									tree;

		private final TreeTaskerWebApplicationController	controller;
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	// PUBLIC
	public TTWtree(
		TreeTaskerWebApplicationController controller )
	{
		this.controller = controller;
		init();
	}

	/**
	 * Adds <code>node</code> as a root node.
	 * 
	 * @param node
	 */
	public void addNode(
		A_NavigationNode node ) {
		navigationTree.addItem( node );
		refreshNodeCaption( node );
		refreshNodeIcon( (TaskNode) node );
	}

	/**
	 * Adds <code>childNode</code> to <code>parentNode</code>.
	 * <code>parentNode</code> must already be in the tree.
	 * 
	 * @param childNode
	 * @param parentNode
	 */
	public void addNode(
		A_NavigationNode childNode,
		A_NavigationNode parentNode ) {
		addNode( childNode );
		navigationTree.setParent( childNode, parentNode );
	}

	/**
	 * Expands a <code>node</code>
	 * 
	 * @param node
	 */
	public void expandNode(
		A_NavigationNode node ) {
		navigationTree.expandItem( node );
	}

	/**
	 * Expands all the children recursively starting from <code>node</code>.
	 * 
	 * @param node
	 */
	public void expandNodeRecursively(
		A_NavigationNode node ) {
		navigationTree.expandItemsRecursively( node );
	}

	/**
	 * Return all the children from <code>node</code>
	 * 
	 * @param node
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	public Collection<A_NavigationNode> getChildren(
		A_NavigationNode node ) {
		if ( node == null )
		{
			return (Collection<A_NavigationNode>) navigationTree.rootItemIds();
		}
		return (Collection<A_NavigationNode>) navigationTree.getChildren( node );
	}

	/**
	 * 
	 * @return the node that is currently displaying its content.
	 */
	public A_NavigationNode getCurrentNode() {
		return currentNode;
	}

	/**
	 * 
	 * @param node
	 * @return the parent node of the specified node
	 */
	public A_NavigationNode getParent(
		A_NavigationNode node ) {
		return (A_NavigationNode) navigationTree.getParent( node );
	}

	/**
	 * 
	 * @return a set of the selected nodes
	 */
	public Set<?> getValue() {
		return (Set<?>) navigationTree.getValue();
	}

	/**
	 * Moves <code>node</code> immediately after <code>siblingNode</code>. The
	 * two nodes must have the same parent
	 * 
	 * @param node
	 * @param siblingNode
	 */
	public void moveAfterSiblingNode(
		A_NavigationNode node,
		A_NavigationNode parentNode,
		A_NavigationNode siblingNode ) {
		HierarchicalContainer container = (HierarchicalContainer) navigationTree.getContainerDataSource();
		if ( container.setParent( node, parentNode ) )
		{
			container.moveAfterSibling( node, siblingNode );
		}
	}

	/**
	 * Refreshes <code>node</code> caption in the menu.
	 * 
	 * @param node
	 */
	public void refreshNodeCaption(
		A_NavigationNode node ) {
		if ( navigationTree.containsId( node ) )
		{
			navigationTree.setItemCaption( node, node.getCaption() );
		}
	}

	/**
	 * Refreshes <code>node</code> icon in the menu.
	 * 
	 * @param node
	 */
	public void refreshNodeIcon(
		TaskNode node ) {
		if ( navigationTree.containsId( node ) )
		{
			if ( node.getTask().getStatus() == TT_Task.DONE )
			{
				navigationTree.setItemIcon( node, new ThemeResource( "icons/tick.png" ) );
			}
			else
			{
				navigationTree.setItemIcon( node, new ThemeResource( "icons/bullet.png" ) );
			}
		}
	}

	/**
	 * Clears the tree content.
	 */
	public void removeAllNodes() {
		navigationTree.removeAllItems();
		currentNode = null;
	}

	@SuppressWarnings( "unchecked" )
	/**
	 * Removes <code>node</code> and all its children from the tree.
	 * @param node
	 */
	public void removeNodeRecursively(
		A_NavigationNode node ) {
		if ( node != null )
		{
			if ( navigationTree.hasChildren( node ) )
			{
				Collection<A_NavigationNode> childNodes = new ArrayList<A_NavigationNode>(
					(Collection<A_NavigationNode>) navigationTree.getChildren( node ) );
				for ( A_NavigationNode childNode : childNodes )
				{
					removeNodeRecursively( childNode );
				}
			}
			removeNode( node );
		}
	}

	/**
	 * Selects <code>node</code> in the tree. Listeners are triggered.
	 * 
	 * @param node
	 */
	public void select(
		A_NavigationNode node ) {
		select( node, true );
	}

	/**
	 * Selects <code>node</code> in the tree.
	 * 
	 * @param node
	 * @param triggerListeners
	 *            whether the selection triggers listeners or not.
	 */
	public void select(
		A_NavigationNode node,
		boolean triggerListeners ) {
		if ( triggerListeners )
		{
			navigationTree.select( node );
		}
		else
		{
			navigationTree.removeListener( changeListener );
			navigationTree.select( node );
			navigationTree.addListener( changeListener );
		}
	}

	/**
	 * Unselects all the nodes.
	 */
	public void unselectAll() {
		currentNode = null;
		navigationTree.setValue( Collections.EMPTY_SET );
	}

	/**
	 * Unselects the node
	 */
	public void unselectNode() {
		if ( currentNode != null )
		{
			currentNode.onExit();
			navigationTree.unselect( currentNode );
			currentNode = null;
		}
	}

	@SuppressWarnings( "unchecked" )
	/**
	 * Initializes listeners to control the tree behaviors.
	 */
	protected void initListeners() {

		// This listener updates the content view depending on what is selected
		// on the tree.
		changeListener = new Property.ValueChangeListener()
		{
			/**
			 * 
			 */
			private static final long	serialVersionUID	= 1L;

			@Override
			public void valueChange(
				ValueChangeEvent event ) {
				Set<A_NavigationNode> values = (Set<A_NavigationNode>) event.getProperty().getValue();

				if ( values.size() == 1 )
				{
					A_NavigationNode node = values.iterator().next();

					if ( node != currentNode )
					{
						if ( currentNode != null )
						{
							currentNode.onExit();
						}
						currentNode = node;
						node.onEnter();
					}
				}
				else if ( values.isEmpty() )
				{
					if ( currentNode != null )
					{
						currentNode.onExit();
					}
					currentNode = null;
				}
			}
		};
		// Right-clic validation
		navigationTree.addListener( new ItemClickEvent.ItemClickListener()
		{
			private static final long	serialVersionUID	= 1L;

			@Override
			public void itemClick(
				ItemClickEvent event ) {
				if ( event.getButton() == ClickEvent.BUTTON_RIGHT )
				{
					controller.validTask( (A_NavigationNode) event.getItemId() );
				}
			}
		} );
		navigationTree.addListener( changeListener );
		navigationTree.setImmediate( true );
	}

	private void init() {
		navigationTree = new Tree();
		HierarchicalContainer container = new HierarchicalContainer();
		navigationTree.setContainerDataSource( container );
		navigationTree.setDragMode( Tree.TreeDragMode.NODE );
		navigationTree.setDropHandler( new TreeSortDropHandler( navigationTree, controller ) );
		navigationTree.setMultiSelect( true );
		currentNode = null;

		setSizeUndefined();
		addComponent( navigationTree );

		initListeners();
	}

	/**
	 * Removes <code>node</code> from the tree. Does not remove any children the
	 * node might have.
	 * 
	 * @param node
	 */
	private void removeNode(
		A_NavigationNode node ) {
		if ( navigationTree.containsId( node ) )
		{
			navigationTree.removeItem( node );
		}
	}

	// </editor-fold>
	// PROTECTED
	protected TreeTaskerWebApplicationController	controller;

	protected Tree									navigationTree;

	// PRIVATE
	private Property.ValueChangeListener			changeListener;

	private A_NavigationNode						currentNode;
}
