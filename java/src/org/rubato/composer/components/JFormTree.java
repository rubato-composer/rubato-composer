/*
 * Copyright (C) 2005 Gérard Milmeister
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of version 2 of the GNU General Public
 * License as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package org.rubato.composer.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.*;
import javax.swing.tree.*;

import org.rubato.composer.icons.Icons;
import org.rubato.math.module.Module;
import org.rubato.math.yoneda.*;


/**
 * 
 * @author Gérard Milmeister
 */
public class JFormTree
        extends JPanel
        implements TreeExpansionListener, TreeSelectionListener {

    
    public JFormTree() {
        this.form = null;
        this.withModules = false;
        setLayout(new BorderLayout());
        top = null;
        createTree();
    }
    
    
    public JFormTree(Form form, boolean withModules) {
        this.form = form;
        this.withModules = withModules;
        setLayout(new BorderLayout());
        top = createFormNode(null, form, 0);
        createTree();
    }
    
    
    private void createTree() {
        treeModel = new DefaultTreeModel(top);
        formTree = new JTree(treeModel);
        formTree.collapseRow(0);
        formTree.addTreeExpansionListener(this);
        formTree.addTreeSelectionListener(this);
        formTree.setCellRenderer(new FormCellRenderer());
        add(formTree, BorderLayout.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }
    
    
    public void setForm(Form form) {
        this.form = form;
        this.selectedForm = null;
        this.selectedModule = null;
        this.path = null;
        top = (form == null)?null:createFormNode(null, form, 0);
        treeModel.setRoot(top);
        treeModel.reload();
        formTree.collapseRow(0);
    }
    
    
    @SuppressWarnings("nls")
    protected String getFormString(String label, Form f) {
        StringBuilder buf = new StringBuilder(30);
        buf.append("<html>");
        if (label != null) {
            buf.append("["+label+"] ");
        }
        buf.append("<b>");
        buf.append(f.getNameString());
        buf.append("</b>");
        buf.append(": ");
        buf.append("<i>");
        buf.append(f.getTypeString());
        buf.append("</i>");
        buf.append("</html>");
        return buf.toString();
    }
    
    
    private DefaultMutableTreeNode createFormNode(String label, Form f, int pos) {
        DefaultMutableTreeNode node = null;
        node = new DefaultMutableTreeNode(new FormNode(label, f, pos, false));
        if (withModules || !(f instanceof SimpleForm)) {
            node.add(new DefaultMutableTreeNode());
        }
        return node;
    }
    
    
    private DefaultMutableTreeNode createModuleNode(Module module, int pos) {
        DefaultMutableTreeNode node = null;
        node = new DefaultMutableTreeNode(new ModuleNode(module, pos, false));
        return node;
    }
    
    
    private void expandPath(TreePath p) {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)p.getLastPathComponent();
        Node node = (Node)treeNode.getUserObject();
        if (!node.expanded) {
            if (node instanceof FormNode) {
                FormNode formNode = (FormNode)node;
                Form f = formNode.getForm();
                for (int i = 0; i < treeNode.getChildCount(); i++) {
                    treeModel.removeNodeFromParent((DefaultMutableTreeNode)treeNode.getChildAt(0));
                }
                if (f instanceof SimpleForm) {
                    SimpleForm simpleForm = (SimpleForm)f;
                    Module module = simpleForm.getModule();
                    for (int i = 0; i < module.getDimension(); i++) {
                        treeModel.insertNodeInto(createModuleNode(module.getComponentModule(i), i), treeNode, i);
                    }
                }
                if (f instanceof LimitForm) {
                    LimitForm limitForm = (LimitForm)f;
                    for (int i = 0; i < limitForm.getFormCount(); i++) {
                        Form subForm = limitForm.getForm(i);
                        String label = limitForm.indexToLabel(i);
                        treeModel.insertNodeInto(createFormNode(label, subForm, i), treeNode, i);
                    }
                }
                else if (f instanceof ColimitForm) {
                    ColimitForm colimitForm = (ColimitForm)f;            
                    for (int i = 0; i < colimitForm.getFormCount(); i++) {
                        Form subForm = colimitForm.getForm(i);
                        String label = colimitForm.indexToLabel(i);
                        treeModel.insertNodeInto(createFormNode(label, subForm, i), treeNode, i);
                    }
                }
                else if (f instanceof PowerForm) {
                    PowerForm powerForm = (PowerForm)f;
                    treeModel.insertNodeInto(createFormNode(null, powerForm.getForm(), 0), treeNode, 0);
                }
                else if (form instanceof ListForm) {
                    ListForm listForm = (ListForm)f;            
                    treeModel.insertNodeInto(createFormNode(null, listForm.getForm(), 0), treeNode, 0);
                }
                formNode.expanded = true;
                treeModel.reload(treeNode);
                formTree.expandPath(p);
            }
        }
    }

    
    public int[] getSelectedPath() {
        return path;
    }
    
    
    public void setSelectedPath(int[] path) {
        if (path == null) {
            return;
        }
        TreeNode node = null;
        TreeNode[] nodePath = null;
        TreePath treePath = null;
        for (int j = 1; j <= path.length+1; j++) {
            node = (TreeNode)treeModel.getRoot();
            nodePath = new TreeNode[j];
            nodePath[0] = node;
            for (int i = 1; i < j; i++) {
                 node = node.getChildAt(path[i-1]);
                 nodePath[i] = node;
            }
            treePath = new TreePath(nodePath);
            formTree.expandPath(treePath);
        }
        formTree.setSelectionPath(treePath);
    }
    
    
    public Form getSelectedForm() {
        return selectedForm; 
    }
    
    
    public Module getSelectedModule() {
        return selectedModule;
    }
    
    
    public void valueChanged(TreeSelectionEvent e) {
        TreePath treePath = formTree.getSelectionPath();        
        if (treePath != null) {
            int l = treePath.getPathCount();
            if (l > 0) {
                path = new int[l-1];
                for (int i = 1; i < l; i++) {
                    DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getPathComponent(i);
                    Node node = (Node)treeNode.getUserObject();
                    path[i-1] = node.pos;                    
                }
            }
            else {
                path = null;
            }
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treePath.getLastPathComponent();
            Node node = (Node)treeNode.getUserObject();
            selectedForm = node.getForm();
            selectedModule = node.getModule();
        }
        else {
            path = null;
            selectedForm = null;
            selectedModule = null;
        }
        fireActionEvent();
    }
    
    
    public void treeExpanded(TreeExpansionEvent event) {
        expandPath(event.getPath());
    }


    public void treeCollapsed(TreeExpansionEvent event) { /* do nothing */ }

    
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }


    protected void fireActionEvent() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                if (actionEvent == null) {
                    actionEvent = new ActionEvent(this, 0, ""); //$NON-NLS-1$
                }
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }
    
    protected abstract class Node {
        public abstract String toString();
        public abstract Form getForm();
        public abstract Module getModule();
        public boolean expanded;
        public int pos;    
    }
    
    protected class FormNode extends Node {
        public FormNode(String label, Form form, int pos, boolean expanded) {
            this.f = form;
            this.expanded = expanded;
            this.pos = pos;
            this.label = label;
        }
        public String toString() {
            return getFormString(label, f);
        }
        public Form getForm() {
            return f;
        }
        public Module getModule() {
            return null;
        }
        public Form    f;
        public String  label;
    }

    private class ModuleNode extends Node {
        public ModuleNode(Module module, int pos, boolean expanded) {
            this.module = module;
            this.expanded = expanded;
            this.pos = pos;
        }
        public String toString() {
            return ""+pos+": "+module.toVisualString(); //$NON-NLS-1$ //$NON-NLS-2$
        }
        public Form getForm() {
            return null;
        }
        public Module getModule() {
            return module;
        }
        public Module  module;
    }

    class FormCellRenderer extends DefaultTreeCellRenderer {
        
        public FormCellRenderer() { /* do nothing */ }

        public Component getTreeCellRendererComponent(
                            JTree tree,
                            Object value,
                            boolean sel,
                            boolean expanded,
                            boolean leaf,
                            int row,
                            boolean focus) {
            super.getTreeCellRendererComponent(
                            tree, value, sel,
                            expanded, leaf, row,
                            focus);
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)value;
            Node node = (Node)treeNode.getUserObject();
            if (node != null) {
                Form f = node.getForm();
                if (f != null) {
                    if (f instanceof LimitForm) {
                        setIcon(Icons.limitIcon);
                    }
                    else if (f instanceof ColimitForm) {
                        setIcon(Icons.colimitIcon);
                    }
                    else if (f instanceof PowerForm) {
                        setIcon(Icons.powerIcon);
                    }
                    else if (f instanceof ListForm) {
                        setIcon(Icons.listIcon);
                    }
                    else if (f instanceof SimpleForm) {
                        setIcon(Icons.simpleIcon);
                    }
                }
            }
            return this;
        }
    }
    
    
    protected Form  form;
    private   JTree formTree;    
    
    private boolean withModules;
    
    private DefaultMutableTreeNode top;
    private DefaultTreeModel       treeModel;
    
    private int[]  path = null;
    private Form   selectedForm   = null;
    private Module selectedModule = null;
    
    private EventListenerList listenerList = new EventListenerList();
    private ActionEvent actionEvent = null;
}
