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

package org.rubato.composer.network;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.rubato.composer.JComposer;
import org.rubato.composer.JNetworkList;
import org.rubato.composer.rubette.JRubette;
import org.rubato.xml.XMLWriter;

/**
 * @author Gérard Milmeister
 */
public class JNetworkContainer extends JPanel {

    public JNetworkContainer(JComposer jcomposer, JNetworkList networkList) {
        this.jcomposer = jcomposer;
        this.networkList = networkList;
        setLayout(new BorderLayout());
        jnetworks = new ArrayList<JNetwork>();
        networkTabs = new JTabbedPane();
        add(networkTabs, BorderLayout.CENTER);
    }
    
    
    public void clear() {
        for (JNetwork network : jnetworks) {
            network.dispose();
        }
        networkTabs.removeAll();
        jnetworks.clear();
        networkList.clear();
        networkCounter = 1;
    }
    
    
    public boolean isEmpty() {
        return jnetworks.isEmpty();
    }
    
    
    public String newName() {
        return "Untitled #"+(networkCounter++); //$NON-NLS-1$
    }

    
    public JNetwork newJNetwork() {
        String name = newName();
        JNetwork jnetwork = new JNetwork(jcomposer);
        jnetwork.setModel(new NetworkModel(jnetwork, name));
        addJNetwork(jnetwork, name);
        showJNetwork(jnetwork);
        return jnetwork;
    }
    

    public void addJNetwork(JNetwork jnetwork, String name) {
        JScrollPane scrollPane = new JScrollPane(jnetwork);
        jnetworks.add(jnetwork);
        networkTabs.addTab(name, scrollPane);
        networkList.addNetwork(jnetwork);
    }
    
    
    public void addJMacroRubetteView(NetworkModel networkModel, JNetwork jnetwork) {
        // check if this network model is already shown
        for (JNetwork n : jnetworks) {
            if (n.getModel() == networkModel) {
                showJNetwork(n);
                return;
            }
        }
        // create a new JNetwork and show it
        JNetwork newNetwork = networkModel.createJMacroRubetteView(jcomposer, jnetwork);
        addJNetwork(newNetwork, newNetwork.getName());
        showJNetwork(newNetwork);
    }
    

    public void removeJNetwork(JNetwork jnetwork) {
        networkTabs.removeTabAt(jnetworks.indexOf(jnetwork));
        jnetworks.remove(jnetwork);
        networkList.removeNetwork(jnetwork);
        jnetwork.dispose();
    }
    

    public void removeJNetworkForModel(NetworkModel networkModel) {
        JNetwork jnetwork = getJNetworkForModel(networkModel);
        if (jnetwork != null) {
            removeJNetwork(jnetwork);
        }
    }
    
    
    private JNetwork getJNetworkForModel(NetworkModel model) {
        for (JNetwork jnetwork : jnetworks) {
            if (jnetwork.getModel() == model) {
                return jnetwork;
            }
        }
        return null;
    }
    

    public void showJNetwork(JNetwork jnetwork) {
        if (jnetwork != null) {
            int i = jnetworks.indexOf(jnetwork);
            if (i >= 0) {
                networkTabs.setSelectedIndex(i);
            }
        }
    }

    
    public void renameJNetwork(JNetwork jnetwork, String name) {
        // check if name has already been used
        for (JNetwork n : jnetworks) {
            if (n.getModel().getName().equals(name) && n != jnetwork) {
                jcomposer.showErrorDialog(NAMEUSED_ERROR, name);
                return;
            }
        }
        int i = jnetworks.indexOf(jnetwork);
        if (i >= 0) {
            jnetworks.get(i).getModel().setName(name);
            refresh();
        }
    }
    
    
    public void refresh() {
        int i = 0;
        for (JNetwork network : jnetworks) {
            String name = network.getName();
            networkTabs.setTitleAt(i++, name);
        }
        networkList.refresh();
    }
    
    
    public JNetwork getCurrentJNetwork() {
        int i = networkTabs.getSelectedIndex();
        if (i >= 0) {
            return jnetworks.get(i);
        }
        else {
            return null;
        }
    }
    
    
    public void computeNetworkTree() {
        JNetwork jnetwork = getCurrentJNetwork();
        if (jnetwork != null) {
            jnetwork.computeDependencyTree();
        }
    }

    
    public void removeJRubette(JRubette jrubette) {
        JNetwork jnetwork = getCurrentJNetwork();
        jnetwork.removeRubette(jrubette);
    }
    
    
    public void toXML(XMLWriter writer) {
        for (JNetwork network : jnetworks) {
            network.toXML(writer);
        }
    }

    
    // Link back to the containing JComposer
    private JComposer    jcomposer;
    private JNetworkList networkList;
    
    private JTabbedPane  networkTabs;
    private ArrayList<JNetwork> jnetworks;
    private int          networkCounter = 1;
    
    private final static String NAMEUSED_ERROR = Messages.getString("JNetworkContainer.nameused"); //$NON-NLS-1$
}
