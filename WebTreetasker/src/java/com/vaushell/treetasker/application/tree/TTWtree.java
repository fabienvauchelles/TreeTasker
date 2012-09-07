/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.tree;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import com.vaushell.treetasker.application.tree.node.A_NavigationNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public class TTWtree
        extends VerticalLayout
        implements Serializable
{
    // PUBLIC
    public TTWtree( TreeTaskerWebApplicationController controller )
    {
        this.controller = controller;
        init();
    }

    /**
     * Ajoute un noeud à la racine
     * @param node 
     */
    public void addNode( A_NavigationNode node )
    {
        navigationTree.addItem( node );
        refreshNodeCaption( node );
    }

    public void refreshNodeCaption( A_NavigationNode node )
    {
        if ( navigationTree.containsId( node ) )
        {
            navigationTree.setItemCaption( node ,
                                           node.getCaption() );
        }
    }

    public void unselectNode()
    {
        if ( currentNode != null )
        {
            currentNode.onExit();
            navigationTree.unselect( currentNode );
            currentNode = null;
        }
    }

    /**
     * Ajoute childNode à parentNode. parentNode doit être contenu dans le menu.
     * @param childNode
     * @param parentNode 
     */
    public void addNode( A_NavigationNode childNode ,
                         A_NavigationNode parentNode )
    {
        addNode( childNode );
        navigationTree.setParent( childNode ,
                                  parentNode );
    }

    public void removeNode( A_NavigationNode node )
    {
        navigationTree.removeItem( node );
    }

    public void removeNodeRecursively( A_NavigationNode node )
    {
        if ( node != null )
        {
            Collection<A_NavigationNode> childNodes = new ArrayList<A_NavigationNode>( (Collection<A_NavigationNode>) navigationTree.
                    getChildren( node ) );
            for ( A_NavigationNode childNode : childNodes )
            {
                removeNodeRecursively( childNode );
            }
            removeNode( node );
        }
    }

    public A_NavigationNode getParent( A_NavigationNode node )
    {
        return (A_NavigationNode) navigationTree.getParent( node );
    }

    public Collection<A_NavigationNode> getChildren( A_NavigationNode node )
    {
        return (Collection<A_NavigationNode>) navigationTree.getChildren( node );
    }

    public A_NavigationNode getCurrentNode()
    {
        return currentNode;
    }

    public void select( Object itemId )
    {
        select( itemId ,
                true );
    }

    public void select( Object itemId ,
                        boolean triggerListeners )
    {
        if ( triggerListeners )
        {
            navigationTree.select( itemId );
        }
        else
        {
            navigationTree.removeListener( changeListener );
            navigationTree.select( itemId );
            navigationTree.addListener( changeListener );
        }
    }
    // </editor-fold>
    // PROTECTED
    protected TreeTaskerWebApplicationController controller;
    protected Tree navigationTree;
    // PRIVATE
    private Property.ValueChangeListener changeListener;
    private A_NavigationNode currentNode;

    private void init()
    {
        this.navigationTree = new Tree();
        this.currentNode = null;

        setSizeUndefined();
        addComponent( navigationTree );

        initListeners();
    }

    protected void initListeners()
    {
        this.changeListener = new Property.ValueChangeListener()
        {
            public void valueChange( ValueChangeEvent event )
            {
                A_NavigationNode node = (A_NavigationNode) event.getProperty().getValue();

                if ( node == null )
                {
                    return;
                }

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
        };
        navigationTree.addListener( changeListener );
        this.navigationTree.setImmediate( true );
    }
}
