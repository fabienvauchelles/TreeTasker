/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaushell.treetasker.application.tree.node;

import com.vaushell.treetasker.application.TreeTaskerWebApplicationController;
import java.io.Serializable;

/**
 *
 * @author VAUSHELL - Frederic PEAK <fred@vaushell.com>
 */
public abstract class A_NavigationNode
        implements Serializable
{
    // PUBLIC
    public A_NavigationNode( TreeTaskerWebApplicationController controller )
    {
        this.controller = controller;
    }

    public TreeTaskerWebApplicationController getController()
    {
        return controller;
    }

    /**
     * Méthode appelée à l'entrée du noeud
     */
    public abstract void onEnter();

    /**
     * Méthode appelée à la sortie du noeud
     */
    public abstract void onExit();

    /**
     * 
     * @return La description du noeud qui sera affiché dans le menu
     */
    public abstract String getCaption();
    // PROTECTED
    protected TreeTaskerWebApplicationController controller;
}
